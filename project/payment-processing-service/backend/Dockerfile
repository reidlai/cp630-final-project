# Use a multi-stage build to create a minimal image
# Stage 1: Build the application
FROM openjdk:17-jdk-alpine AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the build output to the container
COPY build/libs/app.jar app.jar

# Stage 2: Create the final image using OpenJDK 21
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/app.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]