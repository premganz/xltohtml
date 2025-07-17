package com.example.clime.generator;

import com.example.clime.parser.MhtParser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
public class HtmlGenerator {

    public String generateHtml(MhtParser.MhtData mhtData, String fileName) {
        MhtParser.ApiData apiData = mhtData.getApiData();
        String layout = mhtData.getLayoutSheet();
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"utf-8\">\n");
        html.append("    <title>Generated from ").append(fileName).append("</title>\n");
        html.append("    <style>\n");
        html.append("        body { font-family: Arial, sans-serif; margin: 20px; }\n");
        html.append("        table { border-collapse: collapse; width: 100%; margin: 10px 0; }\n");
        html.append("        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        html.append("        th { background-color: #f2f2f2; }\n");
        html.append("        form { margin: 20px 0; }\n");
        html.append("        input, button { margin: 5px; padding: 8px; }\n");
        html.append("        .hidden { display: none; }\n");
        html.append("        .result { margin: 20px 0; padding: 15px; background-color: #f9f9f9; border-radius: 5px; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        
        // Add the layout content
        html.append(processLayout(layout, apiData));
        
        // Add JavaScript for API calls
        html.append(generateJavaScript(apiData));
        
        html.append("</body>\n");
        html.append("</html>");
        
        return html.toString();
    }
    
    private String processLayout(String layout, MhtParser.ApiData apiData) {
        // Remove style="display:none;" from result div to make it visible
        String processedLayout = layout.replaceAll("style=\"display:none;\"", "");
        
        // Process iteration blocks
        processedLayout = processIterations(processedLayout);
        
        return processedLayout;
    }
    
    private String processIterations(String layout) {
        Pattern iteratePattern = Pattern.compile("iterate\\(\\{([^}]+)\\}\\)(.*?)end_iterate", Pattern.DOTALL);
        Matcher matcher = iteratePattern.matcher(layout);
        
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        
        while (matcher.find()) {
            result.append(layout, lastEnd, matcher.start());
            
            String arrayName = matcher.group(1);
            String template = matcher.group(2);
            
            // Generate JavaScript template for iteration
            result.append("<div id=\"").append(arrayName).append("-container\">");
            result.append("<!-- Template for ").append(arrayName).append(" iteration -->");
            result.append("<script type=\"text/template\" id=\"").append(arrayName).append("-template\">");
            result.append(template);
            result.append("</script>");
            result.append("</div>");
            
            lastEnd = matcher.end();
        }
        
        result.append(layout.substring(lastEnd));
        return result.toString();
    }
    
    private String generateJavaScript(MhtParser.ApiData apiData) {
        StringBuilder js = new StringBuilder();
        js.append("\n<script>\n");
        
        // Function to populate placeholders
        js.append("function populateTemplate(template, data) {\n");
        js.append("    return template.replace(/\\{([^}]+)\\}/g, function(match, key) {\n");
        js.append("        return data[key] || match;\n");
        js.append("    });\n");
        js.append("}\n\n");
        
        // Function to handle iterations
        js.append("function renderIteration(containerId, templateId, dataArray) {\n");
        js.append("    const container = document.getElementById(containerId);\n");
        js.append("    const template = document.getElementById(templateId);\n");
        js.append("    if (!container || !template) return;\n");
        js.append("    \n");
        js.append("    let html = '';\n");
        js.append("    dataArray.forEach(item => {\n");
        js.append("        html += populateTemplate(template.innerHTML, item);\n");
        js.append("    });\n");
        js.append("    \n");
        js.append("    // Remove template and replace with rendered content\n");
        js.append("    template.style.display = 'none';\n");
        js.append("    const renderedDiv = document.createElement('div');\n");
        js.append("    renderedDiv.innerHTML = html;\n");
        js.append("    container.appendChild(renderedDiv);\n");
        js.append("}\n\n");
        
        if (!apiData.getUrl().isEmpty()) {
            // API fetch function
            js.append("async function fetchData(").append(apiData.getParameter().isEmpty() ? "" : "parameter").append(") {\n");
            js.append("    try {\n");
            
            String url = apiData.getUrl();
            if (!apiData.getParameter().isEmpty()) {
                js.append("        const url = '").append(url).append("'.replace('{").append(apiData.getParameter()).append("}', parameter);\n");
            } else {
                js.append("        const url = '").append(url).append("';\n");
            }
            
            js.append("        const response = await fetch(url, { method: '").append(apiData.getMethod()).append("' });\n");
            js.append("        const data = await response.json();\n");
            js.append("        \n");
            js.append("        // Populate single data placeholders\n");
            js.append("        document.querySelectorAll('[id=\"result\"] p').forEach(p => {\n");
            js.append("            p.innerHTML = populateTemplate(p.innerHTML, data);\n");
            js.append("        });\n");
            js.append("        \n");
            js.append("        // Handle students iteration if data contains students array\n");
            js.append("        if (data.students && Array.isArray(data.students)) {\n");
            js.append("            renderIteration('students-container', 'students-template', data.students);\n");
            js.append("        }\n");
            js.append("        \n");
            js.append("        // Show result div\n");
            js.append("        const resultDiv = document.getElementById('result');\n");
            js.append("        if (resultDiv) resultDiv.style.display = 'block';\n");
            js.append("        \n");
            js.append("    } catch (error) {\n");
            js.append("        console.error('Error fetching data:', error);\n");
            js.append("        alert('Error fetching data: ' + error.message);\n");
            js.append("    }\n");
            js.append("}\n\n");
            
            // Form submission handler
            if (!apiData.getParameter().isEmpty()) {
                js.append("document.addEventListener('DOMContentLoaded', function() {\n");
                js.append("    const form = document.getElementById('studentForm');\n");
                js.append("    if (form) {\n");
                js.append("        form.addEventListener('submit', function(e) {\n");
                js.append("            e.preventDefault();\n");
                js.append("            const parameter = document.getElementById('").append(apiData.getParameter()).append("').value;\n");
                js.append("            if (parameter) {\n");
                js.append("                fetchData(parameter);\n");
                js.append("            } else {\n");
                js.append("                alert('Please enter a ").append(apiData.getParameter()).append("');\n");
                js.append("            }\n");
                js.append("        });\n");
                js.append("    }\n");
                js.append("});\n");
            } else {
                // Auto-fetch if no parameter required
                js.append("document.addEventListener('DOMContentLoaded', function() {\n");
                js.append("    fetchData();\n");
                js.append("});\n");
            }
        }
        
        js.append("</script>\n");
        return js.toString();
    }
}