package com.example.controller;

import com.example.service.IndexVerificationService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import jakarta.inject.Inject;
import org.bson.Document;
import reactor.core.publisher.Mono;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@Controller("/api/admin")
public class AdminController {

    private final IndexVerificationService indexVerificationService;

    @Inject
    public AdminController(IndexVerificationService indexVerificationService) {
        this.indexVerificationService = indexVerificationService;
    }

    @Get("/indexes/{collection}")
    public Mono<HttpResponse<Document>> getCollectionIndexes(@PathVariable @NotBlank String collection) {
        if (!collection.equals("pokemon_cards") && !collection.equals("card_owners")) {
            return Mono.just(HttpResponse.badRequest());
        }
        
        return indexVerificationService.getIndexStats(collection)
            .map(doc -> (HttpResponse<Document>) HttpResponse.ok(doc))
            .onErrorReturn(HttpResponse.serverError());
    }

    @Get("/indexes")
    public Mono<HttpResponse<Map<String, Object>>> getAllIndexes() {
        return indexVerificationService.getIndexStats("pokemon_cards")
            .zipWith(indexVerificationService.getIndexStats("card_owners"))
            .map(tuple -> {
                Map<String, Object> result = Map.of(
                    "pokemon_cards", tuple.getT1(),
                    "card_owners", tuple.getT2(),
                    "message", "Indexes retrieved successfully - proves Liquibase is working!"
                );
                return (HttpResponse<Map<String, Object>>) HttpResponse.ok(result);
            })
            .onErrorReturn(HttpResponse.serverError());
    }
}
