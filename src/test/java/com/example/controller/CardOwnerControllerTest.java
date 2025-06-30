package com.example.controller;

import com.example.model.CardOwner;
import com.example.service.CardOwnerService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CardOwnerControllerTest implements TestPropertyProvider {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withExposedPorts(27017);

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    CardOwnerService cardOwnerService;

    private CardOwner testOwner;

    @Override
    public Map<String, String> getProperties() {
        mongoDBContainer.start();
        return Map.of(
                "mongodb.uri", mongoDBContainer.getReplicaSetUrl("pokemon_test_db")
        );
    }

    @BeforeEach
    void setUp() {
        CardOwner.Address address = new CardOwner.Address(
                "123 Pokemon St",
                "Pallet Town",
                "Kanto",
                "12345",
                "Pokemon World"
        );

        testOwner = new CardOwner(
                "Ash",
                "Ketchum",
                "ash.ketchum@pokemon.com",
                "555-0123",
                address,
                Arrays.asList("card1", "card2")
        );
    }

    @Test
    void testCreateOwner() {
        var response = client.toBlocking().exchange(
                HttpRequest.POST("/api/owners", testOwner),
                CardOwner.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());

        CardOwner createdOwner = response.getBody().get();
        assertNotNull(createdOwner.getId());
        assertEquals(testOwner.getFirstName(), createdOwner.getFirstName());
        assertEquals(testOwner.getLastName(), createdOwner.getLastName());
        assertEquals(testOwner.getEmail(), createdOwner.getEmail());
        assertNotNull(createdOwner.getCreatedAt());
        assertNotNull(createdOwner.getUpdatedAt());
    }

    @Test
    void testGetAllOwners() {
        // First create an owner
        CardOwner savedOwner = cardOwnerService.createOwner(testOwner).block();

        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/owners"),
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        List<?> owners = response.getBody().get();
        assertFalse(owners.isEmpty());
    }

    @Test
    void testGetOwnerById() {
        // First create an owner
        CardOwner savedOwner = cardOwnerService.createOwner(testOwner).block();
        assertNotNull(savedOwner);

        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/owners/" + savedOwner.getId()),
                CardOwner.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        
        CardOwner retrievedOwner = response.getBody().get();
        assertEquals(savedOwner.getId(), retrievedOwner.getId());
        assertEquals(savedOwner.getFirstName(), retrievedOwner.getFirstName());
    }

    @Test
    void testGetOwnerByIdNotFound() {
        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/owners/nonexistent-id"),
                CardOwner.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void testUpdateOwner() {
        // First create an owner
        CardOwner savedOwner = cardOwnerService.createOwner(testOwner).block();
        assertNotNull(savedOwner);

        // Update the owner
        savedOwner.setFirstName("Red");
        savedOwner.setPhoneNumber("555-9999");

        var response = client.toBlocking().exchange(
                HttpRequest.PUT("/api/owners/" + savedOwner.getId(), savedOwner),
                CardOwner.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        
        CardOwner updatedOwner = response.getBody().get();
        assertEquals("Red", updatedOwner.getFirstName());
        assertEquals("555-9999", updatedOwner.getPhoneNumber());
    }

    @Test
    void testDeleteOwner() {
        // First create an owner
        CardOwner savedOwner = cardOwnerService.createOwner(testOwner).block();
        assertNotNull(savedOwner);

        var response = client.toBlocking().exchange(
                HttpRequest.DELETE("/api/owners/" + savedOwner.getId())
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());

        // Verify the owner is deleted
        var getResponse = client.toBlocking().exchange(
                HttpRequest.GET("/api/owners/" + savedOwner.getId()),
                CardOwner.class
        );
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatus());
    }

    @Test
    void testGetOwnerByEmail() {
        // First create an owner
        CardOwner savedOwner = cardOwnerService.createOwner(testOwner).block();
        assertNotNull(savedOwner);

        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/owners/search/email?email=" + savedOwner.getEmail()),
                CardOwner.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        
        CardOwner retrievedOwner = response.getBody().get();
        assertEquals(savedOwner.getEmail(), retrievedOwner.getEmail());
    }

    @Test
    void testGetOwnersByLastName() {
        // Create owners with same last name
        cardOwnerService.createOwner(testOwner).block();
        
        CardOwner anotherOwner = new CardOwner(
                "Gary",
                "Ketchum", // Same last name
                "gary.ketchum@pokemon.com",
                "555-4567",
                testOwner.getAddress(),
                Arrays.asList("card3", "card4")
        );
        cardOwnerService.createOwner(anotherOwner).block();

        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/owners/search/lastname/Ketchum"),
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        List<?> owners = response.getBody().get();
        assertTrue(owners.size() >= 2);
    }

    @Test
    void testAddCardToOwner() {
        // First create an owner
        CardOwner savedOwner = cardOwnerService.createOwner(testOwner).block();
        assertNotNull(savedOwner);

        String newCardId = "new-card-123";

        var response = client.toBlocking().exchange(
                HttpRequest.POST("/api/owners/" + savedOwner.getId() + "/cards/" + newCardId, ""),
                CardOwner.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        
        CardOwner updatedOwner = response.getBody().get();
        assertTrue(updatedOwner.getOwnedCardIds().contains(newCardId));
    }

    @Test
    void testRemoveCardFromOwner() {
        // First create an owner with cards
        CardOwner savedOwner = cardOwnerService.createOwner(testOwner).block();
        assertNotNull(savedOwner);

        String cardToRemove = savedOwner.getOwnedCardIds().get(0);

        var response = client.toBlocking().exchange(
                HttpRequest.DELETE("/api/owners/" + savedOwner.getId() + "/cards/" + cardToRemove),
                CardOwner.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        
        CardOwner updatedOwner = response.getBody().get();
        assertFalse(updatedOwner.getOwnedCardIds().contains(cardToRemove));
    }

    @Test
    void testGetOwnerCount() {
        // Create an owner
        cardOwnerService.createOwner(testOwner).block();

        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/owners/count"),
                Long.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        Long count = response.getBody().get();
        assertTrue(count > 0);
    }
}
