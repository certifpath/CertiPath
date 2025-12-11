# Build stage
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Copy pom and source
COPY pom.xml .
COPY src ./src

# Build the jar (skip tests for speed during image build)
RUN mvn -B -DskipTests -Dmaven.repo.local=/root/.m2/repository package

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the executable jar from the builder stage (use wildcard to avoid hard-coded version)
COPY --from=builder /workspace/target/*.jar app.jar

# Expose the configured server port (default: 8082)
EXPOSE 8082

# Use a non-root user (optional; Alpine/OpenJDK distros vary). Fallback to root if useradd not present.
RUN set -eux; \
    if command -v addgroup >/dev/null 2>&1; then \
    addgroup --system appgroup || true; \
    adduser --system --ingroup appgroup appuser || true; \
    chown -R appuser:appgroup /app; \
    fi || true

USER appuser

# Allow passing JVM options via JAVA_OPTS
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
