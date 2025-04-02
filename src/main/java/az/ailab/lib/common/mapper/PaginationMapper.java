package az.ailab.lib.common.mapper;

import az.ailab.lib.common.dto.request.PaginationRequest;
import az.ailab.lib.common.dto.response.PaginationResponse;
import io.micrometer.common.util.StringUtils;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    /**
     * Converts the current {@link PaginationRequest} to a Spring {@link Pageable} object with sorting,
     * based on the provided set of allowed sort fields.
     * <p>
     * Behavior summary:
     * <ul>
     *     <li>If {@code allowedSortFields} is {@code null} or empty → returns {@link Sort#unsorted()}.</li>
     *     <li>If {@code sortBy} is {@code null}, blank, or not found in the {@code allowedSortFields} set →
     *         the first element from {@code allowedSortFields} is used as the default sort field.</li>
     *     <li>If {@code direction} is {@code null}, blank, or equals {@code "desc"} (case-insensitive) →
     *         sorting is applied in {@link Sort.Direction#DESC} direction. Otherwise, {@link Sort.Direction#ASC} is used.</li>
     * </ul>
     * <p>
     * Example usage:
     * <pre>
     * {@code
     * LinkedHashSet<String> allowedFields = new LinkedHashSet<>(List.of("createdAt", "name", "email"));
     * Pageable pageable = paginationRequest.toPageable(allowedFields);
     * }
     * </pre>
     *
     * @param allowedSortFields a non-empty set of valid fields to be used for sorting; order matters
     * @return a {@link Pageable} instance with sorting applied, or unsorted if no valid field is found
     */
    public static Pageable toPageable(final PaginationRequest paginationRequest,
                                      final LinkedHashSet<String> allowedSortFields) {
        final int defaultPage = 0;
        final int defaultSize = 10;
        final Integer page = paginationRequest.page();
        final Integer size = paginationRequest.size();

        final int effectivePage = page != null ? page : defaultPage;
        final int effectiveSize = size != null ? size : defaultSize;

        final Sort sort = resolveSort(paginationRequest, allowedSortFields);

        return PageRequest.of(effectivePage, effectiveSize, sort);
    }

    private static Sort resolveSort(final PaginationRequest paginationRequest,
                                    final LinkedHashSet<String> allowedSortFields) {
        if (allowedSortFields == null || allowedSortFields.isEmpty()) {
            return Sort.unsorted();
        }

        final String sortBy = paginationRequest.sortBy();

        final Sort.Direction direction = resolveDirection(paginationRequest.direction());
        final String sortField = StringUtils.isBlank(sortBy) || !allowedSortFields.contains(sortBy)
                ? allowedSortFields.iterator().next()
                : sortBy.trim();

        return Sort.by(direction, sortField);
    }

    private static Sort.Direction resolveDirection(String direction) {
        if (direction == null || direction.isBlank() || direction.equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
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
