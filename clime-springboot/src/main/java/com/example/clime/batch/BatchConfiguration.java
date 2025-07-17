package com.example.clime.batch;

import com.example.clime.generator.HtmlGenerator;
import com.example.clime.parser.MhtParser;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Configuration
public class BatchConfiguration {

    @Autowired
    private MhtParser mhtParser;
    
    @Autowired
    private HtmlGenerator htmlGenerator;

    @Bean
    public Job mhtProcessingJob(JobRepository jobRepository, Step mhtProcessingStep) {
        return new JobBuilder("mhtProcessingJob", jobRepository)
                .start(mhtProcessingStep)
                .build();
    }

    @Bean
    public Step mhtProcessingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("mhtProcessingStep", jobRepository)
                .tasklet(mhtProcessingTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet mhtProcessingTasklet() {
        return (contribution, chunkContext) -> {
            String fileName = (String) chunkContext.getStepContext()
                    .getJobParameters().get("fileName");
            
            if (fileName == null) {
                throw new IllegalArgumentException("fileName parameter is required");
            }
            
            try {
                processMhtFile(fileName);
                return RepeatStatus.FINISHED;
            } catch (Exception e) {
                throw new RuntimeException("Error processing MHT file: " + fileName, e);
            }
        };
    }
    
    private void processMhtFile(String fileName) throws Exception {
        Path inputFile = Paths.get("input", fileName);
        Path processedDir = Paths.get("processed");
        Path generatedDir = Paths.get("src/main/resources/static/generated");
        
        // Ensure directories exist
        Files.createDirectories(processedDir);
        Files.createDirectories(generatedDir);
        
        // Parse MHT file
        MhtParser.MhtData mhtData = mhtParser.parseMhtFile(inputFile);
        
        // Generate HTML
        String htmlContent = htmlGenerator.generateHtml(mhtData, fileName);
        
        // Save generated HTML
        String htmlFileName = fileName.replaceAll("\\.(mht|mhtml)$", ".html");
        Path outputPath = generatedDir.resolve(htmlFileName);
        Files.write(outputPath, htmlContent.getBytes());
        
        // Move processed file
        Path processedPath = processedDir.resolve(fileName);
        Files.move(inputFile, processedPath, StandardCopyOption.REPLACE_EXISTING);
        
        System.out.println("Successfully processed " + fileName + " -> " + htmlFileName);
    }
}