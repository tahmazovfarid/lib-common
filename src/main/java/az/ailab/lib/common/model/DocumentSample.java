package az.ailab.lib.common.model;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DocumentSample {

    @NotBlank
    private String endpoint;

    @NotEmpty
    private Map<String, Document> documentMap;

}
