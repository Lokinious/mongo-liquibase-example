package com.example.util;

import com.example.model.CardOwner;
import com.example.model.PokemonCard;
import com.example.service.CardOwnerService;
import com.example.service.PokemonCardService;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Singleton
@Requires(property = "app.seed-data", value = "true", defaultValue = "false")
public class DataSeeder implements ApplicationEventListener<StartupEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DataSeeder.class);

    private final PokemonCardService pokemonCardService;
    private final CardOwnerService cardOwnerService;

    @Inject
    public DataSeeder(PokemonCardService pokemonCardService, CardOwnerService cardOwnerService) {
        this.pokemonCardService = pokemonCardService;
        this.cardOwnerService = cardOwnerService;
    }

    @Override
    public void onApplicationEvent(StartupEvent event) {
        LOG.info("Seeding initial data...");
        
        seedPokemonCards();
        seedCardOwners();
        
        LOG.info("Data seeding completed!");
    }

    private void seedPokemonCards() {
        List<PokemonCard> cards = Arrays.asList(
            new PokemonCard(
                "Pikachu",
                "Electric",
                60,
                "Common",
                "Base Set",
                new BigDecimal("25.00"),
                Arrays.asList("Static", "Thunder Shock")
            ),
            new PokemonCard(
                "Charizard",
                "Fire",
                120,
                "Rare Holo",
                "Base Set",
                new BigDecimal("350.00"),
                Arrays.asList("Blaze", "Fire Blast")
            ),
            new PokemonCard(
                "Blastoise",
                "Water",
                100,
                "Rare Holo",
                "Base Set",
                new BigDecimal("150.00"),
                Arrays.asList("Torrent", "Hydro Pump")
            ),
            new PokemonCard(
                "Venusaur",
                "Grass",
                100,
                "Rare Holo",
                "Base Set",
                new BigDecimal("120.00"),
                Arrays.asList("Overgrow", "Solar Beam")
            ),
            new PokemonCard(
                "Mewtwo",
                "Psychic",
                120,
                "Rare Holo",
                "Base Set",
                new BigDecimal("200.00"),
                Arrays.asList("Pressure", "Psychic")
            )
        );

        cards.forEach(card -> {
            pokemonCardService.createCard(card)
                .doOnSuccess(savedCard -> LOG.info("Created card: {}", savedCard.getName()))
                .subscribe();
        });
    }

    private void seedCardOwners() {
        List<CardOwner> owners = Arrays.asList(
            new CardOwner(
                "Ash",
                "Ketchum",
                "ash.ketchum@pokemon.com",
                "555-0123",
                new CardOwner.Address("123 Pokemon St", "Pallet Town", "Kanto", "12345", "Pokemon World"),
                Arrays.asList()
            ),
            new CardOwner(
                "Gary",
                "Oak",
                "gary.oak@pokemon.com",
                "555-0456",
                new CardOwner.Address("456 Research Blvd", "Pallet Town", "Kanto", "12346", "Pokemon World"),
                Arrays.asList()
            ),
            new CardOwner(
                "Misty",
                "Waterflower",
                "misty.waterflower@pokemon.com",
                "555-0789",
                new CardOwner.Address("789 Gym Ave", "Cerulean City", "Kanto", "12347", "Pokemon World"),
                Arrays.asList()
            )
        );

        owners.forEach(owner -> {
            cardOwnerService.createOwner(owner)
                .doOnSuccess(savedOwner -> LOG.info("Created owner: {} {}", savedOwner.getFirstName(), savedOwner.getLastName()))
                .subscribe();
        });
    }
}
