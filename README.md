# Pokemon Card Management Microservice

A comprehensive Micronaut microservice example demonstrating MongoDB integration with Liquibase for schema management. This project showcases a Pokemon card collection management system with full CRUD operations, search capabilities, and comprehensive testing.

## Features

- 🃏 **Pokemon Card Management**: Create, read, update, and delete Pokemon cards
- 👤 **Owner Management**: Manage card owners and their collections
- 🔍 **Advanced Search**: Search by type, rarity, set, name, and more
- 🗄️ **MongoDB Integration**: Reactive MongoDB operations with proper indexing
- 📊 **Schema Management**: Liquibase integration for MongoDB collections and indexes
- 🐳 **Docker Support**: Complete Docker containerization for local development
- 🧪 **Comprehensive Testing**: Unit and integration tests with Testcontainers
- ⚡ **Reactive Programming**: Built with Reactor for non-blocking operations

## Technology Stack

- **Framework**: Micronaut 4.2.1
- **Database**: MongoDB 7.0
- **Schema Management**: Liquibase with MongoDB extension
- **Testing**: JUnit 5, Testcontainers, Reactor Test
- **Build Tool**: Gradle 8.5
- **Java Version**: 17
- **Containerization**: Docker & Docker Compose

## Project Structure

```
src/
├── main/
│   ├── java/com/example/
│   │   ├── Application.java                 # Main application class
│   │   ├── controller/                      # REST controllers
│   │   │   ├── PokemonCardController.java
│   │   │   └── CardOwnerController.java
│   │   ├── service/                         # Business logic
│   │   │   ├── PokemonCardService.java
│   │   │   └── CardOwnerService.java
│   │   ├── repository/                      # Data access layer
│   │   │   ├── PokemonCardRepository.java
│   │   │   └── CardOwnerRepository.java
│   │   ├── model/                           # Domain models
│   │   │   ├── PokemonCard.java
│   │   │   └── CardOwner.java
│   │   └── config/
│   │       └── LiquibaseConfig.java         # Liquibase configuration
│   └── resources/
│       ├── application.yml                  # Application configuration
│       └── db/changelog/
│           └── db.changelog-master.json     # Liquibase changelog
└── test/                                    # Comprehensive test suite
```

## Quick Start

### Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Git

### Running with Docker (Recommended)

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd mongo-liquibase-example
   ```

2. **Start the application with Docker Compose**:
   ```bash
   docker-compose up -d
   ```

   This will start:
   - MongoDB on port 27017
   - Pokemon Card Service on port 8080

3. **Verify the application is running**:
   ```bash
   curl http://localhost:8080/api/cards/count
   ```

### Running Locally

1. **Start MongoDB**:
   ```bash
   docker run -d --name mongodb -p 27017:27017 mongo:7.0
   ```

2. **Build and run the application**:
   ```bash
   ./gradlew run
   ```

### Running Tests

Run the complete test suite:
```bash
./gradlew test
```

Run specific test classes:
```bash
./gradlew test --tests "PokemonCardControllerTest"
```

## API Documentation

### Pokemon Cards API

#### Create a Pokemon Card
```http
POST /api/cards
Content-Type: application/json

{
  "name": "Pikachu",
  "type": "Electric",
  "hp": 60,
  "rarity": "Common",
  "set": "Base Set",
  "marketPrice": 25.00,
  "abilities": ["Static", "Thunder Shock"]
}
```

#### Get All Cards
```http
GET /api/cards
```

#### Get Card by ID
```http
GET /api/cards/{id}
```

#### Update Card
```http
PUT /api/cards/{id}
Content-Type: application/json

{
  "name": "Raichu",
  "type": "Electric",
  "hp": 90,
  "rarity": "Uncommon",
  "set": "Base Set",
  "marketPrice": 45.00,
  "abilities": ["Static", "Thunder", "Lightning Bolt"]
}
```

#### Delete Card
```http
DELETE /api/cards/{id}
```

#### Search Cards
```http
GET /api/cards/search/type/{type}        # Search by type
GET /api/cards/search/rarity/{rarity}    # Search by rarity
GET /api/cards/search/set/{set}          # Search by set
GET /api/cards/search/name?name={name}   # Search by name
```

#### Get Card Count
```http
GET /api/cards/count
```

### Card Owners API

#### Create Owner
```http
POST /api/owners
Content-Type: application/json

