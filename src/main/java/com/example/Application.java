package com.example;

import io.micronaut.runtime.Micronaut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        LOG.info("🚀 Starting Pokemon Card Service with MongoDB and Liquibase...");
        LOG.info("📦 This service demonstrates Liquibase schema management with MongoDB");
        LOG.info("🔍 Index verification will run after startup to prove Liquibase is working");
        
        Micronaut.run(Application.class, args);
        
        LOG.info("✅ Pokemon Card Service started successfully!");
        LOG.info("🌐 Access the service at: http://localhost:8080");
        LOG.info("📊 View index verification at: http://localhost:8080/api/admin/indexes");
        LOG.info("💊 Health check at: http://localhost:8080/health");
    }
}
