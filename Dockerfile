FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src src

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew clean build -x test

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-Dmicronaut.environments=docker", "-jar", "build/libs/mongo-liquibase-example-0.1-all.jar"]
