package com.example.clime.metamodule;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class HtmlParser {

    public String parseHtmlFile(Path filePath) throws IOException {
        String content = Files.readString(filePath);
        Document doc = Jsoup.parse(content);
        
        // Basic parsing for Excel-generated HTML tables
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>\n")
                   .append("<html>\n")
                   .append("<head>\n")
                   .append("    <title>Parsed Data</title>\n")
                   .append("    <link rel=\"stylesheet\" href=\"/css/style.css\">\n")
                   .append("</head>\n")
                   .append("<body>\n")
                   .append("    <div class=\"container\">\n")
                   .append("        <h1>Data from ").append(filePath.getFileName()).append("</h1>\n");
        
        // Extract tables and convert them to a more web-friendly format
        Elements tables = doc.select("table");
        for (Element table : tables) {
            htmlBuilder.append("        <div class=\"table-container\">\n")
                       .append("            <table class=\"data-table\">\n");
            
            Elements rows = table.select("tr");
            for (Element row : rows) {
                htmlBuilder.append("                <tr>\n");
                Elements cells = row.select("td, th");
                for (Element cell : cells) {
                    String cellText = cell.text().trim();
                    htmlBuilder.append("                    <td>").append(cellText).append("</td>\n");
                }
                htmlBuilder.append("                </tr>\n");
            }
            
            htmlBuilder.append("            </table>\n")
                       .append("        </div>\n");
        }
        
        htmlBuilder.append("        <div class=\"api-section\">\n")
                   .append("            <h2>API Integration</h2>\n")
                   .append("            <button onclick=\"fetchStudentData('S001')\">Load Student S001</button>\n")
                   .append("            <button onclick=\"fetchStudentData('S002')\">Load Student S002</button>\n")
                   .append("            <button onclick=\"fetchStudentData('S003')\">Load Student S003</button>\n")
                   .append("            <div id=\"student-data\"></div>\n")
                   .append("        </div>\n")
                   .append("    </div>\n")
                   .append("    <script src=\"/js/app.js\"></script>\n")
                   .append("</body>\n")
                   .append("</html>");
        
        return htmlBuilder.toString();
    }
}