# 1. Phase de Build (Utilise Maven pour créer le .jar)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# On saute les tests pour accélérer le build et éviter les erreurs de connexion DB pendant le build
RUN mvn clean package -DskipTests

# 2. Phase d'Exécution (Image légère)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]