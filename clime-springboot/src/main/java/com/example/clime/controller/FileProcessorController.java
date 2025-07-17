package com.example.clime.controller;

import com.example.clime.metamodule.FileProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
public class FileProcessorController {

    @Autowired
    private FileProcessor fileProcessor;

    @PostMapping("/process")
    public ResponseEntity<String> processFiles() {
        try {
            fileProcessor.processFiles();
            return ResponseEntity.ok("Files processed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error processing files: " + e.getMessage());
        }
    }
}
