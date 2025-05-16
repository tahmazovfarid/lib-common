package az.ailab.lib.common.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import org.springframework.core.env.Environment;

/**
 * A DataSource-Proxy listener that outputs SQL statements along with execution metrics
 * and optional inline parameters. Supports both DQL (SELECT) and DML (INSERT/UPDATE/DELETE).
 * <p>
 * Parameter inlining is controlled by Spring profiles and properties:
 * <ul>
 *   <li>Enabled only when running in 'dev' or 'local' profiles.</li>
 *   <li>Requires 'spring.jpa.sql.logging.enabled=true' to enable logging.</li>
 *   <li>Requires 'spring.jpa.sql.logging.show-parameters=true' to inline parameter values.</li>
 * </ul>
 *
 * @since 1.2.1
 * @author tahmazovfarid
 */
@Slf4j
public class QueryExecutionListenerImpl implements QueryExecutionListener {

    /**
     * Flag indicating whether SQL parameters should be inlined into the logged query.
     */
    private final boolean paramLogEnabled;

    /**
     * Constructs the listener and configures parameter logging based on the active environment.
     * <p>
     * Logs a warning if SQL logging or parameter inlining is enabled outside of 'dev'/'local'.
     *
     * @param env the Spring Environment from which to read profiles and properties
     */
    public QueryExecutionListenerImpl(Environment env) {
        // Determine if running in dev or local profile
        String profiles = env.getProperty("spring.profiles.active", "");
        boolean isDevOrLocal = Stream.of(profiles.split(","))
                .map(String::trim)
                .anyMatch(p -> p.equalsIgnoreCase("dev") || p.equalsIgnoreCase("local"));

        // Read SQL logging flags
        boolean paramLogging = env.getProperty("spring.jpa.sql-logging.show-parameters", Boolean.class, false);

        // Warn if logging enabled outside dev/local
        if (!isDevOrLocal) {
            if (paramLogging) {
                log.warn("Parameter logging is ENABLED in non-dev environment [{}]; ignoring inline parameters for safety.",
                        profiles);
                paramLogging = false;
            }
            log.warn("SQL logging is ENABLED in non-dev environment [{}]; consider disabling before production.",
                    profiles);
        }

        // Only enable inline params in dev/local when both flags are set
        this.paramLogEnabled = isDevOrLocal && paramLogging;
    }

    /**
     * No-op implementation. Intentionally left blank.
     *
     * @param execInfo information about the SQL execution context
     * @param queryInfoList list of QueryInfo objects representing each SQL executed
     */
    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        // intentionally no operation before query execution
    }

    /**
     * Called after each SQL execution. Prints the formatted query, execution time, batch size,
     * and affected row or column count in a single line.
     *
     * @param execInfo information about the execution (time, result, etc.)
     * @param queryInfoList list of QueryInfo for each SQL statement executed
     */
    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        String rawSql = getRawSql(queryInfoList);
        // Always print raw SQL, then metrics
        String query = (paramLogEnabled && rawSql.contains("?"))
                ? formatSqlWithParams(rawSql, queryInfoList)
                : normalize(rawSql);

        long timeMs = execInfo.getElapsedTime();
        int batchSize = execInfo.getBatchSize();
        Object result = execInfo.getResult();
        String bs = batchSize > 1 ? " batchSize=" + batchSize : "";

        if (result instanceof ResultSet) {
            int cols = 0;
            try {
                cols = ((ResultSet) result).getMetaData().getColumnCount();
            } catch (SQLException ignored) {
                // ignore metadata failure
            }
            System.out.printf("Query: %s | cols=%d%s time=%dms%n", query, cols, bs, timeMs);
        } else {
            int rowsAffected = extractRowCount(result, batchSize);
            System.out.printf("Query: %s | rowsAffected=%d%s time=%dms%n", query, rowsAffected, bs, timeMs);
        }
    }

    /**
     * Extracts the number of affected rows from the result object.
     *
     * @param result the execution result (int[] for batch, Integer for single update)
     * @param batchSize the configured batch size
     * @return computed row count
     */
    private int extractRowCount(Object result, int batchSize) {
        if (result instanceof int[] counts) {
            return batchSize > 0 ? batchSize : Arrays.stream(counts).filter(c -> c > 0).sum();
        }
        if (result instanceof Integer single) {
            return single;
        }
        return 0;
    }

    /**
     * Inlines SQL parameters into the query string. Supports INSERT batching and generic DML/DQL.
     *
     * @param rawSql the original SQL with '?' placeholders
     * @param queryInfoList list of QueryInfo containing parameter sets
     * @return SQL string with parameters inlined
     */
    private String formatSqlWithParams(String rawSql, List<QueryInfo> queryInfoList) {
        String sqlLc = rawSql.toLowerCase();
        // INSERT batching
        if (sqlLc.startsWith("insert") && sqlLc.contains("values")) {
            int idx = sqlLc.indexOf("values");
            String prefix = rawSql.substring(0, idx + "values".length());
            List<String> tuples = new ArrayList<>();
            for (QueryInfo qi : queryInfoList) {
                for (List<ParameterSetOperation> paramOps : qi.getParametersList()) {
                    if (paramOps.isEmpty()) continue;
                    String tuple = paramOps.stream()
                            .map(p -> formatValue(p.getArgs()[1]))
                            .collect(Collectors.joining(", ", "(", ")"));
                    tuples.add(tuple);
                }
            }
            if (!tuples.isEmpty()) {
                return normalize(prefix) + " " + String.join(", ", tuples) + ";" ;
            }
        }
        // Generic inlining for other statements
        List<String> filled = new ArrayList<>();
        for (QueryInfo qi : queryInfoList) {
            for (List<ParameterSetOperation> paramOps : qi.getParametersList()) {
                if (paramOps.isEmpty()) continue;
                String filledSql = rawSql;
                for (ParameterSetOperation p : paramOps) {
                    Object val = p.getArgs()[1];
                    filledSql = filledSql.replaceFirst("\\?", Matcher.quoteReplacement(formatValue(val)));
                }
                filled.add(filledSql);
            }
        }
        if (!filled.isEmpty()) {
            return normalize(String.join(" ; ", filled)) + ";";
        }
        // no params to inline
        return normalize(rawSql);
    }

    /**
     * Formats a parameter value for embedding into SQL.
     * Numbers are returned directly; strings are single-quoted with internal quotes escaped.
     *
     * @param val the parameter value
     * @return formatted SQL literal
     */
    private String formatValue(Object val) {
        if (val == null) return "null";
        if (val instanceof Number) return val.toString();
        String str = String.valueOf(val).replace("'", "\\'");
        return "'" + str + "'";
    }

    /**
     * Concatenates raw SQL queries from a list of QueryInfo.
     *
     * @param queryInfoList list of QueryInfo objects
     * @return concatenated SQL string
     */
    private String getRawSql(List<QueryInfo> queryInfoList) {
        return queryInfoList.stream()
                .map(QueryInfo::getQuery)
                .collect(Collectors.joining(" ; "));
    }

    /**
     * Normalizes whitespace in a SQL string to a single space and trims ends.
     *
     * @param sql the raw SQL string
     * @return normalized SQL string
     */
    private String normalize(String sql) {
        return sql.trim().replaceAll("\\s+", " ");
    }

}