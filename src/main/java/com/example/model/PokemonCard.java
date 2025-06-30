package com.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Introspected
@Serdeable
public class PokemonCard {
    
    @JsonProperty("_id")
    private String id;
    
    private String name;
    private String type;
    private Integer hp;
    private String rarity;
    private String set;
    private BigDecimal marketPrice;
    private List<String> abilities;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public PokemonCard() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public PokemonCard(String name, String type, Integer hp, String rarity, String set, 
                      BigDecimal marketPrice, List<String> abilities) {
        this();
        this.name = name;
        this.type = type;
        this.hp = hp;
        this.rarity = rarity;
        this.set = set;
        this.marketPrice = marketPrice;
        this.abilities = abilities;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Integer getHp() {
        return hp;
    }
    
    public void setHp(Integer hp) {
        this.hp = hp;
    }
    
    public String getRarity() {
        return rarity;
    }
    
    public void setRarity(String rarity) {
        this.rarity = rarity;
    }
    
    public String getSet() {
        return set;
    }
    
    public void setSet(String set) {
        this.set = set;
    }
    
    public BigDecimal getMarketPrice() {
        return marketPrice;
    }
    
    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }
    
    public List<String> getAbilities() {
        return abilities;
    }
    
    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "PokemonCard{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", hp=" + hp +
                ", rarity='" + rarity + '\'' +
                ", set='" + set + '\'' +
                ", marketPrice=" + marketPrice +
                ", abilities=" + abilities +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
