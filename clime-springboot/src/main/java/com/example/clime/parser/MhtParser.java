package com.example.clime.parser;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.BodyPart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
public class MhtParser {

    public MhtData parseMhtFile(Path mhtFile) throws IOException, MessagingException {
        byte[] content = Files.readAllBytes(mhtFile);
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(content));
        
        String htmlContent = extractHtmlContent(message);
        Document doc = Jsoup.parse(htmlContent);
        
        return new MhtData(
            extractApiData(doc),
            extractDataSheet(doc),
            extractLayoutSheet(doc)
        );
    }
    
    private String extractHtmlContent(MimeMessage message) throws MessagingException, IOException {
        if (message.isMimeType("text/html")) {
            return (String) message.getContent();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                if (part.isMimeType("text/html")) {
                    return (String) part.getContent();
                }
            }
        }
        return "";
    }
    
    private ApiData extractApiData(Document doc) {
        Element apiDiv = doc.getElementById("api");
        if (apiDiv == null) return new ApiData();
        
        Elements rows = apiDiv.select("table tr");
        String url = "", method = "GET", parameter = "";
        
        for (Element row : rows) {
            Elements cells = row.select("td");
            if (cells.size() == 2) {
                String key = cells.get(0).text();
                String value = cells.get(1).text();
                switch (key) {
                    case "url" -> url = value;
                    case "method" -> method = value;
                    case "parameter" -> parameter = value;
                }
            }
        }
        
        return new ApiData(url, method, parameter);
    }
    
    private List<Map<String, String>> extractDataSheet(Document doc) {
        Element dataDiv = doc.getElementById("data");
        if (dataDiv == null) return new ArrayList<>();
        
        Elements rows = dataDiv.select("table tr");
        if (rows.isEmpty()) return new ArrayList<>();
        
        // First row contains headers
        List<String> headers = new ArrayList<>();
        Elements headerCells = rows.get(0).select("td");
        for (Element cell : headerCells) {
            headers.add(cell.text());
        }
        
        List<Map<String, String>> data = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            Elements cells = rows.get(i).select("td");
            Map<String, String> record = new HashMap<>();
            for (int j = 0; j < Math.min(headers.size(), cells.size()); j++) {
                record.put(headers.get(j), cells.get(j).text());
            }
            data.add(record);
        }
        
        return data;
    }
    
    private String extractLayoutSheet(Document doc) {
        Element layoutDiv = doc.getElementById("layout");
        return layoutDiv != null ? layoutDiv.html() : "";
    }
    
    public static class MhtData {
        private final ApiData apiData;
        private final List<Map<String, String>> dataSheet;
        private final String layoutSheet;
        
        public MhtData(ApiData apiData, List<Map<String, String>> dataSheet, String layoutSheet) {
            this.apiData = apiData;
            this.dataSheet = dataSheet;
            this.layoutSheet = layoutSheet;
        }
        
        public ApiData getApiData() { return apiData; }
        public List<Map<String, String>> getDataSheet() { return dataSheet; }
        public String getLayoutSheet() { return layoutSheet; }
    }
    
    public static class ApiData {
        private final String url;
        private final String method;
        private final String parameter;
        
        public ApiData() {
            this("", "GET", "");
        }
        
        public ApiData(String url, String method, String parameter) {
            this.url = url;
            this.method = method;
            this.parameter = parameter;
        }
        
        public String getUrl() { return url; }
        public String getMethod() { return method; }
        public String getParameter() { return parameter; }
    }
}