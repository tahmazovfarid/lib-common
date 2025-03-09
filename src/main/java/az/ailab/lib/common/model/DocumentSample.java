package az.ailab.lib.common.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
import lombok.Data;

@Data
public class DocumentSample {

    @NotBlank
    private String endpoint;

    @NotEmpty
    private Map<String, Document> documentMap;

}
