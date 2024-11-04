package com.huang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableScheduling
@EnableWebSocket
@SpringBootApplication
public class GetSessionApplication {

    public static void main(String[] args) {
        SpringApplication.run(GetSessionApplication.class, args);
    }
}
