package dev.qingzhou.pushserver.v2.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "dev.qingzhou.pushserver.v2")
public class V2ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(V2ExceptionHandler.class);

    @ExceptionHandler(V2Exception.class)
    public ResponseEntity<V2Response<Void>> handleV2Exception(V2Exception ex) {
        return ResponseEntity.status(ex.getStatus().getHttpStatus())
                .body(V2Response.fail(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<V2Response<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = "Validation failed";
        if (ex.getBindingResult().getFieldError() != null) {
            message = ex.getBindingResult().getFieldError().getDefaultMessage();
        }
        return ResponseEntity.badRequest().body(V2Response.fail(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<V2Response<Void>> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(V2Response.fail("Invalid request payload"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<V2Response<Void>> handleException(Exception ex) {
        log.error("Unhandled v2 error", ex);
        return ResponseEntity.internalServerError()
                .body(V2Response.fail("Internal Server Error"));
    }
}