{
  "firstName": "Ash",
  "lastName": "Ketchum",
  "email": "ash.ketchum@pokemon.com",
  "phoneNumber": "555-0123",
  "address": {
    "street": "123 Pokemon St",
    "city": "Pallet Town",
    "state": "Kanto",
    "zipCode": "12345",
    "country": "Pokemon World"
  },
  "ownedCardIds": ["card1", "card2"]
}
```

#### Get All Owners
```http
GET /api/owners
```

#### Get Owner by ID
```http
GET /api/owners/{id}
```

#### Update Owner
```http
PUT /api/owners/{id}
```

#### Delete Owner
```http
DELETE /api/owners/{id}
```

#### Search Owners
```http
GET /api/owners/search/email?email={email}           # Find by email
GET /api/owners/search/lastname/{lastName}           # Find by last name
GET /api/owners/search/card/{cardId}                 # Find owners of a card
```

#### Manage Owner's Cards
```http
POST /api/owners/{ownerId}/cards/{cardId}     # Add card to owner
DELETE /api/owners/{ownerId}/cards/{cardId}   # Remove card from owner
```

## Database Schema

### Pokemon Cards Collection
```javascript
{
  "_id": "ObjectId",
  "name": "String",
  "type": "String",
  "hp": "Number",
  "rarity": "String",
  "set": "String",
  "marketPrice": "Decimal",
  "abilities": ["String"],
  "createdAt": "DateTime",
  "updatedAt": "DateTime"
}
```

**Indexes**:
- `_id` (unique)
- `name`
- `type`
- `rarity`
- `set`

### Card Owners Collection
```javascript
{
  "_id": "ObjectId",
  "firstName": "String",
  "lastName": "String",
  "email": "String",
  "phoneNumber": "String",
  "address": {
    "street": "String",
    "city": "String",
    "state": "String",
    "zipCode": "String",
    "country": "String"
  },
  "ownedCardIds": ["String"],
  "createdAt": "DateTime",
  "updatedAt": "DateTime"
}
```

**Indexes**:
- `_id` (unique)
- `email` (unique)
- `lastName`
- `ownedCardIds`

## Index Verification - Proving Liquibase Works! 🔍

This service includes built-in verification to prove that Liquibase is successfully creating indexes in MongoDB (not just relying on MongoDB's default `_id` indexes).

### Automatic Verification on Startup

When the application starts, you'll see detailed logs showing all indexes:

```
🔍 Index verification will run after startup to prove Liquibase is working
--- Verifying indexes for collection: pokemon_cards ---
Index found: _id_ | Keys: {"_id":1} | Options: none
Index found: name_1_set_1 | Keys: {"name":1,"set":1} | Options: {"unique":true}
Index found: type_1_rarity_1 | Keys: {"type":1,"rarity":1} | Options: none
Index found: marketPrice_-1 | Keys: {"marketPrice":-1} | Options: none
Index found: createdAt_-1 | Keys: {"createdAt":-1} | Options: none
--- Completed verification for collection: pokemon_cards ---
```

### API Endpoint for Index Inspection

Get real-time index information via REST API:

```bash
# View all indexes for both collections
curl http://localhost:8080/api/admin/indexes

