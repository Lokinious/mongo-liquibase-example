package com.example.service;

import com.example.model.CardOwner;
import com.example.repository.CardOwnerRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Singleton
public class CardOwnerService {
    
    private final CardOwnerRepository cardOwnerRepository;
    
    @Inject
    public CardOwnerService(CardOwnerRepository cardOwnerRepository) {
        this.cardOwnerRepository = cardOwnerRepository;
    }
    
    public Mono<CardOwner> createOwner(CardOwner owner) {
        owner.setCreatedAt(LocalDateTime.now());
        owner.setUpdatedAt(LocalDateTime.now());
        return cardOwnerRepository.save(owner);
    }
    
    public Mono<CardOwner> updateOwner(String id, CardOwner updatedOwner) {
        return cardOwnerRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Owner not found with id: " + id)))
                .map(existingOwner -> {
                    updatedOwner.setId(existingOwner.getId());
                    updatedOwner.setCreatedAt(existingOwner.getCreatedAt());
                    updatedOwner.setUpdatedAt(LocalDateTime.now());
                    return updatedOwner;
                })
                .flatMap(cardOwnerRepository::save);
    }
    
    public Mono<CardOwner> findById(String id) {
        return cardOwnerRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Owner not found with id: " + id)));
    }
    
    public Flux<CardOwner> findAll() {
        return cardOwnerRepository.findAll();
    }
    
    public Mono<CardOwner> findByEmail(String email) {
        return cardOwnerRepository.findByEmail(email);
    }
    
    public Flux<CardOwner> findByLastName(String lastName) {
        return cardOwnerRepository.findByLastName(lastName);
    }
    
    public Flux<CardOwner> findOwnersByCardId(String cardId) {
        return cardOwnerRepository.findByOwnedCardId(cardId);
    }
    
    public Mono<Void> deleteOwner(String id) {
        return cardOwnerRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Owner not found with id: " + id)))
                .flatMap(owner -> cardOwnerRepository.deleteById(id));
    }
    
    public Mono<Long> countOwners() {
        return cardOwnerRepository.count();
    }
    
    public Mono<CardOwner> addCardToOwner(String ownerId, String cardId) {
        return cardOwnerRepository.findById(ownerId)
                .switchIfEmpty(Mono.error(new RuntimeException("Owner not found with id: " + ownerId)))
                .map(owner -> {
                    if (owner.getOwnedCardIds() != null && !owner.getOwnedCardIds().contains(cardId)) {
                        owner.getOwnedCardIds().add(cardId);
                        owner.setUpdatedAt(LocalDateTime.now());
                    }
                    return owner;
                })
                .flatMap(cardOwnerRepository::save);
    }
    
    public Mono<CardOwner> removeCardFromOwner(String ownerId, String cardId) {
        return cardOwnerRepository.findById(ownerId)
                .switchIfEmpty(Mono.error(new RuntimeException("Owner not found with id: " + ownerId)))
                .map(owner -> {
                    if (owner.getOwnedCardIds() != null) {
                        owner.getOwnedCardIds().remove(cardId);
                        owner.setUpdatedAt(LocalDateTime.now());
                    }
                    return owner;
                })
                .flatMap(cardOwnerRepository::save);
    }
}
