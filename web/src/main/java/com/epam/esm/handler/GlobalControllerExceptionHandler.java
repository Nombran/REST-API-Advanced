package com.epam.esm.handler;

import com.epam.esm.service.ServiceConflictException;
import com.epam.esm.service.ServiceNotFoundException;
import com.epam.esm.tag.TagNotFoundException;
import com.epam.esm.user.UserNotFoundException;
import org.modelmapper.MappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ErrorResponse handleMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        error.setError(HttpStatus.UNSUPPORTED_MEDIA_TYPE.toString());
        error.setMessage(ex.getMessage());
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MappingException.class)
    public ErrorResponse handleMappingException(MappingException ex) {
        ex.printStackTrace();
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError(HttpStatus.BAD_REQUEST.toString());
        error.setMessage(ex.getCause().getMessage());
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        ex.printStackTrace();
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class})
    public ErrorResponse handleIllegalArgumentException(RuntimeException ex) {
        ex.printStackTrace();
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError(HttpStatus.BAD_REQUEST.toString());
        error.setMessage(ex.getMessage());
        return error;
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse handleRequestMethodNotSupportedResponse
            (HttpRequestMethodNotSupportedException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
        error.setError(HttpStatus.METHOD_NOT_ALLOWED.toString());
        error.setMessage(ex.getMessage());
        return error;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ServiceNotFoundException.class,
            NoHandlerFoundException.class,
            TagNotFoundException.class,
            UserNotFoundException.class,
    })
    public ErrorResponse handleResourceNotFound(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setError(HttpStatus.NOT_FOUND.toString());
        error.setMessage(ex.getMessage());
        return error;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({ServiceConflictException.class})
    public ErrorResponse handleServiceConflictException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setError(HttpStatus.CONFLICT.toString());
        error.setMessage(ex.getMessage());
        return error;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({org.hibernate.exception.ConstraintViolationException.class})
    public ErrorResponse handleServiceConflictException(
            org.hibernate.exception.ConstraintViolationException ex) {

        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setError(HttpStatus.CONFLICT.toString());
        String message = ex.getCause().getLocalizedMessage();
        error.setMessage(message.substring(message.indexOf("Detail")));
        return error;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.setError(HttpStatus.UNAUTHORIZED.toString());
        error.setMessage(ex.getMessage());
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ErrorResponse handleInvalidFormatException(HttpMessageNotReadableException ex) {
        ex.printStackTrace();
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError(HttpStatus.BAD_REQUEST.toString());
        error.setMessage(ex.getCause().getLocalizedMessage());
        return error;
    }
}
