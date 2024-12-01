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

import datetime

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
    df = pl.read_csv(file_path + "/" + file_name)
    
    # Step 1: Create name columns
    df = df.with_columns([
        pl.col("Person").str.split(' ').list.get(0).alias("first_name"),
        pl.col("Person").str.split(' ').list.get(-1).alias("last_name")
    ])

    # Step 2: Create address and other derived columns
    df = df.with_columns([
        pl.when(pl.col("Apartment").is_not_null())
        .then(
            pl.concat_str([
                pl.lit("Unit "), pl.col("Apartment"), pl.lit(", "),
                pl.col("Address"), pl.lit(", "),
                pl.col("City"), pl.lit(", "),
                pl.col("State"), pl.lit(" "),
                pl.col("Zipcode")
            ])
        )
        .otherwise(
            pl.concat_str([
                pl.col("Address"), pl.lit(", "),
                pl.col("City"), pl.lit(", "),
                pl.col("State"), pl.lit(" "),
                pl.col("Zipcode")
            ])
        ).alias("address"),
        
        pl.concat_str([
            pl.col("first_name").str.to_lowercase(),
            pl.lit("."),
            pl.col("last_name").str.to_lowercase(),
            pl.lit("@example.com")
        ]).alias("email"),
        
        pl.col("Per Capita Income - Zipcode").str.replace_all(r"[\$,]", "").cast(pl.Float64).alias("per_capita_income"),
        pl.col("Yearly Income - Person").str.replace_all(r"[\$,]", "").cast(pl.Float64).alias("yearly_income"),
        pl.col("Total Debt").str.replace_all(r"[\$,]", "").cast(pl.Float64).alias("total_debt")
    ])

    # Step 3: Drop and rename columns
    df = df.drop([
        "Person", "Address", "Apartment", "City", "State", "Zipcode",
        "Per Capita Income - Zipcode", "Yearly Income - Person", "Total Debt"
    ])

    df = df.rename({
        "Current Age": "current_age",
        "Retirement Age": "retirement_age",
        "Birth Year": "birth_year",
        "Birth Month": "birth_month",
        "Gender": "gender",
        "Latitude": "latitude",
        "Longitude": "longitude",
        "FICO Score": "credit_score",
        "Num Credit Cards": "credit_card_count"
    })

    # Add row numbers
    df = df.with_row_count("id", offset=0)

    conn_url = f"postgresql://{postgres_user}:{postgres_password}@{postgres_host}:{postgres_port}/{postgres_dbname}"
    df.write_database(table_name = f"{postgres_schema}.{table_name}", connection = conn_url, if_exists = "append")
    
    minio_config_profile = 'minio'
    minio_config_loader = ConfigFileLoader(config_path, minio_config_profile)
    minio_endpoint = minio_config_loader.get('MINIO_ENDPOINT')
    minio_access_key = minio_config_loader.get('MINIO_ACCESS_KEY')
    minio_secret_key = minio_config_loader.get('MINIO_SECRET_KEY')
    minio_bucket = minio_config_loader.get('MINIO_BUCKET')
    print(f"minio_bucket: {minio_bucket}")

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
