package dev.qingzhou.pushserver.exception;

public class PortalException extends RuntimeException {

    private final PortalStatus status;

    public PortalException(PortalStatus status, String message) {
        super(message);
        this.status = status;
    }

    public PortalException(PortalStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public PortalStatus getStatus() {
        return status;
    }
}
