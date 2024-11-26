FROM openjdk:17-jdk-alpine

ARG CACHEBUST=1

# Install PostgreSQL client
RUN apk add --no-cache postgresql-client

# Set the working directory in the container
WORKDIR /app

# Copy the build output to the container
COPY ./build/libs/app.jar app.jar
COPY ./wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh
RUN pwd
RUN ls

# Expose the port the app runs on
EXPOSE 8080

CMD ["/app/wait-for-it.sh", "db", "java", "-jar", "app.jar"]
