package az.ailab.lib.common.resolver;

import az.ailab.lib.common.model.dto.request.PaginationRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class PaginationRequestArgumentResolver implements HandlerMethodArgumentResolver {

    private final Validator validator;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return PaginationRequest.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new ServletRequestBindingException("No HttpServletRequest");
        }

        Map<String, String> params = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> snakeToCamel(e.getKey()),
                        e -> e.getValue().length > 0 ? e.getValue()[0] : null
                ));

        Integer page = params.get("page") == null ? null : Integer.valueOf(params.get("page"));
        Integer size = params.get("size") == null ? null : Integer.valueOf(params.get("size"));
        String sortBy = params.get("sortBy");
        String direction = params.get("direction");

        Constructor<PaginationRequest>
                ctor = PaginationRequest.class.getDeclaredConstructor(Integer.class, Integer.class, String.class, String.class);
        PaginationRequest paginationRequest = ctor.newInstance(page, size, sortBy, direction);

        DataBinder binder = new DataBinder(paginationRequest);
        binder.setValidator(validator);
        binder.validate();

        if (binder.getBindingResult().hasErrors()) {
            throw new BindException(binder.getBindingResult());
        }

        return paginationRequest;
    }

    private static String snakeToCamel(String key) {
        StringBuilder sb = new StringBuilder();
        boolean upper = false;
        for (char c : key.toCharArray()) {
            if (c == '_') {
                upper = true;
            } else {
                sb.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return sb.toString();
    }

}
