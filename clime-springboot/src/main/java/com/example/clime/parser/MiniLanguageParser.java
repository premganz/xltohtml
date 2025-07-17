package com.example.clime.parser;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MiniLanguageParser {

    public ParsedScript parseScript(String script) {
        ParsedScript result = new ParsedScript();
        
        // Parse each block type
        result.setApiDeclaration(parseApiDeclaration(script));
        result.setOutputStructure(parseOutputStructure(script));
        result.setLayout(parseLayout(script));
        
        return result;
    }

    private ApiDeclaration parseApiDeclaration(String script) {
        Pattern blockPattern = Pattern.compile(
            "/begin block, type api_declaration\\s*(.*?)/end block", 
            Pattern.DOTALL
        );
        Matcher blockMatcher = blockPattern.matcher(script);
        
        if (blockMatcher.find()) {
            String blockContent = blockMatcher.group(1);
            
            // Parse signature matter
            Pattern matterPattern = Pattern.compile(
                "/begin matter, signature ([^,]+),\\[parameters\\]\\s*\\[([^\\]]+)\\]\\s*/end matter",
                Pattern.DOTALL
            );
            Matcher matterMatcher = matterPattern.matcher(blockContent);
            
            if (matterMatcher.find()) {
                String methodName = matterMatcher.group(1).trim();
                String parameter = matterMatcher.group(2).trim();
                
                // Convert to REST endpoint URL
                String url = "/api/" + convertToRestEndpoint(methodName, parameter);
                
                return new ApiDeclaration(methodName, parameter, url);
            }
        }
        
        return new ApiDeclaration();
    }

    private String convertToRestEndpoint(String methodName, String parameter) {
        // Convert camelCase method name to REST path
        // getStudentDetails -> students/{parameter}
        String endpoint = methodName.replaceFirst("get", "").toLowerCase();
        if (endpoint.endsWith("details")) {
            endpoint = endpoint.replace("details", "");
        }
        // Add 's' for plural REST endpoint convention
        if (!endpoint.endsWith("s")) {
            endpoint = endpoint + "s";
        }
        return endpoint + "/" + "{" + parameter.replace("v_", "") + "}";
    }

    private OutputStructure parseOutputStructure(String script) {
        Pattern blockPattern = Pattern.compile(
            "/begin block, type output_structure\\s*(.*?)/end block", 
            Pattern.DOTALL
        );
        Matcher blockMatcher = blockPattern.matcher(script);
        
        if (blockMatcher.find()) {
            String blockContent = blockMatcher.group(1);
            
            // Parse JSON structure in text matter
            Pattern jsonPattern = Pattern.compile(
                "//\\{([^}]+)\\}//",
                Pattern.DOTALL
            );
            Matcher jsonMatcher = jsonPattern.matcher(blockContent);
            
            if (jsonMatcher.find()) {
                String jsonStructure = "{" + jsonMatcher.group(1) + "}";
                return new OutputStructure(jsonStructure);
            }
        }
        
        return new OutputStructure();
    }

    private Layout parseLayout(String script) {
        Pattern blockPattern = Pattern.compile(
            "/begin block, type layout\\s*(.*?)/end block", 
            Pattern.DOTALL
        );
        Matcher blockMatcher = blockPattern.matcher(script);
        
        if (blockMatcher.find()) {
            String blockContent = blockMatcher.group(1);
            
            // Parse text matter content
            Pattern textPattern = Pattern.compile(
                "/begin matter text //\\s*(.*?)\\s*//\\s*/end matter",
                Pattern.DOTALL
            );
            Matcher textMatcher = textPattern.matcher(blockContent);
            
            if (textMatcher.find()) {
                String layoutText = textMatcher.group(1).trim();
                return new Layout(layoutText);
            }
        }
        
        return new Layout();
    }

    // Helper method to extract variables (v_*) from text
    public List<String> extractVariables(String text) {
        List<String> variables = new ArrayList<>();
        Pattern pattern = Pattern.compile("v_([a-zA-Z_][a-zA-Z0-9_.]*)");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
        
        return variables;
    }

    // Data classes for parsed content
    public static class ParsedScript {
        private ApiDeclaration apiDeclaration;
        private OutputStructure outputStructure;
        private Layout layout;

        public ApiDeclaration getApiDeclaration() { return apiDeclaration; }
        public void setApiDeclaration(ApiDeclaration apiDeclaration) { this.apiDeclaration = apiDeclaration; }
        
        public OutputStructure getOutputStructure() { return outputStructure; }
        public void setOutputStructure(OutputStructure outputStructure) { this.outputStructure = outputStructure; }
        
        public Layout getLayout() { return layout; }
        public void setLayout(Layout layout) { this.layout = layout; }
    }

    public static class ApiDeclaration {
        private String methodName;
        private String parameter;
        private String url;

        public ApiDeclaration() {
            this("", "", "");
        }

        public ApiDeclaration(String methodName, String parameter, String url) {
            this.methodName = methodName;
            this.parameter = parameter;
            this.url = url;
        }

        public String getMethodName() { return methodName; }
        public String getParameter() { return parameter; }
        public String getUrl() { return url; }
    }

    public static class OutputStructure {
        private String jsonStructure;

        public OutputStructure() {
            this("");
        }

        public OutputStructure(String jsonStructure) {
            this.jsonStructure = jsonStructure;
        }

        public String getJsonStructure() { return jsonStructure; }
    }

    public static class Layout {
        private String content;

        public Layout() {
            this("");
        }

        public Layout(String content) {
            this.content = content;
        }

        public String getContent() { return content; }
    }
}