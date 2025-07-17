package com.example.clime.generator;

import com.example.clime.parser.MiniLanguageParser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MiniLanguageHtmlGenerator {

    public String generateHtml(MiniLanguageParser.ParsedScript parsedScript) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>Generated Application</title>\n");
        html.append("    <style>\n");
        html.append("        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }\n");
        html.append("        .container { max-width: 800px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n");
        html.append("        h1 { color: #333; border-bottom: 2px solid #007bff; padding-bottom: 10px; }\n");
        html.append("        .input-section { background: #f8f9fa; padding: 20px; border-radius: 5px; margin: 20px 0; }\n");
        html.append("        .output-section { background: #fff; padding: 20px; border: 1px solid #ddd; border-radius: 5px; margin: 20px 0; }\n");
        html.append("        input, button { padding: 10px; margin: 5px; border: 1px solid #ddd; border-radius: 4px; }\n");
        html.append("        button { background: #007bff; color: white; cursor: pointer; }\n");
        html.append("        button:hover { background: #0056b3; }\n");
        html.append("        .layout-content { white-space: pre-wrap; font-family: 'Courier New', monospace; }\n");
        html.append("        table { border-collapse: collapse; width: 100%; margin: 10px 0; }\n");
        html.append("        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        html.append("        th { background-color: #f2f2f2; }\n");
        html.append("        .hidden { display: none; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");
        html.append("        <h1>Generated Application</h1>\n");
        
        // Generate input form based on API declaration
        html.append(generateInputForm(parsedScript.getApiDeclaration()));
        
        // Generate output section based on layout
        html.append(generateOutputSection(parsedScript.getLayout()));
        
        html.append("    </div>\n");
        
        // Generate JavaScript
        html.append(generateJavaScript(parsedScript));
        
        html.append("</body>\n");
        html.append("</html>\n");
        
        return html.toString();
    }

    private String generateInputForm(MiniLanguageParser.ApiDeclaration apiDeclaration) {
        if (apiDeclaration.getParameter().isEmpty()) {
            return "";
        }
        
        StringBuilder form = new StringBuilder();
        form.append("        <div class=\"input-section\">\n");
        form.append("            <h2>Input Parameters</h2>\n");
        form.append("            <form id=\"dataForm\">\n");
        
        String paramName = apiDeclaration.getParameter().replace("v_", "");
        form.append("                <label for=\"").append(paramName).append("\">")
            .append(formatLabel(paramName)).append(":</label>\n");
        form.append("                <input type=\"text\" id=\"").append(paramName)
            .append("\" name=\"").append(paramName).append("\" required>\n");
        form.append("                <button type=\"submit\">Fetch Data</button>\n");
        form.append("            </form>\n");
        form.append("        </div>\n");
        
        return form.toString();
    }

    private String generateOutputSection(MiniLanguageParser.Layout layout) {
        StringBuilder output = new StringBuilder();
        output.append("        <div class=\"output-section\" id=\"output\" style=\"display: none;\">\n");
        output.append("            <h2>Results</h2>\n");
        output.append("            <div class=\"layout-content\">\n");
        
        // Process the layout content to make it HTML-friendly
        String processedContent = processLayoutForHtml(layout.getContent());
        output.append(processedContent);
        
        output.append("            </div>\n");
        output.append("        </div>\n");
        
        return output.toString();
    }

    private String processLayoutForHtml(String layoutContent) {
        // Convert the layout content to HTML
        StringBuilder processed = new StringBuilder();
        
        String[] lines = layoutContent.split("\n");
        boolean inTable = false;
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            if (trimmedLine.startsWith("----") && trimmedLine.contains("List of courses")) {
                // Start of table section
                processed.append("                <h3>List of Courses</h3>\n");
                processed.append("                <table id=\"coursesTable\">\n");
                processed.append("                    <thead>\n");
                processed.append("                        <tr>\n");
                processed.append("                            <th>Course ID</th>\n");
                processed.append("                            <th>Course Name</th>\n");
                processed.append("                        </tr>\n");
                processed.append("                    </thead>\n");
                processed.append("                    <tbody id=\"coursesTableBody\">\n");
                processed.append("                        <!-- Courses will be populated here -->\n");
                processed.append("                    </tbody>\n");
                processed.append("                </table>\n");
                inTable = true;
            } else if (trimmedLine.startsWith("v_course_id") && trimmedLine.contains("v_course_name")) {
                // Skip the template line, we'll handle this in JavaScript
                continue;
            } else if (trimmedLine.startsWith("----") && inTable) {
                // End of table section
                inTable = false;
                continue;
            } else if (!trimmedLine.startsWith("----") && !inTable) {
                // Regular content line
                if (trimmedLine.contains("student name:") || trimmedLine.contains("student id:")) {
                    // Student info line
                    processed.append("                <p>").append(escapeHtml(trimmedLine)).append("</p>\n");
                } else if (!trimmedLine.isEmpty()) {
                    // Other content
                    processed.append("                <div>").append(escapeHtml(trimmedLine)).append("</div>\n");
                }
            }
        }
        
        return processed.toString();
    }

    private String generateJavaScript(MiniLanguageParser.ParsedScript parsedScript) {
        StringBuilder js = new StringBuilder();
        js.append("\n<script>\n");
        
        MiniLanguageParser.ApiDeclaration apiDeclaration = parsedScript.getApiDeclaration();
        
        if (!apiDeclaration.getUrl().isEmpty()) {
            js.append("document.getElementById('dataForm').addEventListener('submit', async function(e) {\n");
            js.append("    e.preventDefault();\n");
            js.append("    \n");
            
            String paramName = apiDeclaration.getParameter().replace("v_", "");
            js.append("    const ").append(paramName).append(" = document.getElementById('").append(paramName).append("').value;\n");
            js.append("    \n");
            js.append("    if (!").append(paramName).append(") {\n");
            js.append("        alert('Please enter a ").append(formatLabel(paramName)).append("');\n");
            js.append("        return;\n");
            js.append("    }\n");
            js.append("    \n");
            js.append("    try {\n");
            
            // Build the API URL
            String url = apiDeclaration.getUrl().replace("{" + paramName + "}", "' + " + paramName + " + '");
            js.append("        const response = await fetch(window.location.origin + '").append(url).append("');\n");
            js.append("        \n");
            js.append("        if (!response.ok) {\n");
            js.append("            throw new Error('Failed to fetch student data');\n");
            js.append("        }\n");
            js.append("        \n");
            js.append("        const data = await response.json();\n");
            js.append("        \n");
            js.append("        // Populate student data\n");
            js.append("        populateStudentData(data);\n");
            js.append("        \n");
            js.append("        // Show output section\n");
            js.append("        document.getElementById('output').style.display = 'block';\n");
            js.append("        \n");
            js.append("    } catch (error) {\n");
            js.append("        console.error('Error:', error);\n");
            js.append("        alert('Error fetching data: ' + error.message);\n");
            js.append("    }\n");
            js.append("});\n");
            js.append("\n");
            
            // Add function to populate student data
            js.append("function populateStudentData(student) {\n");
            js.append("    // Replace variables in student info\n");
            js.append("    const paragraphs = document.querySelectorAll('#output p');\n");
            js.append("    paragraphs.forEach(p => {\n");
            js.append("        let content = p.textContent;\n");
            js.append("        content = content.replace(/v_student\\.name/g, student.name);\n");
            js.append("        content = content.replace(/v_student_id/g, student.id);\n");
            js.append("        p.textContent = content;\n");
            js.append("    });\n");
            js.append("    \n");
            js.append("    // Populate courses table\n");
            js.append("    const coursesTableBody = document.getElementById('coursesTableBody');\n");
            js.append("    if (coursesTableBody && student.courses) {\n");
            js.append("        coursesTableBody.innerHTML = '';\n");
            js.append("        student.courses.forEach(course => {\n");
            js.append("            const row = document.createElement('tr');\n");
            js.append("            row.innerHTML = `\n");
            js.append("                <td>${course.id}</td>\n");
            js.append("                <td>${course.name}</td>\n");
            js.append("            `;\n");
            js.append("            coursesTableBody.appendChild(row);\n");
            js.append("        });\n");
            js.append("    }\n");
            js.append("}\n");
        }
        
        js.append("</script>\n");
        return js.toString();
    }

    private String formatLabel(String paramName) {
        // Convert camelCase to readable label
        return paramName.replaceAll("([a-z])([A-Z])", "$1 $2")
                        .toLowerCase()
                        .replaceAll("^.", String.valueOf(Character.toUpperCase(paramName.charAt(0))));
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
}