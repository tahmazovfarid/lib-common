package az.ailab.lib.common.util.filter;

import org.springframework.data.jpa.domain.Specification;

public interface FilterSpecification<T> {

    Specification<T> toSpecification();

}
