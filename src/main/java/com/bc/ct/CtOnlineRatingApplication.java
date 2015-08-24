package com.bc.ct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CtOnlineRatingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CtOnlineRatingApplication.class, args);
    }
}
