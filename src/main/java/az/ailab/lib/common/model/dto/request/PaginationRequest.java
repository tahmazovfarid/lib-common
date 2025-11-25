package az.ailab.lib.common.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record PaginationRequest(
        @Min(value = 0, message = "${page.min}")
        Integer page,

        @Min(value = 1, message = "${size.min}")
        @Max(value = 100, message = "${size.max}")
        Integer size,

        @Pattern(regexp = "^[a-zA-Z]+$", message = "${sort.pattern}")
        @JsonProperty("sort_by")
        String sortBy,

        @Pattern(regexp = "^(asc|desc)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "${direction.pattern}")
        String direction) {

}