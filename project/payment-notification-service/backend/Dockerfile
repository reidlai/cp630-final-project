FROM openjdk:17-jdk-alpine

# Install PostgreSQL client
RUN apk add --no-cache postgresql-client

# Set the working directory in the container
WORKDIR /app

# Copy the build output to the container
COPY ./build/libs/app.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]