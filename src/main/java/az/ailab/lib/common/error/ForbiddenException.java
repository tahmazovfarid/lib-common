package az.ailab.lib.common.error;

import az.ailab.lib.common.dto.response.ResponseWrapper;
import feign.error.FeignExceptionConstructor;
import feign.error.ResponseBody;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ForbiddenException extends CommonException {

    @FeignExceptionConstructor
    public ForbiddenException(@ResponseBody ResponseWrapper<ErrorResponse> errorResponseWrapper) {
        super(errorResponseWrapper);
    }

}
