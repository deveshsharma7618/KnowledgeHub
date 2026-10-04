package com.deveshsharma.knowledgehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the KnowledgeHub Spring Boot application.
 */
@SpringBootApplication
public class KnowledgeHubApplication {

    /**
     * Starts the embedded Spring application.
     *
     * @param args command-line arguments
     */
    static void main(String[] args) {
        SpringApplication.run(KnowledgeHubApplication.class, args);
    }

}
