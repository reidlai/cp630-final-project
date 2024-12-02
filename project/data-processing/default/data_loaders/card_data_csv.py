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

@data_loader
def load_data_from_file(*args, **kwargs) -> pl.DataFrame:
    """
    Template for loading data from filesystem.
    Load data from 1 file or multiple file directories.

    For multiple directories, use the following:
        FileIO().load(file_directories=['dir_1', 'dir_2'])

    Docs: https://docs.mage.ai/design/data-loading#fileio
    """

    file_path = kwargs.get("file_path")
    file_name = kwargs['configuration'].get('file_name')
    table_name = kwargs['configuration'].get('table_name')

    config_path = path.join(get_repo_path(), 'io_config.yaml')
    db_config_profile = 'db'
    db_config_loader = ConfigFileLoader(config_path, db_config_profile)

    postgres_host = db_config_loader.get('POSTGRES_HOST')
    postgres_port = db_config_loader.get('POSTGRES_PORT')
    postgres_dbname = db_config_loader.get('POSTGRES_DBNAME')
    postgres_user = db_config_loader.get('POSTGRES_USER')
    postgres_password = db_config_loader.get('POSTGRES_PASSWORD')
    postgres_schema = db_config_loader.get('POSTGRES_SCHEMA')

    # Read the CSV file
    df = pl.read_csv(file_path + "/" + file_name).with_columns([
        (pl.concat_str([
            pl.col("Expires").str.split("/").list.get(1),
            pl.lit("-"),
            pl.col("Expires").str.split("/").list.get(0),
            pl.lit("-01")
        ]).alias("card_expiration_date")),
        (pl.concat_str([
            pl.col("Acct Open Date").str.split("/").list.get(1),
            pl.lit("-"),
            pl.col("Acct Open Date").str.split("/").list.get(0),
            pl.lit("-01"),
        ])).alias("account_open_date"),
        (pl.when(pl.col("Has Chip") == "YES").then(True).otherwise(False)).alias("has_chip"),
        (pl.when(pl.col("Card on Dark Web") == "Yes").then(True).otherwise(False)).alias("card_on_dark_web"),
        # (pl.col("Credit Limit").str.replace("$", "").str.replace(",", "").cast(pl.Float64)).alias("credit_limit")
        (pl.col("Credit Limit").str.replace_all(r"[\$,]", "").cast(pl.Float64)).alias("credit_limit")
    ]).drop([
        "Expires",
        "Has Chip",
        "Card on Dark Web",
        "Acct Open Date",
        "Credit Limit"
    ]).rename({
        "User": "customer_id",
        "Card Brand": "card_brand",
        "Card Type": "card_type",
        "Card Number": "card_number",
        "CARD INDEX": "card_index",
        "CVV": "cvv_code",
        "Cards Issued": "number_cards_issued",
        "Year PIN last Changed": "pin_last_changed_year",
    }).with_row_count("id", offset=0)
        
    conn_url = f"postgresql://{postgres_user}:{postgres_password}@{postgres_host}:{postgres_port}/{postgres_dbname}"
    df.write_database(table_name = f"{postgres_schema}.{table_name}", connection = conn_url, if_exists = "append")

    minio_config_profile = 'minio'
    minio_config_loader = ConfigFileLoader(config_path, minio_config_profile)
    minio_endpoint = minio_config_loader.get('MINIO_ENDPOINT')
    minio_access_key = minio_config_loader.get('MINIO_ACCESS_KEY')
    minio_secret_key = minio_config_loader.get('MINIO_SECRET_KEY')
    minio_bucket = minio_config_loader.get('MINIO_BUCKET')

    # write each df into parquet file to minio with suffix of timestamp
    client = Minio(
        endpoint=minio_endpoint,
        access_key=minio_access_key,
        secret_key=minio_secret_key,
        secure=False
    )
        
    # read df to parquet file
    parquet_file = io.BytesIO()
    df.write_parquet(parquet_file)
    parquet_file.seek(0)
    # generate timestamp
    client.put_object(
        bucket_name=minio_bucket,
        object_name=f"{table_name}.parquet",
        data=parquet_file,
        length=parquet_file.getbuffer().nbytes,
        content_type='application/octet-stream'
    )
    
    return df

@test
def test_output(output, *args) -> None:
    """
    Template code for testing the output of the block.
    """
    assert output is not None, 'The output is undefined'