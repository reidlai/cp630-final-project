FROM mageai/mageai:0.9.74

ARG CACHEBUST=1

ENV PROJECT_PATH=/home/src/default
ENV USER_CODE_PATH=/home/src/default
ENV PROJECT_TYPE=standalone
ENV PROJECT_UUID="default"
ENV CLUSTER_TYPE=""
ENV DBT_DOCS_INSTANCE=""
ENV MANAGE_INSTANCE="" 

WORKDIR /app
COPY run_app.sh /app/run_app.sh
RUN chmod +x run_app.sh
RUN pwd
RUN ls -l

RUN pip install --upgrade pip
RUN pip install kagglehub
# RUN pip install polars --upgrade

# Start Mage AI development server
CMD ["/app/run_app.sh"]