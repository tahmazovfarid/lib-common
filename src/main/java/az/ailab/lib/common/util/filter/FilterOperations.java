package az.ailab.lib.common.util.filter;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.springframework.data.jpa.domain.Specification;

public final class FilterOperations {

    private FilterOperations() {

    }

    // Object operations
    public static <T, O> Specification<T> equalsObject(O value, Function<Root<T>, Expression<O>> fieldExtractor) {
        return value == null ? null :
                (root, query, cb) -> cb.equal(fieldExtractor.apply(root), value);
    }

    // String operations
    public static <T> Specification<T> contains(String value, Function<Root<T>, Expression<String>> fieldExtractor) {
        return isEmpty(value) ? null :
                (root, query, cb)
                        -> cb.like(cb.lower(fieldExtractor.apply(root)), "%" + value.toLowerCase() + "%");
    }

    public static <T> Specification<T> startsWith(String value, Function<Root<T>, Expression<String>> fieldExtractor) {
        return isEmpty(value) ? null :
                (root, query, cb)
                        -> cb.like(cb.lower(fieldExtractor.apply(root)), value.toLowerCase() + "%");
    }

    public static <T> Specification<T> endsWith(String value, Function<Root<T>, Expression<String>> fieldExtractor) {
        return isEmpty(value) ? null :
                (root, query, cb)
                        -> cb.like(cb.lower(fieldExtractor.apply(root)), "%" + value.toLowerCase());
    }

    public static <T> Specification<T> equals(String value, Function<Root<T>, Expression<String>> fieldExtractor) {
        return isEmpty(value) ? null :
                (root, query, cb)
                        -> cb.equal(cb.lower(fieldExtractor.apply(root)), value.toLowerCase());
    }

    // Number operations
    public static <T, N extends Number> Specification<T> equals(N value, Function<Root<T>, Expression<N>> fieldExtractor) {
        return value == null ? null :
                (root, query, cb)
                        -> cb.equal(fieldExtractor.apply(root), value);
    }

    public static <T, N extends Number & Comparable<N>> Specification<T> greaterThan(N value,
                                                                                     Function<Root<T>, Expression<N>> fieldExtractor) {
        return value == null ? null :
                (root, query, cb) -> cb.greaterThan(fieldExtractor.apply(root), value);
    }

    public static <T, N extends Number & Comparable<N>> Specification<T> lessThan(N value,
                                                                                  Function<Root<T>, Expression<N>> fieldExtractor) {
        return value == null ? null :
                (root, query, cb) -> cb.lessThan(fieldExtractor.apply(root), value);
    }

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
    public static <T> Specification<T> isTrue(Boolean value, Function<Root<T>, Expression<Boolean>> fieldExtractor) {
        return value == null ? null : value ?
                (root, query, cb) -> cb.isTrue(fieldExtractor.apply(root)) :
                (root, query, cb) -> cb.isFalse(fieldExtractor.apply(root));
    }

    // Date operations
    public static <T> Specification<T> dateAfter(LocalDate value, Function<Root<T>, Expression<LocalDate>> fieldExtractor) {
        return value == null ? null :
                (root, query, cb) -> cb.greaterThanOrEqualTo(fieldExtractor.apply(root), value);
    }

    public static <T> Specification<T> dateBefore(LocalDate value, Function<Root<T>, Expression<LocalDate>> fieldExtractor) {
        return value == null ? null :
                (root, query, cb) -> cb.lessThanOrEqualTo(fieldExtractor.apply(root), value);
    }

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

    // Join operations
    public static <T, J> Specification<T> joinEquals(Object value, BiFunction<Root<T>, CriteriaBuilder, Join<T, J>> joinExtractor,
                                                     Function<Join<T, J>, Expression<Object>> fieldExtractor) {
        return value == null ? null :
                (root, query, cb)
                        -> cb.equal(fieldExtractor.apply(joinExtractor.apply(root, cb)), value);
    }

    public static boolean isEmpty(String value) {
        return StringUtils.isBlank(value);
    }

}
