# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom first to cache dependency layer
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -q

# ---- Runtime stage ----
# eclipse-temurin:17-jre-alpine has no arm64 variant; use the Debian-slim JRE instead
FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

# Create a non-root user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

COPY --from=build /app/target/inventory-backend-1.0.0.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
