package com.example.repository;

import com.example.model.CardOwner;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Filters.*;

@Singleton
public class CardOwnerRepository {
    
    private final MongoCollection<CardOwner> collection;
    
    @Inject
    public CardOwnerRepository(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("pokemon_db");
        this.collection = database.getCollection("card_owners", CardOwner.class);
    }
    
    public Mono<CardOwner> save(CardOwner owner) {
        if (owner.getId() == null) {
            return Mono.from(collection.insertOne(owner))
                    .then(Mono.just(owner));
        } else {
            return Mono.from(collection.replaceOne(eq("_id", owner.getId()), owner))
                    .then(Mono.just(owner));
        }
    }
    
    public Mono<CardOwner> findById(String id) {
        return Mono.from(collection.find(eq("_id", id)).first());
    }
    
    public Flux<CardOwner> findAll() {
        return Flux.from(collection.find());
    }
    
    public Mono<CardOwner> findByEmail(String email) {
        return Mono.from(collection.find(eq("email", email)).first());
    }
    
    public Flux<CardOwner> findByLastName(String lastName) {
        return Flux.from(collection.find(eq("lastName", lastName)));
    }
    
    public Flux<CardOwner> findByOwnedCardId(String cardId) {
        return Flux.from(collection.find(in("ownedCardIds", cardId)));
    }
    
    public Mono<Void> deleteById(String id) {
        return Mono.from(collection.deleteOne(eq("_id", id))).then();
    }
    
    public Mono<Long> count() {
        return Mono.from(collection.estimatedDocumentCount());
    }
}
