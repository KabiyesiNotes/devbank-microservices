# ─────────────────────────────────────────────────────────────────────────────
# Stage 1 – Build
#   Uses the full Maven + JDK image only for compilation.
#   The compiled fat-jar is copied out; this layer never ships to ECR.
# ─────────────────────────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy dependency manifest first so Maven can cache the layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 – Runtime
#   eclipse-temurin:17-jre supports both linux/amd64 and linux/arm64,
#   making it suitable for local development on Apple Silicon / ARM instances
#   as well as AWS ECS Fargate (AMD64 or Graviton).
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre

# Non-root user for security best practice
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

COPY --from=build /app/target/inventory-backend-1.0.0.jar app.jar

# Ensure the non-root user owns the jar
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

# Use exec form to properly handle signals (important for ECS task shutdown)
ENTRYPOINT ["java", "-jar", "app.jar"]
