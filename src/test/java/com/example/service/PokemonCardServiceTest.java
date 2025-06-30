package com.example.service;

import com.example.model.PokemonCard;
import com.example.repository.PokemonCardRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PokemonCardServiceTest implements TestPropertyProvider {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withExposedPorts(27017);

    @Inject
    PokemonCardService pokemonCardService;

    @Inject
    PokemonCardRepository pokemonCardRepository;

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
        StepVerifier.create(pokemonCardService.createCard(testCard))
                .assertNext(savedCard -> {
                    assertNotNull(savedCard.getId());
                    assertEquals(testCard.getName(), savedCard.getName());
                    assertEquals(testCard.getType(), savedCard.getType());
                    assertEquals(testCard.getHp(), savedCard.getHp());
                    assertNotNull(savedCard.getCreatedAt());
                    assertNotNull(savedCard.getUpdatedAt());
                })
                .verifyComplete();
    }

    @Test
    void testFindById() {
        // First create a card
        PokemonCard savedCard = pokemonCardService.createCard(testCard).block();
        assertNotNull(savedCard);

        StepVerifier.create(pokemonCardService.findById(savedCard.getId()))
                .assertNext(foundCard -> {
                    assertEquals(savedCard.getId(), foundCard.getId());
                    assertEquals(savedCard.getName(), foundCard.getName());
                })
                .verifyComplete();
    }

    @Test
    void testFindByIdNotFound() {
        StepVerifier.create(pokemonCardService.findById("nonexistent-id"))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testUpdateCard() {
        // First create a card
        PokemonCard savedCard = pokemonCardService.createCard(testCard).block();
        assertNotNull(savedCard);

        // Update the card
        PokemonCard updatedCard = new PokemonCard(
                "Raichu",
                "Electric",
                90,
                "Uncommon",
                "Base Set",
                new BigDecimal("45.00"),
                Arrays.asList("Static", "Thunder", "Lightning Bolt")
        );

        StepVerifier.create(pokemonCardService.updateCard(savedCard.getId(), updatedCard))
                .assertNext(result -> {
                    assertEquals(savedCard.getId(), result.getId());
                    assertEquals("Raichu", result.getName());
                    assertEquals(Integer.valueOf(90), result.getHp());
                    assertEquals("Uncommon", result.getRarity());
                    assertEquals(savedCard.getCreatedAt(), result.getCreatedAt());
                    assertTrue(result.getUpdatedAt().isAfter(savedCard.getUpdatedAt()));
                })
                .verifyComplete();
    }

    @Test
    void testDeleteCard() {
        // First create a card
        PokemonCard savedCard = pokemonCardService.createCard(testCard).block();
        assertNotNull(savedCard);

        StepVerifier.create(pokemonCardService.deleteCard(savedCard.getId()))
                .verifyComplete();

        // Verify the card is deleted
        StepVerifier.create(pokemonCardService.findById(savedCard.getId()))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testFindByType() {
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

        StepVerifier.create(pokemonCardService.findByType("Electric"))
                .assertNext(card -> assertEquals("Electric", card.getType()))
                .thenConsumeWhile(card -> "Electric".equals(card.getType()))
                .verifyComplete();
    }

    @Test
    void testFindByRarity() {
        pokemonCardService.createCard(testCard).block();

        StepVerifier.create(pokemonCardService.findByRarity("Common"))
                .assertNext(card -> assertEquals("Common", card.getRarity()))
                .thenConsumeWhile(card -> "Common".equals(card.getRarity()))
                .verifyComplete();
    }

    @Test
    void testSearchByName() {
        pokemonCardService.createCard(testCard).block();

        StepVerifier.create(pokemonCardService.searchByName("Pika"))
                .assertNext(card -> assertTrue(card.getName().toLowerCase().contains("pika")))
                .thenConsumeWhile(card -> card.getName().toLowerCase().contains("pika"))
                .verifyComplete();
    }

    @Test
    void testCountCards() {
        // Create a few cards
        pokemonCardService.createCard(testCard).block();

        PokemonCard anotherCard = new PokemonCard(
                "Charizard",
                "Fire",
                120,
                "Rare",
                "Base Set",
                new BigDecimal("100.00"),
                Arrays.asList("Blaze", "Fire Blast")
        );
        pokemonCardService.createCard(anotherCard).block();

        StepVerifier.create(pokemonCardService.countCards())
                .assertNext(count -> assertTrue(count >= 2))
                .verifyComplete();
    }
}
