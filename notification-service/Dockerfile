# ======= BUILD STAGE =======
FROM maven:3.9-eclipse-temurin-22 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ======= RUNTIME STAGE =======
FROM eclipse-temurin:22-jre
WORKDIR /app

COPY --from=build /app/target/notification-service-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]
