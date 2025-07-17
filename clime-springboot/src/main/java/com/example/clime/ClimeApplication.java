package com.example.clime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

@SpringBootApplication
@EnableBatchProcessing
public class ClimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClimeApplication.class, args);
    }
}