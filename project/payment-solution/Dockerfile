# FROM openjdk:17-jdk-alpine

# ARG CACHEBUST=1

# # Install PostgreSQL client
# RUN apk add --no-cache postgresql-client libstdc++ libc6-compat gcompat bash
# # Install dos2unix to convert line endings to Unix format in the wait-for-it.sh script
# RUN apk add dos2unix --update-cache --repository http://dl-3.alpinelinux.org/alpine/edge/community/ --allow-untrusted

# # Set the working directory in the container
# WORKDIR /app

# ENV POSTGRES_HOST=localhost
# ENV POSTGRES_PORT=5432
# ENV MODEL_PATH=/app/model/lightgbm_fraud_detector.onnx
# ENV ENCODING_MAPPING_DIR=/app/model
# ENV DB_LIVENESS_ENABLED=true

# # Copy the build output to the container
# COPY ./build/libs/app.jar app.jar
# COPY ./wait-for-it.sh wait-for-it.sh
# COPY ./model model
# RUN dos2unix -b wait-for-it.sh; chmod +x wait-for-it.sh


# # Expose the port the app runs on
# EXPOSE 8080

# CMD ["/app/wait-for-it.sh", "java", "-jar", "app.jar"]


# Use Eclipse Temurin (formerly AdoptOpenJDK) with Alpine

FROM eclipse-temurin:17-jdk

ARG CACHEBUST=1
ENV ONNXRUNTIME_VERSION=1.16.3

# Install required dependencies
RUN apt-get update && apt-get install -y \
    wget \
    postgresql-client \
    && rm -rf /var/lib/apt/lists/*

# Download and install ONNX Runtime
RUN mkdir -p /usr/local/lib \
    && cd /usr/local/lib \
    && wget -q https://github.com/microsoft/onnxruntime/releases/download/v${ONNXRUNTIME_VERSION}/onnxruntime-linux-x64-${ONNXRUNTIME_VERSION}.tgz \
    && tar -xzf onnxruntime-linux-x64-${ONNXRUNTIME_VERSION}.tgz \
    && cp onnxruntime-linux-x64-${ONNXRUNTIME_VERSION}/lib/* . \
    && rm -rf onnxruntime-linux-x64-${ONNXRUNTIME_VERSION}* \
    && ln -sf libonnxruntime.so.${ONNXRUNTIME_VERSION} libonnxruntime.so

# Set the working directory in the container
WORKDIR /app

# Set environment variables
ENV POSTGRES_HOST=localhost
ENV POSTGRES_PORT=5432
ENV MODEL_PATH=/app/model/lightgbm_fraud_detector.onnx
ENV ENCODING_MAPPING_DIR=/app/model
ENV DB_LIVENESS_ENABLED=true
ENV LD_LIBRARY_PATH=/usr/local/lib

# Copy the build output to the container
COPY ./build/libs/app.jar app.jar
COPY ./wait-for-it.sh wait-for-it.sh
COPY ./model model

# Make wait-for-it.sh executable
RUN chmod +x wait-for-it.sh

# Expose the port the app runs on
EXPOSE 8080

CMD ["./wait-for-it.sh", "java", "-jar", "app.jar"]