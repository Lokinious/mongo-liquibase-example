package com.example.service;

import com.example.model.PokemonCard;
import com.example.repository.PokemonCardRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Singleton
public class PokemonCardService {
    
    private final PokemonCardRepository pokemonCardRepository;
    
    @Inject
    public PokemonCardService(PokemonCardRepository pokemonCardRepository) {
        this.pokemonCardRepository = pokemonCardRepository;
    }
    
    public Mono<PokemonCard> createCard(PokemonCard card) {
        card.setCreatedAt(LocalDateTime.now());
        card.setUpdatedAt(LocalDateTime.now());
        return pokemonCardRepository.save(card);
    }
    
    public Mono<PokemonCard> updateCard(String id, PokemonCard updatedCard) {
        return pokemonCardRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Card not found with id: " + id)))
                .map(existingCard -> {
                    updatedCard.setId(existingCard.getId());
                    updatedCard.setCreatedAt(existingCard.getCreatedAt());
                    updatedCard.setUpdatedAt(LocalDateTime.now());
                    return updatedCard;
                })
                .flatMap(pokemonCardRepository::save);
    }
    
    public Mono<PokemonCard> findById(String id) {
        return pokemonCardRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Card not found with id: " + id)));
    }
    
    public Flux<PokemonCard> findAll() {
        return pokemonCardRepository.findAll();
    }
    
    public Flux<PokemonCard> findByType(String type) {
        return pokemonCardRepository.findByType(type);
    }
    
    public Flux<PokemonCard> findByRarity(String rarity) {
        return pokemonCardRepository.findByRarity(rarity);
    }
    
    public Flux<PokemonCard> findBySet(String set) {
        return pokemonCardRepository.findBySet(set);
    }
    
    public Flux<PokemonCard> searchByName(String name) {
        return pokemonCardRepository.findByName(name);
    }
    
    public Mono<Void> deleteCard(String id) {
        return pokemonCardRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Card not found with id: " + id)))
                .flatMap(card -> pokemonCardRepository.deleteById(id));
    }
    
    public Mono<Long> countCards() {
        return pokemonCardRepository.count();
    }
}
