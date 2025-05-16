package az.ailab.lib.common.model.dto.response;

import java.io.Serializable;
import java.util.List;

public record PaginationResponse<T> (
        List<T> content,
        Integer page,
        Integer size,
        Boolean hasNext,
        Long totalElements,
        Integer totalPages) implements Serializable {

}
