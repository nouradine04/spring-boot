# Étape 1 : Utiliser une image Java pour builder l'application
FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package

# Étape 2 : Utiliser une image légère pour l'exécution
FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=builder /app/target/crud-springboot-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]