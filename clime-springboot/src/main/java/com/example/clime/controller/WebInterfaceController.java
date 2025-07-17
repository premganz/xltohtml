package com.example.clime.controller;

import com.example.clime.parser.MiniLanguageParser;
import com.example.clime.generator.MiniLanguageHtmlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebInterfaceController {

    @Autowired
    private MiniLanguageParser miniLanguageParser;

    @Autowired
    private MiniLanguageHtmlGenerator htmlGenerator;

    @GetMapping("/")
    public String landingPage() {
        return "redirect:/landing.html";
    }

    @PostMapping("/api/generate")
    @ResponseBody
    public ResponseEntity<String> generateFromScript(@RequestBody String script) {
        try {
            // Parse the mini-language script
            MiniLanguageParser.ParsedScript parsedScript = miniLanguageParser.parseScript(script);
            
            // Generate HTML from parsed script
            String generatedHtml = htmlGenerator.generateHtml(parsedScript);
            
            return ResponseEntity.ok(generatedHtml);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error parsing script: " + e.getMessage());
        }
    }
}