package az.ailab.lib.common.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PaginationRequest(
        @Min(value = 0, message = "Page index must not be less than zero")
        Integer page,

        @Min(value = 1, message = "Page size must not be less than one")
        @Max(value = 100, message = "Page size must not be greater than 100")
        Integer size,

        @Pattern(regexp = "^[a-zA-Z]+$", message = "Sort by only contain letters.")
        String sortBy,

        @Pattern(regexp = "^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Direction must be either 'asc' or 'desc'")
        String direction) {

    public Pageable toPageable() {
        final String effectiveSortField = sortBy == null || sortBy.isBlank()
                ? "id"
                : sortBy.trim();

        final Sort.Direction effectiveDirection = parseDirection(direction);

        return PageRequest.of(page, size, Sort.by(effectiveDirection, effectiveSortField));
    }

    private Sort.Direction parseDirection(String dir) {
        if (dir == null || dir.isBlank() || dir.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        }
        return Sort.Direction.DESC;
    }


}
