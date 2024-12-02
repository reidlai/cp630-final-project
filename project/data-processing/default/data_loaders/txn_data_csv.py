if 'get_repo_path' not in globals():
    from mage_ai.settings.repo import get_repo_path
if 'ConfigFileLoader' not in globals():
    from mage_ai.io.config import ConfigFileLoader
if 'data_loader' not in globals():
    from mage_ai.data_preparation.decorators import data_loader
if 'test' not in globals():
    from mage_ai.data_preparation.decorators import test
if 'pl' not in globals():
    import polars as pl
if 'minio' not in globals():
    from minio import Minio
if 'path' not in globals():
    from os import path
if 'get_repo_path' not in globals():
    from mage_ai.settings.repo import get_repo_path
if 'ConfigFileLoader' not in globals():
    from mage_ai.io.config import ConfigFileLoader
if 'io' not in globals():
    import io
from mage_ai.io.postgres import Postgres
import datetime

@data_loader
def load_data_from_file(*args, **kwargs) -> pl.DataFrame:

    # Set file path and table name parameter
    file_path = kwargs.get('file_path')
    file_name = kwargs['configuration'].get('file_name')
    table_name = kwargs['configuration'].get('table_name')

    # Create db config loader
    config_path = path.join(get_repo_path(), 'io_config.yaml')
    db_config_profile = 'db'
    db_config_loader = ConfigFileLoader(config_path, db_config_profile)

    # Fetch Postgres parameters from io_config.yaml
    postgres_host = db_config_loader.get('POSTGRES_HOST')
    postgres_port = db_config_loader.get('POSTGRES_PORT')
    postgres_dbname = db_config_loader.get('POSTGRES_DBNAME')
    postgres_user = db_config_loader.get('POSTGRES_USER')
    postgres_password = db_config_loader.get('POSTGRES_PASSWORD')
    postgres_schema = db_config_loader.get('POSTGRES_SCHEMA')
    
    # Create minio config loader
    minio_config_profile = 'minio'
    minio_config_loader = ConfigFileLoader(config_path, minio_config_profile)

    # Fetch minio parameters from io_config.yaml
    minio_endpoint = minio_config_loader.get('MINIO_ENDPOINT')
    minio_access_key = minio_config_loader.get('MINIO_ACCESS_KEY')
    minio_secret_key = minio_config_loader.get('MINIO_SECRET_KEY')
    minio_bucket = minio_config_loader.get('MINIO_BUCKET')

    # Create batch reader and batches from Kaggle dataset
    reader = pl.read_csv_batched(file_path + "/" + file_name)
    batches = reader.next_batches(10000)

    # Create mion client
    client = Minio(
        endpoint=minio_endpoint,
        access_key=minio_access_key,
        secret_key=minio_secret_key,
        secure=False
    )
    
    # Loop batches
    for df in batches:
        df = df.with_columns([
                (pl.concat_str([
                        pl.col("Year").cast(pl.Utf8), 
                        pl.lit("-"), 
                        pl.col("Month").cast(pl.Utf8).str.zfill(2), 
                        pl.lit("-"), 
                        pl.col("Day").cast(pl.Utf8).str.zfill(2), 
                        pl.lit(" "), 
                        pl.col("Time")
                    ]).str.strptime(pl.Datetime, "%Y-%m-%d %H:%M", strict=False)
                ).alias("transaction_datetime"),
                (pl.col("Amount").str.replace_all(r"[\$,]", "").cast(pl.Float64)).alias("transaction_amount"),
                (pl.when(pl.col("Is Fraud?") == "Yes").then(True).otherwise(False)).alias("fraud_detected"),
                (pl.col("Zip").cast(pl.Int64).cast(pl.Utf8).alias("merchant_zip"))
            ]).drop(columns=[
                "Year",
                "Month",
                "Day",
                "Time",
                "Amount",
                "Is Fraud?",
                "Zip"
            ]).rename({
                "User": "customer_id",
                "Card": "card_index",
                "Use Chip": "transaction_type",
                "Merchant Name": "merchant_id",
                "Merchant City": "merchant_city",
                "Merchant State": "merchant_state",
                "MCC": "merchant_mcc_code",
                "Errors?": "transaction_error"
            })

        # Export dataframe to Postgres
        with Postgres.with_config(db_config_loader) as loader:
            loader.export(
                df.to_pandas(),
                postgres_schema,
                table_name,
                index=False,  # Specifies whether to include index in exported table
                if_exists='append'  # Specify resolution policy if table name already exists
            )

        # read df to parquet file
        parquet_file = io.BytesIO()
        df.write_parquet(parquet_file)
        parquet_file.seek(0)

        # generate timestamp
        timestampStr = datetime.datetime.now().strftime("%Y%m%d%H%M%S%f")[:-3]

        # Save parquet files to minio
        client.put_object(
            bucket_name=minio_bucket,
            object_name=f"{table_name}_{timestampStr}.parquet",
            data=parquet_file,
            length=parquet_file.getbuffer().nbytes,
            content_type='application/octet-stream'
        )
    return {}

@test
def test_output(output, *args) -> None:
    """
    Template code for testing the output of the block.
    """
    assert output is not None, 'The output is undefined'