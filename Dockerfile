# Stage 1: Build
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven files
COPY pom.xml /app/
COPY src/ /app/src/

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Build shaded JAR
RUN mvn clean package

# Stage 2: Runtime (slim)
FROM eclipse-temurin:21-jre

# Install only minimal OpenJFX runtime for GUI
RUN apt-get update && \
    apt-get install -y --no-install-recommends openjfx x11-apps && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy only JAR from build stage
COPY --from=build /app/target/flappybird-1.0-SNAPSHOT.jar /app/flappybird.jar

# Run app
CMD ["java", "--module-path", "/usr/share/openjfx/lib", "--add-modules", "javafx.controls", "-jar", "flappybird.jar"]
