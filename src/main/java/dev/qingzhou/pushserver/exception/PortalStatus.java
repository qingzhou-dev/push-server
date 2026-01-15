package dev.qingzhou.pushserver.exception;

import org.springframework.http.HttpStatus;

public enum PortalStatus {
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    CONFLICT(HttpStatus.CONFLICT),
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY);

    private final HttpStatus httpStatus;

    PortalStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
