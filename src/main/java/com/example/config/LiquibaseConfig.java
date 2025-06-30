package com.example.config;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class LiquibaseConfig {
    
    private static final Logger LOG = LoggerFactory.getLogger(LiquibaseConfig.class);
    
    public LiquibaseConfig() {
        LOG.info("LiquibaseConfig initialized - MongoDB schema management will be handled manually for now");
        LOG.info("In production, you would configure Liquibase with the MongoDB extension");
    }
    
    // Note: Liquibase MongoDB extension configuration commented out until dependency is available
    // @Bean
    // @Singleton
    // public Liquibase liquibase() { ... }
}
