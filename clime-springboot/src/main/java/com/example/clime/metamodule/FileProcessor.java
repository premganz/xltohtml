package com.example.clime.metamodule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileProcessor {

    @Autowired
    private HtmlParser htmlParser;

    private final String inputDirectory = "src/main/resources/input";
    private final String outputDirectory = "src/main/resources/static";

    public void processFiles() {
        try {
            Path inputPath = Paths.get(inputDirectory);
            if (!Files.exists(inputPath)) {
                Files.createDirectories(inputPath);
            }
            
            Path outputPath = Paths.get(outputDirectory);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }
            
            Files.list(inputPath)
                .filter(path -> path.toString().endsWith(".htm") || path.toString().endsWith(".html"))
                .forEach(this::processFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processFile(Path filePath) {
        try {
            String processedHtml = htmlParser.parseHtmlFile(filePath);
            saveProcessedHtml(filePath.getFileName().toString(), processedHtml);
            System.out.println("Processed file: " + filePath.getFileName());
        } catch (IOException e) {
            System.err.println("Error processing file " + filePath.getFileName() + ": " + e.getMessage());
        }
    }

    private void saveProcessedHtml(String fileName, String content) {
        String outputFileName = fileName.replaceAll("\\.(htm|html)$", "_processed.html");
        Path outputPath = Paths.get(outputDirectory, outputFileName);
        try {
            Files.write(outputPath, content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}