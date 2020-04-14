package com.codefaucet.LoanMan;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan("com.codefaucet.LoanMan")
@SpringBootApplication
public class LoanManApplication {

    @Value("${cors.pathPattern}")
    private String corsPathPattern;

    @Value("${cors.allowedOrigins}")
    private String corsAllowedOrigins;

    public static void main(String[] args) {
	SpringApplication.run(LoanManApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
	return new WebMvcConfigurer() {
	    @Override
	    public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping(corsPathPattern).allowedOrigins(corsAllowedOrigins);
	    }
	};
    }

}
