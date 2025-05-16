package az.ailab.lib.common.model.dto.response;

import java.io.Serializable;

public record IdResponse<T>(T id) implements Serializable {

}