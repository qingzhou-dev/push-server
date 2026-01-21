package dev.qingzhou.pushserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class PushServerApplication {

    public static void main(String[] args) {
        if (System.getProperty("java.home") == null) {
            System.setProperty("java.home", new File(".").getAbsolutePath());
        }
        SpringApplication.run(PushServerApplication.class, args);
    }

}
