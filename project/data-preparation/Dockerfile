FROM mageai/mageai:0.9.74

ARG CACHEBUST=1

ENV PROJECT_PATH=/home/src/default
ENV USER_CODE_PATH=/home/src/default
ENV PROJECT_TYPE=standalone
ENV PROJECT_UUID="default"
ENV CLUSTER_TYPE=""
ENV DBT_DOCS_INSTANCE=""
ENV MANAGE_INSTANCE="" 
ENV POSTGRES_HOST="localhost"
ENV POSTGRES_PORT="5432"
ENV POSTGRES_USER="postgres"
ENV POSTGRES_PASSWORD="postgres"
ENV POSTGRES_DB="postgres"
ENV POSTGRES_SCHEMA="public"

# Install dos2unix to convert line endings to Unix format in the wait-for-it.sh script
RUN apt-get -y update
RUN apt-get -y upgrade
RUN apt-get install dos2unix

WORKDIR /app
COPY run_app.sh /app/run_app.sh

RUN dos2unix -b run_app.sh
RUN chmod +x run_app.sh
RUN pwd
RUN ls -l

RUN pip install --upgrade pip
RUN pip install kagglehub
RUN pip install minio
RUN pip install connectorx>=0.3.2


# RUN pip install "polars==1.16.0" --upgrade

# Start Mage AI development server
CMD ["/app/run_app.sh"]