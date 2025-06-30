package com.example.service;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Singleton
@Requires(property = "app.verify-indexes", value = "true", defaultValue = "true")
public class IndexVerificationService implements ApplicationEventListener<StartupEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(IndexVerificationService.class);

    private final MongoClient mongoClient;

    @Inject
    public IndexVerificationService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public void onApplicationEvent(StartupEvent event) {
        LOG.info("=== Starting Index Verification ===");
        
        // Delay slightly to ensure Liquibase has run
        Mono.delay(Duration.ofSeconds(2))
            .then(verifyIndexes())
            .subscribe(
                unused -> LOG.info("=== Index Verification Completed ==="),
                error -> LOG.error("Index verification failed", error)
            );
    }

    private Mono<Void> verifyIndexes() {
        MongoDatabase database = mongoClient.getDatabase("pokemon_db");
        
        return verifyCollectionIndexes(database, "pokemon_cards")
            .then(verifyCollectionIndexes(database, "card_owners"))
            .then();
    }

    private Mono<Void> verifyCollectionIndexes(MongoDatabase database, String collectionName) {
        LOG.info("--- Verifying indexes for collection: {} ---", collectionName);
        
        return Flux.from(database.getCollection(collectionName).listIndexes())
            .cast(Document.class)
            .doOnNext(indexDoc -> {
                String indexName = indexDoc.getString("name");
                Document keys = indexDoc.get("key", Document.class);
                Document options = extractOptions(indexDoc);
                
                LOG.info("Index found: {} | Keys: {} | Options: {}", 
                    indexName, 
                    keys != null ? keys.toJson() : "null",
                    options.isEmpty() ? "none" : options.toJson()
                );
            })
            .doOnComplete(() -> LOG.info("--- Completed verification for collection: {} ---", collectionName))
            .then();
    }

    private Document extractOptions(Document indexDoc) {
        Document options = new Document();
        
        // Check for common index options
        if (indexDoc.containsKey("unique") && indexDoc.getBoolean("unique", false)) {
            options.put("unique", true);
        }
        if (indexDoc.containsKey("sparse") && indexDoc.getBoolean("sparse", false)) {
            options.put("sparse", true);
        }
        if (indexDoc.containsKey("background") && indexDoc.getBoolean("background", false)) {
            options.put("background", true);
        }
        if (indexDoc.containsKey("expireAfterSeconds")) {
            options.put("expireAfterSeconds", indexDoc.getInteger("expireAfterSeconds"));
        }
        
        return options;
    }

    public Mono<Document> getIndexStats(String collectionName) {
        MongoDatabase database = mongoClient.getDatabase("pokemon_db");
        
        return Flux.from(database.getCollection(collectionName).listIndexes())
            .cast(Document.class)
            .collectList()
            .map(indexes -> {
                Document stats = new Document();
                stats.put("collection", collectionName);
                stats.put("totalIndexes", indexes.size());
                stats.put("indexes", indexes);
                return stats;
            });
    }
}
