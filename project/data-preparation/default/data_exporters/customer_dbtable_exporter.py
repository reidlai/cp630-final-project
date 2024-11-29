from mage_ai.settings.repo import get_repo_path
from mage_ai.io.config import ConfigFileLoader, ConfigKey
from mage_ai.io.postgres import Postgres
from os import path

if 'data_exporter' not in globals():
    from mage_ai.data_preparation.decorators import data_exporter
if 'pd' not in globals():    
    import pandas as pd

@data_exporter
def export_data_to_postgres(df: pd.DataFrame, **kwargs) -> None:
    """
    Template for exporting data to a PostgreSQL database.
    Specify your configuration settings in 'io_config.yaml'.

    Docs: https://docs.mage.ai/design/data-loading#postgresql
    """
    schema_name = 'public'  # Specify the name of the schema to export data to
    table_name = 'customers'  # Specify the name of the table to export data to
    config_path = path.join(get_repo_path(), 'io_config.yaml')
    config_profile = 'db'
    config_loader = ConfigFileLoader(config_path, config_profile)

    # Use get() method instead of load()
    # host = config_loader.get('POSTGRES_HOST')
    # port = config_loader.get('POSTGRES_PORT')
    # database = config_loader.get('POSTGRES_DBNAME')
    # username = config_loader.get('POSTGRES_USER')
    # password = config_loader.get('POSTGRES_PASSWORD')

    with Postgres.with_config(config_loader) as loader:
        return loader.export(
            df,
            schema_name,
            table_name,
            index=False,  # Specifies whether to include index in exported table
            if_exists='replace',  # Specify resolution policy if table name already exists
            drop_table_on_replace=False,
            unique_conflict_method='UPDATE',
            unique_constraints=['id'],
        )