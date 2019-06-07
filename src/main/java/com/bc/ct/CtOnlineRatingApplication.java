package com.bc.ct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class CtOnlineRatingApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CtOnlineRatingApplication.class, args);
    }

    //win 10 comment 11.05
    @Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CtOnlineRatingApplication.class);
	}
}
