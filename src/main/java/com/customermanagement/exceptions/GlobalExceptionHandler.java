package com.customermanagement.exceptions;

import com.customermanagement.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFoundException(CustomerNotFoundException ex, WebRequest request) {
        log.error("GLOBAL_ERROR_HANDLER_CUSTOMER_NOT_FOUND {}", ex.getMessage()," {} ", ex.getStackTrace(), " Req {}", request);
        return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                "Not Found", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInputException(InvalidInputException ex, WebRequest request) {
        log.error("GLOBAL_ERROR_HANDLER_BAD_REQ {}", ex.getMessage()," {} ", ex.getStackTrace(), " Req {}", request);
        return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                "Bad Request", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseOperationException(DatabaseOperationException ex, WebRequest request) {
        log.error("GLOBAL_ERROR_HANDLER_DATABASE_ERROR  {}", ex.getMessage()," {} ", ex.getStackTrace(), " Req {}", request);
        return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Database error", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        log.info("GLOBAL_ERROR_HANDLER_METHOD_ARGS_MISMATCH  {}", ex.getMessage()," {} ", ex.getStackTrace());
        log.error("GLOBAL_ERROR_HANDLER_METHOD_ARGS_MISMATCH  {}", ex.getMessage()," {} ", ex.getStackTrace());
        System.out.println("IMAINI");
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({KafkaPublishException.class, TimeoutException.class})
    public ResponseEntity<ErrorResponse> handleKafkaPublishException(KafkaPublishException ex) {
        log.error("Kafka publish error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Failed to process request due to messaging service error", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("GLOBAL_ERROR_HANDLER_GENERAL_ERROR  {}", ex.getMessage()," {} ", ex.getStackTrace(), " Req {}", request);
        return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}