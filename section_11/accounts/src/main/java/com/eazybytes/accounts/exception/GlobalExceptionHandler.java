package com.eazybytes.accounts.exception;

import com.eazybytes.accounts.dto.ErrorResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// in case of exception in any controller classes on the project, then invoke the respective method from the class
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<ObjectError> objectErrors = ex.getBindingResult().getAllErrors();

        Map<String, String> fieldToErrorMsg = objectErrors.stream()
                .filter(FieldError.class::isInstance)
                .map(FieldError.class::cast)
                .filter(fieldError -> fieldError.getDefaultMessage() != null)
                .collect(
                        Collectors.toMap(
                                FieldError::getField,
                                ObjectError::getDefaultMessage
                        )
                );

        return new ResponseEntity<>(fieldToErrorMsg, HttpStatus.BAD_REQUEST);
    }

    // tell which method must be executed depending on the type of error
    // first it checks if there is an exact match by type and if not, then it checks the parents of the given class
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponseDto> handleGlobalException(
            Exception exception,
            WebRequest request
    ) {
        var error = new ErrorResponseDto(
                request.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomerAlreadyExistsException.class)
    ResponseEntity<ErrorResponseDto> handleCustomerAlreadyExistsException(
            CustomerAlreadyExistsException exception,
            WebRequest request
    ) {
        var error = new ErrorResponseDto(
                request.getDescription(false),
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNoFoundException.class)
    ResponseEntity<ErrorResponseDto> handleResourceNoFoundException(
            ResourceNoFoundException exception,
            WebRequest request
    ) {
        var error = new ErrorResponseDto(
                request.getDescription(false),
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
