package dev.qingzhou.pushserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class PushServerApplication {

    public static void main(String[] args) {
        // 允许 HTTP 代理进行 Basic 认证（解决 HTTPS 隧道建立时的 407 错误）
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        System.setProperty("jdk.http.auth.proxying.disabledSchemes", "");

        if (System.getProperty("java.home") == null) {
            System.setProperty("java.home", new File(".").getAbsolutePath());
        }
        SpringApplication.run(PushServerApplication.class, args);
    }

}
