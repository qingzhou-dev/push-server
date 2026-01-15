package dev.qingzhou.pushserver.exception;

import dev.qingzhou.pushserver.common.PortalResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "dev.qingzhou.pushserver.controller.portal")
public class PortalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(PortalExceptionHandler.class);

    @ExceptionHandler(PortalException.class)
    public ResponseEntity<PortalResponse<Void>> handlePortalException(PortalException ex) {
        return ResponseEntity.status(ex.getStatus().getHttpStatus())
                .body(PortalResponse.fail(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<PortalResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = "Validation failed";
        if (ex.getBindingResult().getFieldError() != null) {
            message = ex.getBindingResult().getFieldError().getDefaultMessage();
        }
        return ResponseEntity.badRequest().body(PortalResponse.fail(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<PortalResponse<Void>> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(PortalResponse.fail("Invalid request payload"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<PortalResponse<Void>> handleException(Exception ex) {
        log.error("Unhandled portal error", ex);
        return ResponseEntity.internalServerError()
                .body(PortalResponse.fail("Internal Server Error"));
    }
}
