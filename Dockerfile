# Étape 1 : Utiliser une image Java officielle
FROM openjdk:17-jdk-slim

# Étape 2 : Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Étape 3 : Copier le fichier JAR généré dans le conteneur
COPY target/crud-springboot-0.0.1-SNAPSHOT.jar app.jar


# Étape 4 : Exposer le port utilisé par Spring Boot
EXPOSE 8080

# Étape 5 : Lancer l'application Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
