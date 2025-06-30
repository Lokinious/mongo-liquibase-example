#!/bin/bash

# Create initial database and collections
mongosh --eval "
use pokemon_db;

// Create collections if they don't exist
db.createCollection('pokemon_cards');
db.createCollection('card_owners');

// Create indexes
db.pokemon_cards.createIndex({ 'name': 1 });
db.pokemon_cards.createIndex({ 'type': 1 });
db.pokemon_cards.createIndex({ 'rarity': 1 });
db.pokemon_cards.createIndex({ 'set': 1 });

db.card_owners.createIndex({ 'email': 1 }, { unique: true });
db.card_owners.createIndex({ 'lastName': 1 });
db.card_owners.createIndex({ 'ownedCardIds': 1 });

print('Database initialization completed');
"
