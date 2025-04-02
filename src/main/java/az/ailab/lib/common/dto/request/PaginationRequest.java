package az.ailab.lib.common.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record PaginationRequest(
        @Min(value = 0, message = "${validation.page.min}")
        Integer page,

        @Min(value = 1, message = "${validation.size.min}")
        @Max(value = 100, message = "${validation.size.max}")
        Integer size,

        @Pattern(regexp = "^[a-zA-Z]+$", message = "${validation.sort.pattern}")
        String sortBy,

        @Pattern(regexp = "^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "${validation.direction.pattern}")
        String direction) {

}