FROM eclipse-temurin:22-jdk AS builder
WORKDIR /app
COPY . .
RUN ./mvnw -DskipTests package

FROM eclipse-temurin:22-jre
WORKDIR /app
COPY --from=builder /app/target/notification-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
