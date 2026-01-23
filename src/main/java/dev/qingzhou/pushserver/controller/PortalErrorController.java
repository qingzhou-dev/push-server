package dev.qingzhou.pushserver.controller;

import dev.qingzhou.pushserver.common.PortalResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortalErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<PortalResponse<Void>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute("jakarta.servlet.error.status_code");
        String message = "系统发生错误";
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                message = "接口不存在";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(PortalResponse.fail(message));
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                message = "未授权或会话过期";
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(PortalResponse.fail(message));
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                message = "没有访问权限";
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(PortalResponse.fail(message));
            } else if (statusCode >= 500) {
                message = "系统内部错误";
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PortalResponse.fail(message));
            }
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PortalResponse.fail(message));
    }
}