# View indexes for specific collection
curl http://localhost:8080/api/admin/indexes/pokemon_cards
curl http://localhost:8080/api/admin/indexes/card_owners
```

**Sample Response**:
```json
{
  "pokemon_cards": {
    "collection": "pokemon_cards",
    "totalIndexes": 5,
    "indexes": [
      {
        "name": "_id_",
        "key": {"_id": 1}
      },
      {
        "name": "name_1_set_1",
        "key": {"name": 1, "set": 1},
        "unique": true
      },
      {
        "name": "type_1_rarity_1",
        "key": {"type": 1, "rarity": 1}
      }
    ]
  },
  "message": "Indexes retrieved successfully - proves Liquibase is working!"
}
```

### What Makes This Verification Meaningful

1. **No Default Index Confusion**: We specifically avoid creating indexes on `_id` since MongoDB creates those automatically
2. **Real Business Indexes**: The indexes created solve actual performance problems:
   - `name + set` (unique) - Prevents duplicate cards in same set
   - `type + rarity` - Efficient filtering for game mechanics
   - `marketPrice` (desc) - Sort by value (highest first)
   - `email` (unique) - Ensure unique user accounts
3. **Compound Indexes**: Demonstrates advanced MongoDB indexing capabilities
4. **Unique Constraints**: Shows Liquibase can enforce business rules

This proves that Liquibase is genuinely managing your MongoDB schema, not just creating collections that MongoDB would create anyway!

## Liquibase Integration

This project demonstrates MongoDB schema management using Liquibase with the MongoDB extension. The changelog file (`db.changelog-master.json`) contains:

1. Collection creation for `pokemon_cards` and `card_owners`
2. Index creation for optimized queries
3. Unique constraints on primary keys and email fields

### Adding New Changes

To add new schema changes:

1. Create a new changeset in `src/main/resources/db/changelog/db.changelog-master.json`
2. Use MongoDB-specific Liquibase commands with the `ext:` prefix
3. Restart the application to apply changes

Example changeset:
```json
{
  "changeSet": {
    "id": "12",
    "author": "developer",
    "changes": [
      {
        "ext:createIndex": {
          "collectionName": "pokemon_cards",
          "keys": {
            "marketPrice": 1
          }
        }
      }
    ]
  }
}
```

## Configuration

### Application Configuration (`application.yml`)

```yaml
micronaut:
  application:
    name: pokemon-card-service
  server:
    port: 8080

mongodb:
  uri: mongodb://localhost:27017/pokemon_db

liquibase:
  datasources:
    default:
      change-log: classpath:db/changelog/db.changelog-master.json
```

### Environment Variables

For Docker deployment, you can override configuration using environment variables:

- `MONGODB_URI`: MongoDB connection string
- `MICRONAUT_SERVER_PORT`: Application port
- `LIQUIBASE_CONTEXTS`: Liquibase contexts to run

## Testing Strategy

### Test Coverage

The project includes comprehensive testing at multiple levels:

1. **Unit Tests**: Service layer logic testing
2. **Integration Tests**: Repository operations with test database
3. **Controller Tests**: HTTP endpoint testing with full application context
4. **Testcontainers**: Isolated MongoDB instances for testing

### Test Execution

Tests use Testcontainers to spin up real MongoDB instances, ensuring:
- Tests run against actual database
- No test pollution between runs
- Consistent test environment across different machines

### Running Specific Tests

```bash
# Run all tests
./gradlew test

# Run controller tests only
./gradlew test --tests "*ControllerTest"

# Run service tests only
./gradlew test --tests "*ServiceTest"

# Run with detailed output
./gradlew test --info
```

## Development

### Building

```bash
# Clean and build
./gradlew clean build

# Build without tests
./gradlew build -x test

# Create fat JAR
./gradlew shadowJar
```

### Code Quality

The project follows standard Java conventions and includes:
- Comprehensive JavaDoc comments
- Proper exception handling
- Reactive programming patterns
- Clean architecture separation

### Adding New Features

1. **Add new model**: Create in `model` package with proper annotations
2. **Create repository**: Extend reactive MongoDB operations
3. **Implement service**: Add business logic and validation
4. **Add controller**: Create REST endpoints
5. **Write tests**: Add comprehensive test coverage
6. **Update schema**: Add Liquibase changeset if needed

## Troubleshooting

### Common Issues

1. **MongoDB Connection Issues**:
   - Ensure MongoDB is running on port 27017
   - Check connection string in `application.yml`
   - Verify network connectivity in Docker environment

2. **Liquibase Errors**:
   - Check changelog syntax in JSON file
   - Verify MongoDB Liquibase extension is properly loaded
   - Review database permissions

3. **Test Failures**:
   - Ensure Docker is running for Testcontainers
   - Check for port conflicts
   - Verify test isolation

### Debugging

Enable debug logging by adding to `application.yml`:
```yaml
logger:
  levels:
    com.example: DEBUG
    liquibase: DEBUG
    org.mongodb.driver: DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add comprehensive tests
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
