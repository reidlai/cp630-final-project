FROM mageai/mageai:latest

ARG CACHEBUST=1

ENV PROJECT_PATH=/default
ENV USER_CODE_PATH=/default
ENV PROJECT_TYPE=standalone
ENV PROJECT_UUID="default"
ENV CLUSTER_TYPE=""
ENV DBT_DOCS_INSTANCE=""
ENV MANAGE_INSTANCE="" 

WORKDIR /app
COPY run_app.sh run_app.sh
RUN chmod +x run_app.sh
RUN pwd
RUN ls -l

RUN pip install --upgrade pip
RUN pip install kagglehub

# Start Mage AI development server
CMD ["/app/run_app.sh"]