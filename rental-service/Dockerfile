# Dockerfile for Rental Service
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent pom first
COPY pom.xml ../pom.xml

# Copy rental-service pom and source
COPY rental-service/pom.xml ./pom.xml
COPY rental-service/src ./src

# Build the application
RUN mvn clean package -DskipTests -f ./pom.xml

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8004

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8004/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
