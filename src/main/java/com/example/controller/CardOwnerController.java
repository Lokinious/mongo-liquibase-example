package com.example.controller;

import com.example.model.CardOwner;
import com.example.service.CardOwnerService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Controller("/api/owners")
@Validated
public class CardOwnerController {
    
    private final CardOwnerService cardOwnerService;
    
    @Inject
    public CardOwnerController(CardOwnerService cardOwnerService) {
        this.cardOwnerService = cardOwnerService;
    }
    
    @Post
    public Mono<HttpResponse<CardOwner>> createOwner(@Body @Valid CardOwner owner) {
        return cardOwnerService.createOwner(owner)
                .map(o -> (HttpResponse<CardOwner>) HttpResponse.created(o));
    }
    
    @Get
    public Flux<CardOwner> getAllOwners() {
        return cardOwnerService.findAll();
    }
    
    @Get("/{id}")
    public Mono<HttpResponse<CardOwner>> getOwner(@PathVariable @NotBlank String id) {
        return cardOwnerService.findById(id)
                .map(o -> (HttpResponse<CardOwner>) HttpResponse.ok(o))
                .onErrorReturn(HttpResponse.notFound());
    }
    
    @Put("/{id}")
    public Mono<HttpResponse<CardOwner>> updateOwner(@PathVariable @NotBlank String id, 
                                                    @Body @Valid CardOwner owner) {
        return cardOwnerService.updateOwner(id, owner)
                .map(o -> (HttpResponse<CardOwner>) HttpResponse.ok(o))
                .onErrorReturn(HttpResponse.notFound());
    }
    
    @Delete("/{id}")
    public Mono<HttpResponse<Object>> deleteOwner(@PathVariable @NotBlank String id) {
        return cardOwnerService.deleteOwner(id)
                .then(Mono.just((HttpResponse<Object>) HttpResponse.noContent()))
                .onErrorReturn(HttpResponse.notFound());
    }
    
    @Get("/search/email")
    public Mono<HttpResponse<CardOwner>> getOwnerByEmail(@QueryValue @NotBlank String email) {
        return cardOwnerService.findByEmail(email)
                .map(o -> (HttpResponse<CardOwner>) HttpResponse.ok(o))
                .switchIfEmpty(Mono.just(HttpResponse.notFound()));
    }
    
    @Get("/search/lastname/{lastName}")
    public Flux<CardOwner> getOwnersByLastName(@PathVariable @NotBlank String lastName) {
        return cardOwnerService.findByLastName(lastName);
    }
    
    @Get("/search/card/{cardId}")
    public Flux<CardOwner> getOwnersByCardId(@PathVariable @NotBlank String cardId) {
        return cardOwnerService.findOwnersByCardId(cardId);
    }
    
    @Post("/{ownerId}/cards/{cardId}")
    public Mono<HttpResponse<CardOwner>> addCardToOwner(@PathVariable @NotBlank String ownerId, 
                                                       @PathVariable @NotBlank String cardId) {
        return cardOwnerService.addCardToOwner(ownerId, cardId)
                .map(o -> (HttpResponse<CardOwner>) HttpResponse.ok(o))
                .onErrorReturn(HttpResponse.notFound());
    }
    
    @Delete("/{ownerId}/cards/{cardId}")
    public Mono<HttpResponse<CardOwner>> removeCardFromOwner(@PathVariable @NotBlank String ownerId, 
                                                            @PathVariable @NotBlank String cardId) {
        return cardOwnerService.removeCardFromOwner(ownerId, cardId)
                .map(o -> (HttpResponse<CardOwner>) HttpResponse.ok(o))
                .onErrorReturn(HttpResponse.notFound());
    }
    
    @Get("/count")
    public Mono<Long> getOwnerCount() {
        return cardOwnerService.countOwners();
    }
}
