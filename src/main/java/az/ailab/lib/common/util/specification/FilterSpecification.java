package az.ailab.lib.common.util.specification;

import org.springframework.data.jpa.domain.Specification;

public interface FilterSpecification<T> {

    Specification<T> toSpecification();

}
