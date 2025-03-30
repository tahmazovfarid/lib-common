package az.ailab.lib.common.mapper;

import az.ailab.lib.common.dto.response.PaginationResponse;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;

public final class PaginationMapper {

    private PaginationMapper() {

    }

    /**
     * Generic method to map a Page to PaginationResponse using a mapper function
     *
     * @param page           Source JPA Page
     * @param mapperFunction Function to convert each entity to DTO
     * @return PaginationResponse with converted content
     */
    public static <E, D> PaginationResponse<D> toPaginationResponse(Page<E> page, Function<E, D> mapperFunction) {
        if (page == null) {
            return null;
        }

        List<D> content = page.getContent().stream()
                .map(mapperFunction)
                .toList();

        return buildPaginationResponse(page, content);
    }

    /**
     * Overloaded method to map a Page using an existing mapped content list
     *
     * @param page    Source JPA Page (for pagination metadata)
     * @param content Already mapped content list
     * @return PaginationResponse with the provided content
     */
    public static <E, D> PaginationResponse<D> toPaginationResponse(Page<E> page, List<D> content) {
        if (page == null) {
            return null;
        }

        return buildPaginationResponse(page, content);
    }

    private static <E, D> PaginationResponse<D> buildPaginationResponse(Page<E> page, List<D> content) {
        return new PaginationResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

}
