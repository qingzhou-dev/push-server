package dev.qingzhou.pushserver.v2.web;

public class V2Response<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> V2Response<T> ok(T data) {
        V2Response<T> response = new V2Response<>();
        response.success = true;
        response.message = "ok";
        response.data = data;
        return response;
    }

    public static <T> V2Response<T> ok(String message, T data) {
        V2Response<T> response = new V2Response<>();
        response.success = true;
        response.message = message;
        response.data = data;
        return response;
    }

    public static <T> V2Response<T> fail(String message) {
        V2Response<T> response = new V2Response<>();
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
