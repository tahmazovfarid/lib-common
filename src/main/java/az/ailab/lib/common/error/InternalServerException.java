package az.ailab.lib.common.error;

import az.ailab.lib.common.model.dto.response.ErrorResponse;
import az.ailab.lib.common.model.dto.response.ResponseWrapper;
import feign.error.FeignExceptionConstructor;
import feign.error.ResponseBody;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class InternalServerException extends CommonException {

    @FeignExceptionConstructor
    public InternalServerException(@ResponseBody ResponseWrapper<ErrorResponse> errorResponseWrapper) {
        super(errorResponseWrapper);
    }

}
