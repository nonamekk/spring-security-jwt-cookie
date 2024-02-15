package box.springsecuritycookie.controllers.exception_handlers;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> validationHandler(@NotNull MethodArgumentNotValidException exception, WebRequest request ) {
        Map<String, Object> resObj = new LinkedHashMap<>();
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        List<Object> errorDetails = new ArrayList<>();

        exception.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError) {
                Map<String, Object> obj = new LinkedHashMap<String, Object>();
                FieldError fieldError = (FieldError) error;
                String fieldName = fieldError.getField();
                String errorMessage = fieldError.getDefaultMessage();

                obj.put("field", fieldName);
                obj.put("error", errorMessage);
                errorDetails.add(obj);
            }
        });

        resObj.put("timestamp", new Date());
        resObj.put("status", HttpStatus.BAD_REQUEST.value());
        resObj.put("error", errorDetails);
        resObj.put("path ", path);

        return new ResponseEntity<>(resObj, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> globalExceptionHandler(@NotNull Exception exception, WebRequest request) {
        Map<String, Object> resObj = new LinkedHashMap<>();
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        resObj.put("timestamp", new Date());
        resObj.put("status", HttpStatus.BAD_REQUEST.value());
        resObj.put("error", exception.getMessage());
        resObj.put("path ", path);

        return new ResponseEntity<>(resObj, HttpStatus.BAD_REQUEST);
    }
}
