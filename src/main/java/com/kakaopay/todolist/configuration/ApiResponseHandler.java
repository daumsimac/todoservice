package com.kakaopay.todolist.configuration;

import com.kakaopay.todolist.dto.ApiErrorResponse;
import com.kakaopay.todolist.dto.ApiResponse;
import com.kakaopay.todolist.exception.ContentNotFoundException;
import com.kakaopay.todolist.exception.InvalidMoveTargetException;
import com.kakaopay.todolist.exception.ParentCompletedException;
import com.kakaopay.todolist.exception.TodoDependencyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
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
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFoundException (final Exception e) {
        log.error(e.getMessage(), e);

        return new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(
            value = {
                    InvalidMoveTargetException.class,
                    TodoDependencyException.class,
                    ParentCompletedException.class
            }
    )
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBadRequestException (final Exception e) {
        log.error(e.getMessage(), e);

        return new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
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

        Object ret = null;
        if (body instanceof ApiResponse) {
            ApiResponse apiResponse = (ApiResponse) body;

            try {
                httpStatus = HttpStatus.valueOf(apiResponse.getCode());
                response.setStatusCode(httpStatus);

                ret = response;
            }
            catch (IllegalArgumentException iae) {}
        }
        else if (body instanceof ApiErrorResponse) {
            ret = body;
        }
        else {
            for (Annotation annotation : returnType.getMethodAnnotations()) {
                if (annotation instanceof ResponseStatus) {
                    ResponseStatus responseStatus = (ResponseStatus) annotation;
                    httpStatus = responseStatus.value();
                    break;
                }
            }

            ret = new ApiResponse(httpStatus.value(), body);
        }

        return ret;
    }
}
