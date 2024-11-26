FROM mageai/mageai:latest

ENV PROJECT_PATH=/default
ENV USER_CODE_PATH=/default
ENV PROJECT_TYPE=standalone
ENV PROJECT_UUID="default"
ENV CLUSTER_TYPE=""
ENV DBT_DOCS_INSTANCE=""
ENV MANAGE_INSTANCE="" 

COPY ./default /home/src/default
COPY ./run_app.sh /app/run_app.sh

RUN chmod +x /app/run_app.sh
RUN pip install --upgrade pip
RUN pip install kagglehub


# Start Mage AI development server
CMD ["/bin/sh", "-c", "/app/run_app.sh"]