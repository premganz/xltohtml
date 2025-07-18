package com.example.clime.integration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.File;
import java.time.Duration;

@Configuration
@EnableIntegration
public class FilePollingConfiguration {

    @Autowired
    private JobLauncher jobLauncher;
    
    @Autowired
    private Job mhtProcessingJob;

    @Bean
    public MessageChannel mhtFileInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageSource<File> mhtFileSource() {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File("input"));
        source.setFilter(new SimplePatternFileListFilter("*.mht"));
        return source;
    }

    @Bean
    public IntegrationFlow mhtFilePollingFlow() {
        return IntegrationFlow
                .from(mhtFileSource(), 
                      c -> c.poller(Pollers.fixedDelay(Duration.ofSeconds(10))))
                .channel(mhtFileInputChannel())
                .handle(mhtFileProcessor())
                .get();
    }

    @Bean
    @ServiceActivator(inputChannel = "mhtFileInputChannel")
    public MessageHandler mhtFileProcessor() {
        return message -> {
            try {
                File file = (File) message.getPayload();
                String fileName = file.getName();
                
                System.out.println("Processing MHT file: " + fileName);
                
                JobParameters jobParameters = new JobParametersBuilder()
                        .addString("fileName", fileName)
                        .addLong("timestamp", System.currentTimeMillis())
                        .toJobParameters();
                
                jobLauncher.run(mhtProcessingJob, jobParameters);
                
            } catch (Exception e) {
                System.err.println("Error processing file: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}