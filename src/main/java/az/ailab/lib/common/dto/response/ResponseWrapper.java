package az.ailab.lib.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * A generic response wrapper for standardizing API responses.
 * <p>
 * This class is used to encapsulate both successful and error responses,
 * ensuring consistent structure across all API endpoints.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 * <li>Encapsulates status code, timestamp, data, and errors.</li>
 * <li>Provides utility methods for success and error responses.</li>
 * </ul>
 *
 * @param <T> The type of data included in the response.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper<T> {

    private Integer statusCode;
    private LocalDateTime timeStamp;
    private T data;
    private T error;

    public static <T> ResponseWrapper<T> ok(T data) {
        return wrap(HttpStatus.OK, data);
    }

    public static <T> ResponseWrapper<T> error(T error) {
        ResponseWrapper<T> errorResponse = new ResponseWrapper<>();
        errorResponse.setError(error);
        return errorResponse;
    }

    public static <T> ResponseWrapper<T> wrap(HttpStatus status, T data) {
        ResponseWrapper<T> successResponse = new ResponseWrapper<>();
        successResponse.setStatusCode(status.value());
        successResponse.setTimeStamp(LocalDateTime.now());
        successResponse.setData(data);

        return successResponse;
    }

}
