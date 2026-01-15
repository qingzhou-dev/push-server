package dev.qingzhou.pushserver.v2.web;

public class V2Exception extends RuntimeException {

    private final V2Status status;

    public V2Exception(V2Status status, String message) {
        super(message);
        this.status = status;
    }

    public V2Exception(V2Status status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public V2Status getStatus() {
        return status;
    }
}
