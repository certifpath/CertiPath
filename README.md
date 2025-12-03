# Notification Service

Microservice responsible for sending and managing user notifications.

## Tech Stack
- Java 22 / Spring Boot 3
- PostgreSQL (Docker)
- JPA / Hibernate
- Spring Actuator
- Profiles: `dev`, `test`, `prod`

## Run in Development
### 1. Start PostgreSQL (Docker)
```bash
docker run --name certipath-postgres \
  -e POSTGRES_DB=notificationdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5433:5432 -d postgres:16
