# Build stage
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Build application
COPY src ./src
RUN mvn clean package -DskipTests -q

# ================================
# Run stage
# ================================
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# Copy the built JAR
COPY --from=builder /app/target/*.jar app.jar

# Non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose the application port
EXPOSE 8080

# JVM options optimized for containers
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:InitialRAMPercentage=50.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
