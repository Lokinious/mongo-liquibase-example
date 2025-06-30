package com.example.repository;

import com.example.model.PokemonCard;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.micronaut.context.annotation.Bean;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bson.Document;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Filters.*;

@Singleton
public class PokemonCardRepository {
    
    private final MongoCollection<PokemonCard> collection;
    
    @Inject
    public PokemonCardRepository(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("pokemon_db");
        this.collection = database.getCollection("pokemon_cards", PokemonCard.class);
    }
    
    public Mono<PokemonCard> save(PokemonCard card) {
        if (card.getId() == null) {
            return Mono.from(collection.insertOne(card))
                    .then(Mono.just(card));
        } else {
            return Mono.from(collection.replaceOne(eq("_id", card.getId()), card))
                    .then(Mono.just(card));
        }
    }
    
    public Mono<PokemonCard> findById(String id) {
        return Mono.from(collection.find(eq("_id", id)).first());
    }
    
    public Flux<PokemonCard> findAll() {
        return Flux.from(collection.find());
    }
    
    public Flux<PokemonCard> findByType(String type) {
        return Flux.from(collection.find(eq("type", type)));
    }
    
    public Flux<PokemonCard> findByRarity(String rarity) {
        return Flux.from(collection.find(eq("rarity", rarity)));
    }
    
    public Flux<PokemonCard> findBySet(String set) {
        return Flux.from(collection.find(eq("set", set)));
    }
    
    public Flux<PokemonCard> findByName(String name) {
        return Flux.from(collection.find(regex("name", ".*" + name + ".*", "i")));
    }
    
    public Mono<Void> deleteById(String id) {
        return Mono.from(collection.deleteOne(eq("_id", id))).then();
    }
    
    public Mono<Long> count() {
        return Mono.from(collection.estimatedDocumentCount());
    }
}
