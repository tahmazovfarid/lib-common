package az.ailab.lib.common.error;

import az.ailab.lib.common.dto.response.ResponseWrapper;
import feign.error.FeignExceptionConstructor;
import feign.error.ResponseBody;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ServiceUnavailableException extends CommonException {

    @FeignExceptionConstructor
    public ServiceUnavailableException(@ResponseBody ResponseWrapper<ErrorResponse> errorResponseWrapper) {
        super(errorResponseWrapper);
    }

}
