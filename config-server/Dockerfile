# Dockerfile for Config Server
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent pom first
COPY pom.xml ../pom.xml

# Copy config-server pom and source
COPY config-server/pom.xml ./pom.xml
COPY config-server/src ./src

# Build the application
RUN mvn clean package -DskipTests -f ./pom.xml

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Copy config repository
COPY config-repo /app/config-repo

# Expose port
EXPOSE 8888

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8888/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
