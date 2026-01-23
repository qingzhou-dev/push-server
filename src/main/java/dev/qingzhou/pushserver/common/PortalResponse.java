package dev.qingzhou.pushserver.common;

public class PortalResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> PortalResponse<T> ok(T data) {
        PortalResponse<T> response = new PortalResponse<>();
        response.success = true;
        response.message = "成功";
        response.data = data;
        return response;
    }

    public static <T> PortalResponse<T> ok(String message, T data) {
        PortalResponse<T> response = new PortalResponse<>();
        response.success = true;
        response.message = message;
        response.data = data;
        return response;
    }

    public static <T> PortalResponse<T> fail(String message) {
        PortalResponse<T> response = new PortalResponse<>();
        response.success = false;
        response.message = message;
        response.data = null;
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
