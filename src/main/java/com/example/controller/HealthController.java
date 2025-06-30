package com.example.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Controller("/health")
public class HealthController {

    @Get
    public Mono<HttpResponse<Map<String, Object>>> health() {
        return Mono.just(HttpResponse.ok(Map.of(
                "status", "UP",
                "service", "pokemon-card-service",
                "timestamp", LocalDateTime.now(),
                "version", "0.1"
        )));
    }

    @Get("/readiness")
    public Mono<HttpResponse<Map<String, String>>> readiness() {
        return Mono.just(HttpResponse.ok(Map.of(
                "status", "READY"
        )));
    }

    @Get("/liveness")
    public Mono<HttpResponse<Map<String, String>>> liveness() {
        return Mono.just(HttpResponse.ok(Map.of(
                "status", "ALIVE"
        )));
    }
}
