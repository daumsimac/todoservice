package com.kakaopay.todolist.configuration;

import com.kakaopay.todolist.dto.ApiErrorResponse;
import com.kakaopay.todolist.dto.ApiResponse;
import com.kakaopay.todolist.exception.ContentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

@Slf4j
@ControllerAdvice
public class ApiResponseHandler implements ResponseBodyAdvice<Object> {
    @ExceptionHandler(
            value = {
                    ContentNotFoundException.class
            }
    )
    public ApiErrorResponse handleNotFoundError (final Exception e, HttpServletResponse response) {
        log.error(e.getMessage(), e);

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);

        return new ApiErrorResponse(response.getStatus(), e.getMessage());
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        HttpStatus httpStatus = HttpStatus.OK;

        if (body == null) {
            return new ApiErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "InternalException");
        }

        ApiResponse apiResponse;
        if (body instanceof ApiResponse) {
            apiResponse = (ApiResponse) body;

            try {
                httpStatus = HttpStatus.valueOf(apiResponse.getCode());
                response.setStatusCode(httpStatus);
            }
            catch (IllegalArgumentException iae) {}
        }
        else {
            for (Annotation annotation : returnType.getMethodAnnotations()) {
                if (annotation instanceof ResponseStatus) {
                    ResponseStatus responseStatus = (ResponseStatus) annotation;
                    httpStatus = responseStatus.value();
                    break;
                }
            }

            apiResponse = new ApiResponse(httpStatus.value(), body);
        }

        return apiResponse;
    }
}
