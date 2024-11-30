FROM openjdk:17-jdk-alpine

ARG CACHEBUST=1

# Install PostgreSQL client
RUN apk add --no-cache postgresql-client
# Install dos2unix to convert line endings to Unix format in the wait-for-it.sh script
RUN apk add dos2unix --update-cache --repository http://dl-3.alpinelinux.org/alpine/edge/community/ --allow-untrusted

# Set the working directory in the container
WORKDIR /app

# Copy the build output to the container
COPY ./build/libs/app.jar app.jar
COPY ./wait-for-it.sh wait-for-it.sh
RUN dos2unix -b wait-for-it.sh; chmod +x wait-for-it.sh
RUN pwd
RUN ls

ENV POSTGRES_HOST=localhost
ENV POSTGRES_PORT=5432
# Expose the port the app runs on
EXPOSE 8080

CMD ["/app/wait-for-it.sh", "java", "-jar", "app.jar"]
