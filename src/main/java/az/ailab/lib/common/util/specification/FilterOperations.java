package az.ailab.lib.common.util.specification;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.FetchParent;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.Collection;
import java.util.function.Function;
import org.springframework.data.jpa.domain.Specification;

/**
 * Utility class providing common Specification-based filter operations
 * for building dynamic queries using Spring Data JPA.
 */
public final class FilterOperations {

    private FilterOperations() {

    }

    /**
     * Checks if an object's field equals a given value.
     *
     * <pre>
     * {@code
     * Specification<User> spec = equalsObject(role, root -> root.get("role"));
     * }
     * </pre>
     *
     * @param value          The value to match
     * @param fieldExtractor Function to extract the field from root
     * @return Specification with equals condition
     */
    public static <T, O> Specification<T> equalsObject(O value, Function<Root<T>, Expression<O>> fieldExtractor) {
        return value == null ? null :
                (root, query, cb) -> cb.equal(fieldExtractor.apply(root), value);
    }

    /**
     * Matches field that contains the given value (case-insensitive).
     *
     * <pre>
     * {@code
     * contains("Farid", root -> root.get("firstName"));
     * }
     * </pre>
     *
     * @param value          Substring to search for
     * @param fieldExtractor Field extractor
     * @return Specification or null if value is empty
     */
    public static <T> Specification<T> contains(String value, Function<Root<T>, Expression<String>> fieldExtractor) {
        return isEmpty(value) ? null :
                (root, query, cb)
                        -> cb.like(cb.lower(fieldExtractor.apply(root)), "%" + value.toLowerCase() + "%");
    }

    /**
     * Same as contains, but startsWith.
     */
    public static <T> Specification<T> startsWith(String value, Function<Root<T>, Expression<String>> fieldExtractor) {
        return isEmpty(value) ? null :
                (root, query, cb)
                        -> cb.like(cb.lower(fieldExtractor.apply(root)), value.toLowerCase() + "%");
    }

    /**
     * Same as contains, but endsWith.
     */
    public static <T> Specification<T> endsWith(String value, Function<Root<T>, Expression<String>> fieldExtractor) {
        return isEmpty(value) ? null :
                (root, query, cb)
                        -> cb.like(cb.lower(fieldExtractor.apply(root)), "%" + value.toLowerCase());
    }

    /**
     * Matches if string equals the given value (case-insensitive).
     */
    public static <T> Specification<T> equals(String value, Function<Root<T>, Expression<String>> fieldExtractor) {
        return isEmpty(value) ? null :
                (root, query, cb)
                        -> cb.equal(cb.lower(fieldExtractor.apply(root)), value.toLowerCase());
    }

    /**
     * Checks equality of number.
     */
    public static <T, N extends Number> Specification<T> equals(N value, Function<Root<T>, Expression<N>> fieldExtractor) {
        return value == null ? null :
                (root, query, cb)
                        -> cb.equal(fieldExtractor.apply(root), value);
    }

    /**
     * Matches if string does not equal the given value (case-insensitive).
     */
    public static <T> Specification<T> notEquals(String value, Function<Root<T>, Expression<String>> fieldExtractor) {
        return isEmpty(value) ? null :
                (root, query, cb)
                        -> cb.notEqual(cb.lower(fieldExtractor.apply(root)), value.toLowerCase());
    }

    /**
     * Checks inequality of number.
     */
    public static <T, N extends Number> Specification<T> notEquals(N value, Function<Root<T>, Expression<N>> fieldExtractor) {
        return value == null ? null :
                (root, query, cb)
                        -> cb.notEqual(fieldExtractor.apply(root), value);
    }

    /**
     * Checks if field is greater than value.
     */
    public static <T, N extends Number & Comparable<N>> Specification<T> greaterThan(N value,
                                                                                     Function<Root<T>, Expression<N>> fieldExtractor) {
        return value == null ? null :
                (root, query, cb) -> cb.greaterThan(fieldExtractor.apply(root), value);
    }

    /**
     * Checks if field is less than value.
     */
    public static <T, N extends Number & Comparable<N>> Specification<T> lessThan(N value,
                                                                                  Function<Root<T>, Expression<N>> fieldExtractor) {
        return value == null ? null :
                (root, query, cb) -> cb.lessThan(fieldExtractor.apply(root), value);
    }

