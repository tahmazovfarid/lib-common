package az.ailab.lib.common.model.dto.response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError implements Serializable {

    private String property;
    private String message;

}