package com.example.clime;

import com.example.clime.metamodule.FileProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClimeApplication implements CommandLineRunner {

    @Autowired
    private FileProcessor fileProcessor;

    public static void main(String[] args) {
        SpringApplication.run(ClimeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting file processing...");
        fileProcessor.processFiles();
        System.out.println("File processing completed.");
    }
}