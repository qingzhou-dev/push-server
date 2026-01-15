package dev.qingzhou.pushserver.v2.web;

import org.springframework.http.HttpStatus;

public enum V2Status {
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    CONFLICT(HttpStatus.CONFLICT),
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY);

    private final HttpStatus httpStatus;

    V2Status(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
