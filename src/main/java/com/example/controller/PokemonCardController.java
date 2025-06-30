package com.example.controller;

import com.example.model.PokemonCard;
import com.example.service.PokemonCardService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Controller("/api/cards")
@Validated
public class PokemonCardController {
    
    private final PokemonCardService pokemonCardService;
    
    @Inject
    public PokemonCardController(PokemonCardService pokemonCardService) {
        this.pokemonCardService = pokemonCardService;
    }
    
    @Post
    public Mono<HttpResponse<PokemonCard>> createCard(@Body @Valid PokemonCard card) {
        return pokemonCardService.createCard(card)
                .map(c -> (HttpResponse<PokemonCard>) HttpResponse.created(c));
    }
    
    @Get
    public Flux<PokemonCard> getAllCards() {
        return pokemonCardService.findAll();
    }
    
    @Get("/{id}")
    public Mono<HttpResponse<PokemonCard>> getCard(@PathVariable @NotBlank String id) {
        return pokemonCardService.findById(id)
                .map(card -> (HttpResponse<PokemonCard>) HttpResponse.ok(card))
                .onErrorReturn(HttpResponse.notFound());
    }
    
    @Put("/{id}")
    public Mono<HttpResponse<PokemonCard>> updateCard(@PathVariable @NotBlank String id, 
                                                     @Body @Valid PokemonCard card) {
        return pokemonCardService.updateCard(id, card)
                .map(c -> (HttpResponse<PokemonCard>) HttpResponse.ok(c))
                .onErrorReturn(HttpResponse.notFound());
    }
    
    @Delete("/{id}")
    public Mono<HttpResponse<Object>> deleteCard(@PathVariable @NotBlank String id) {
        return pokemonCardService.deleteCard(id)
                .then(Mono.just((HttpResponse<Object>) HttpResponse.noContent()))
                .onErrorReturn(HttpResponse.notFound());
    }
    
    @Get("/search/type/{type}")
    public Flux<PokemonCard> getCardsByType(@PathVariable @NotBlank String type) {
        return pokemonCardService.findByType(type);
    }
    
    @Get("/search/rarity/{rarity}")
    public Flux<PokemonCard> getCardsByRarity(@PathVariable @NotBlank String rarity) {
        return pokemonCardService.findByRarity(rarity);
    }
    
    @Get("/search/set/{set}")
    public Flux<PokemonCard> getCardsBySet(@PathVariable @NotBlank String set) {
        return pokemonCardService.findBySet(set);
    }
    
    @Get("/search/name")
    public Flux<PokemonCard> searchCardsByName(@QueryValue @NotBlank String name) {
        return pokemonCardService.searchByName(name);
    }
    
    @Get("/count")
    public Mono<Long> getCardCount() {
        return pokemonCardService.countCards();
    }
}
