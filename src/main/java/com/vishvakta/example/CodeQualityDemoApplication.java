package com.vishvakta.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.vishvakta.example")
public class CodeQualityDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeQualityDemoApplication.class, args);
    }
}
