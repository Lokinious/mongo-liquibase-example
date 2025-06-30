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
import reactor.core.publisher.Mono;

import java.time.Duration;

@Singleton
@Requires(property = "app.init-db", value = "true", defaultValue = "true")
public class DatabaseInitializationService implements ApplicationEventListener<StartupEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseInitializationService.class);

    private final MongoClient mongoClient;

    @Inject
    public DatabaseInitializationService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public void onApplicationEvent(StartupEvent event) {
        LOG.info("🗄️ Initializing MongoDB schema (simulating Liquibase functionality)...");
        
        Mono.delay(Duration.ofSeconds(1))
            .then(initializeDatabase())
            .subscribe(
                unused -> LOG.info("✅ Database initialization completed successfully!"),
                error -> LOG.error("❌ Database initialization failed", error)
            );
    }

    private Mono<Void> initializeDatabase() {
        MongoDatabase database = mongoClient.getDatabase("pokemon_db");
        
        return createCollections(database)
            .then(createIndexes(database))
            .then();
    }

    private Mono<Void> createCollections(MongoDatabase database) {
        LOG.info("📦 Creating collections...");
        
        return Mono.from(database.createCollection("pokemon_cards"))
            .doOnSuccess(v -> LOG.info("✓ Created collection: pokemon_cards"))
            .onErrorResume(error -> {
                LOG.info("Collection pokemon_cards already exists or error occurred: {}", error.getMessage());
                return Mono.empty();
            })
            .then(Mono.from(database.createCollection("card_owners")))
            .doOnSuccess(v -> LOG.info("✓ Created collection: card_owners"))
            .onErrorResume(error -> {
                LOG.info("Collection card_owners already exists or error occurred: {}", error.getMessage());
                return Mono.empty();
            })
            .then();
    }

    private Mono<Void> createIndexes(MongoDatabase database) {
        LOG.info("🔍 Creating indexes (simulating Liquibase changesets)...");
        
        return createPokemonCardIndexes(database)
            .then(createCardOwnerIndexes(database))
            .then();
    }

    private Mono<Void> createPokemonCardIndexes(MongoDatabase database) {
        var collection = database.getCollection("pokemon_cards");
        
        return Mono.from(collection.createIndex(
                new Document("name", 1).append("set", 1),
                new com.mongodb.client.model.IndexOptions().unique(true)
            ))
            .doOnSuccess(indexName -> LOG.info("✓ Created unique index on pokemon_cards: name + set"))
            .then(Mono.from(collection.createIndex(
                new Document("type", 1).append("rarity", 1)
            )))
            .doOnSuccess(indexName -> LOG.info("✓ Created compound index on pokemon_cards: type + rarity"))
            .then(Mono.from(collection.createIndex(
                new Document("marketPrice", -1)
            )))
            .doOnSuccess(indexName -> LOG.info("✓ Created descending index on pokemon_cards: marketPrice"))
            .then(Mono.from(collection.createIndex(
                new Document("createdAt", -1)
            )))
            .doOnSuccess(indexName -> LOG.info("✓ Created descending index on pokemon_cards: createdAt"))
            .then();
    }

    private Mono<Void> createCardOwnerIndexes(MongoDatabase database) {
        var collection = database.getCollection("card_owners");
        
        return Mono.from(collection.createIndex(
                new Document("email", 1),
                new com.mongodb.client.model.IndexOptions().unique(true)
            ))
            .doOnSuccess(indexName -> LOG.info("✓ Created unique index on card_owners: email"))
            .then(Mono.from(collection.createIndex(
                new Document("lastName", 1).append("firstName", 1)
            )))
            .doOnSuccess(indexName -> LOG.info("✓ Created compound index on card_owners: lastName + firstName"))
            .then(Mono.from(collection.createIndex(
                new Document("ownedCardIds", 1)
            )))
            .doOnSuccess(indexName -> LOG.info("✓ Created index on card_owners: ownedCardIds"))
            .then();
    }
}
