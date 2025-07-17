package com.example.clime.controller;

import com.example.clime.parser.MiniLanguageParser;
import com.example.clime.generator.MiniLanguageHtmlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class WebInterfaceController {

    @Autowired
    private MiniLanguageParser miniLanguageParser;

    @Autowired
    private MiniLanguageHtmlGenerator htmlGenerator;

    private final Map<String, SseEmitter> logEmitters = new ConcurrentHashMap<>();
    private final AtomicLong sessionCounter = new AtomicLong(0);
    private final String outputDirectory = "../output";

    @GetMapping("/")
    public String landingPage() {
        return "redirect:/landing.html";
    }

    @GetMapping("/api/logs")
    public SseEmitter streamLogs() {
        String sessionId = "session-" + sessionCounter.incrementAndGet();
        SseEmitter emitter = new SseEmitter(30000L);
        
        logEmitters.put(sessionId, emitter);
        
        emitter.onCompletion(() -> logEmitters.remove(sessionId));
        emitter.onTimeout(() -> logEmitters.remove(sessionId));
        emitter.onError((ex) -> logEmitters.remove(sessionId));
        
        try {
            emitter.send(SseEmitter.event()
                .data("Connected to logging stream")
                .name("log"));
        } catch (IOException e) {
            logEmitters.remove(sessionId);
        }
        
        return emitter;
    }

    @PostMapping("/api/generate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateFromScript(@RequestBody String script) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            sendLogMessage("Starting script parsing...");
            
            // Parse the mini-language script
            MiniLanguageParser.ParsedScript parsedScript = miniLanguageParser.parseScript(script);
            sendLogMessage("Script parsed successfully");
            
            // Generate HTML from parsed script
            sendLogMessage("Generating HTML from parsed script...");
            String generatedHtml = htmlGenerator.generateHtml(parsedScript);
            sendLogMessage("HTML generation completed");
            
            // Save to output directory
            String filename = saveToOutputDirectory(generatedHtml);
            sendLogMessage("File saved to output directory as: " + filename);
            
            response.put("html", generatedHtml);
            response.put("filename", filename);
            response.put("status", "success");
            
            sendLogMessage("Generation process completed successfully!");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            sendLogMessage("Error: " + e.getMessage());
            response.put("error", "Error parsing script: " + e.getMessage());
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

    private void sendLogMessage(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String logEntry = "[" + timestamp + "] " + message;
        
        // Send to all connected log streams
        logEmitters.values().removeIf(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                    .data(logEntry)
                    .name("log"));
                return false;
            } catch (IOException e) {
                return true; // Remove failed emitters
            }
        });
    }

    private String saveToOutputDirectory(String htmlContent) throws IOException {
        // Ensure output directory exists
        Path outputPath = Paths.get(outputDirectory);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        
        // Generate filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "generated_" + timestamp + ".html";
        Path filePath = outputPath.resolve(filename);
        
        // Write file
        Files.write(filePath, htmlContent.getBytes(), 
                   StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        
        return filename;
    }
}