package com.peatroxd.niraproxybot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NiraProxyBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(NiraProxyBotApplication.class, args);
    }

}