    /**
     * Checks if number is between min and max.
     */
    public static <T, N extends Number & Comparable<N>> Specification<T> numberBetween(
            N min, N max, Function<Root<T>, Expression<N>> fieldExtractor) {

        if (min == null && max == null) {
            return null;
        }

        return (root, query, cb) -> {
            Expression<N> field = fieldExtractor.apply(root);

            if (min != null && max != null) {
                return cb.between(field, min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(field, min);
            } else {
                return cb.lessThanOrEqualTo(field, max);
            }
        };
    }

    // Boolean operations

    /**
     * Specification that always returns false.
     */
    public static <T> Specification<T> alwaysFalse() {
        return (root, query, cb) -> cb.equal(cb.literal(1), 0);
    }

    /**
     * Specification that always returns true.
     */
    public static <T> Specification<T> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    /**
     * Matches boolean value.
     */
    public static <T> Specification<T> isTrue(Boolean value, Function<Root<T>, Expression<Boolean>> fieldExtractor) {
        return value == null ? null : value ?
                (root, query, cb) -> cb.isTrue(fieldExtractor.apply(root)) :
                (root, query, cb) -> cb.isFalse(fieldExtractor.apply(root));
    }

    // In operations

    /**
     * Matches if value is in collection.
     */
    public static <T, V> Specification<T> in(Collection<V> values, Function<Root<T>, Expression<V>> fieldExtractor) {
        return values == null || values.isEmpty() ? null :
                (root, query, cb) -> fieldExtractor.apply(root).in(values);
    }

    /**
     * Matches if value is NOT in collection.
     */
    public static <T, V> Specification<T> notIn(Collection<V> values, Function<Root<T>, Expression<V>> fieldExtractor) {
        return values == null || values.isEmpty() ? null :
                (root, query, cb) -> cb.not(fieldExtractor.apply(root).in(values));
    }

    // Date operations

    /**
     * After or equal to date.
     */
    public static <T> Specification<T> dateAfter(LocalDate value, Function<Root<T>, Expression<LocalDate>> fieldExtractor) {
        return value == null ? null :
                (root, query, cb) -> cb.greaterThanOrEqualTo(fieldExtractor.apply(root), value);
    }

    /**
     * Before or equal to date.
     */
    public static <T> Specification<T> dateBefore(LocalDate value, Function<Root<T>, Expression<LocalDate>> fieldExtractor) {
        return value == null ? null :
                (root, query, cb) -> cb.lessThanOrEqualTo(fieldExtractor.apply(root), value);
    }

    /**
     * Between two dates.
     */
    public static <T> Specification<T> dateBetween(LocalDate start, LocalDate end,
                                                   Function<Root<T>, Expression<LocalDate>> fieldExtractor) {
        if (start == null && end == null) {
            return null;
        }

        return (root, query, cb) -> {
            Expression<LocalDate> field = fieldExtractor.apply(root);
            Predicate predicate = cb.conjunction();

            if (start != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(field, start));
            }

            if (end != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(field, end));
            }

            return predicate;
        };
    }

    // Fetch operations

    /**
     * Fetches a relation using left join.
     *
     * <pre>{@code
     * fetchJoin("user")
     * }</pre>
     */
    public static <T> Specification<T> fetchJoin(String fieldName) {
        return (root, query, cb) -> {
            if (isNotCountQuery(query)) {
                root.fetch(fieldName, jakarta.persistence.criteria.JoinType.LEFT);
                query.distinct(true);
            }
            return cb.conjunction();
        };
    }

    /**
     * Fetches nested or dynamic relations.
     *
     * <pre>{@code
     * fetchJoin(root -> root.fetch("user").fetch("role"))
     * }</pre>
     */
    public static <T> Specification<T> fetchJoin(Function<Root<T>, FetchParent<T, ?>> fetcher) {
        return (root, query, cb) -> {
            if (isNotCountQuery(query)) {
                fetcher.apply(root);
                query.distinct(true);
            }
            return cb.conjunction();
        };
    }

    /**
     * Compares value inside a join.
     *
     * <pre>{@code
     * joinEquals(roleId, root -> root.join("role"), join -> join.get("id"))
     * }</pre>
     */
    public static <T, J> Specification<T> joinEquals(Object value,
                                                     Function<Root<T>, Join<T, J>> joinExtractor,
                                                     Function<Join<T, J>, Expression<Object>> fieldExtractor) {
        return value == null ? null :
                (root, query, cb)
                        -> cb.equal(fieldExtractor.apply(joinExtractor.apply(root)), value);
    }

    /**
     * Performs a case-insensitive contains (LIKE) match on a joined entity's field.
     *
     * <pre>{@code
     * joinContains("admin", root -> root.join("user"), join -> join.get("username"))
     * }</pre>
     *
     * @param value          The substring to search for
     * @param joinExtractor  Function to perform the join (e.g., root -> root.join("user"))
     * @param fieldExtractor Function to extract the field from the join (e.g., join -> join.get("username"))
     * @return Specification with LIKE condition or null if value is empty
     */
    public static <T, J> Specification<T> joinContains(String value,
                                                       Function<Root<T>, Join<T, J>> joinExtractor,
                                                       Function<Join<T, J>, Expression<String>> fieldExtractor) {
        return StringUtils.isBlank(value) ? null :
                (root, query, cb) -> cb.like(
                        cb.lower(fieldExtractor.apply(joinExtractor.apply(root))),
                        "%" + value.toLowerCase() + "%"
                );
    }

    //Helper
    public static boolean isEmpty(String value) {
        return StringUtils.isBlank(value);
    }

    private static boolean isNotCountQuery(CriteriaQuery<?> query) {
        return query.getResultType() != Long.class && query.getResultType() != long.class;
    }

}
