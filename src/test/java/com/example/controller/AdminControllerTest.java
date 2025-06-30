package com.example.controller;

import com.example.service.IndexVerificationService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminControllerTest implements TestPropertyProvider {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withExposedPorts(27017);

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    IndexVerificationService indexVerificationService;

    @Override
    public Map<String, String> getProperties() {
        mongoDBContainer.start();
        return Map.of(
                "mongodb.uri", mongoDBContainer.getReplicaSetUrl("pokemon_test_db"),
                "app.verify-indexes", "true"
        );
    }

    @Test
    void testGetAllIndexes() {
        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/admin/indexes"),
                Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        
        Map<String, Object> result = response.getBody().get();
        assertTrue(result.containsKey("pokemon_cards"));
        assertTrue(result.containsKey("card_owners"));
        assertTrue(result.containsKey("message"));
        
        String message = (String) result.get("message");
        assertTrue(message.contains("Liquibase"));
    }

    @Test
    void testGetPokemonCardsIndexes() {
        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/admin/indexes/pokemon_cards"),
                Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        
        Map<String, Object> result = response.getBody().get();
        assertEquals("pokemon_cards", result.get("collection"));
        assertTrue(result.containsKey("totalIndexes"));
        assertTrue(result.containsKey("indexes"));
        
        // Should have at least the default _id index plus our custom ones
        Integer totalIndexes = (Integer) result.get("totalIndexes");
        assertTrue(totalIndexes >= 1, "Should have at least the default _id index");
    }

    @Test
    void testGetCardOwnersIndexes() {
        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/admin/indexes/card_owners"),
                Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        
        Map<String, Object> result = response.getBody().get();
        assertEquals("card_owners", result.get("collection"));
        assertTrue(result.containsKey("totalIndexes"));
        assertTrue(result.containsKey("indexes"));
        
        Integer totalIndexes = (Integer) result.get("totalIndexes");
        assertTrue(totalIndexes >= 1, "Should have at least the default _id index");
    }

    @Test
    void testGetInvalidCollectionIndexes() {
        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/admin/indexes/invalid_collection"),
                Map.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }
}
