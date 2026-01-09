package dev.qingzhou.pushserver.handler;

import dev.qingzhou.push.core.model.PushResult;
import dev.qingzhou.pushserver.interceptor.SecurityInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(Exception.class)
    public PushResult handleException(Exception e) {
        // 1. 只有服务端自己看日志
        log.error("Unknown error occurred", e);
        // 2. 告诉前端“系统异常”，别告诉他具体哪行代码错了
        return PushResult.fail("Internal Server Error: " + e.getClass().getSimpleName());
    }

    // 可以专门捕获参数校验异常，返回具体字段错误
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public PushResult handleValidException(org.springframework.web.bind.MethodArgumentNotValidException e) {
        return PushResult.fail("参数错误: " + Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }
}
