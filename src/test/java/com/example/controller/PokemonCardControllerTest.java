package com.example.controller;

import com.example.model.PokemonCard;
import com.example.service.PokemonCardService;
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
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PokemonCardControllerTest implements TestPropertyProvider {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withExposedPorts(27017);

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    PokemonCardService pokemonCardService;

    private PokemonCard testCard;

    @Override
    public Map<String, String> getProperties() {
        mongoDBContainer.start();
        return Map.of(
                "mongodb.uri", mongoDBContainer.getReplicaSetUrl("pokemon_test_db")
        );
    }

    @BeforeEach
    void setUp() {
        testCard = new PokemonCard(
                "Pikachu",
                "Electric",
                60,
                "Common",
                "Base Set",
                new BigDecimal("25.00"),
                Arrays.asList("Static", "Thunder Shock")
        );
    }

    @Test
    void testCreateCard() {
        var response = client.toBlocking().exchange(
                HttpRequest.POST("/api/cards", testCard),
                PokemonCard.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());

        PokemonCard createdCard = response.getBody().get();
        assertNotNull(createdCard.getId());
        assertEquals(testCard.getName(), createdCard.getName());
        assertEquals(testCard.getType(), createdCard.getType());
        assertEquals(testCard.getHp(), createdCard.getHp());
        assertNotNull(createdCard.getCreatedAt());
        assertNotNull(createdCard.getUpdatedAt());
    }

    @Test
    void testGetAllCards() {
        // First create a card
        PokemonCard savedCard = pokemonCardService.createCard(testCard).block();

        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/cards"),
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        List<?> cards = response.getBody().get();
        assertFalse(cards.isEmpty());
    }

    @Test
    void testGetCardById() {
        // First create a card
        PokemonCard savedCard = pokemonCardService.createCard(testCard).block();
        assertNotNull(savedCard);

        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/cards/" + savedCard.getId()),
                PokemonCard.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        
        PokemonCard retrievedCard = response.getBody().get();
        assertEquals(savedCard.getId(), retrievedCard.getId());
        assertEquals(savedCard.getName(), retrievedCard.getName());
    }

    @Test
    void testGetCardByIdNotFound() {
        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/cards/nonexistent-id"),
                PokemonCard.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void testUpdateCard() {
        // First create a card
        PokemonCard savedCard = pokemonCardService.createCard(testCard).block();
        assertNotNull(savedCard);

        // Update the card
        savedCard.setName("Raichu");
        savedCard.setHp(90);

        var response = client.toBlocking().exchange(
                HttpRequest.PUT("/api/cards/" + savedCard.getId(), savedCard),
                PokemonCard.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        
        PokemonCard updatedCard = response.getBody().get();
        assertEquals("Raichu", updatedCard.getName());
        assertEquals(Integer.valueOf(90), updatedCard.getHp());
    }

    @Test
    void testDeleteCard() {
        // First create a card
        PokemonCard savedCard = pokemonCardService.createCard(testCard).block();
        assertNotNull(savedCard);

        var response = client.toBlocking().exchange(
                HttpRequest.DELETE("/api/cards/" + savedCard.getId())
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());

        // Verify the card is deleted
        var getResponse = client.toBlocking().exchange(
                HttpRequest.GET("/api/cards/" + savedCard.getId()),
                PokemonCard.class
        );
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatus());
    }

    @Test
    void testSearchCardsByType() {
        // Create cards with different types
        pokemonCardService.createCard(testCard).block();
        
        PokemonCard fireCard = new PokemonCard(
                "Charizard",
                "Fire",
                120,
                "Rare",
                "Base Set",
                new BigDecimal("100.00"),
                Arrays.asList("Blaze", "Fire Blast")
        );
        pokemonCardService.createCard(fireCard).block();

        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/cards/search/type/Electric"),
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        List<?> cards = response.getBody().get();
        assertFalse(cards.isEmpty());
    }

    @Test
    void testSearchCardsByName() {
        // Create a card
        pokemonCardService.createCard(testCard).block();

        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/cards/search/name?name=Pika"),
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        List<?> cards = response.getBody().get();
        assertFalse(cards.isEmpty());
    }

    @Test
    void testGetCardCount() {
        // Create a card
        pokemonCardService.createCard(testCard).block();

        var response = client.toBlocking().exchange(
                HttpRequest.GET("/api/cards/count"),
                Long.class
        );

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        Long count = response.getBody().get();
        assertTrue(count > 0);
    }
}
