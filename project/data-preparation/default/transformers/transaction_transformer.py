if 'transformer' not in globals():
    from mage_ai.data_preparation.decorators import transformer
if 'test' not in globals():
    from mage_ai.data_preparation.decorators import test
if 'pd' not in globals():
    import pandas as pd
if 'pl' not in globals():
    import polars as pl
    
@transformer
def transform(transaction_pldf: pl.DataFrame, *args, **kwargs) -> pl.DataFrame:
    """
    Template code for a transformer block.

    Add more parameters to this function if this block has multiple parent blocks.
    There should be one parameter for each output variable from each parent block.

    Args:
        data: The output from the upstream parent block
        args: The output from any additional upstream blocks (if applicable)

    Returns:
        Anything (e.g. data frame, dictionary, array, int, str, etc.)
    """
    transaction_pldf = transaction_pldf.with_row_count("id", offset=0)

    # Create transaction_datetime column
    transaction_pldf = transaction_pldf.with_columns([
        (pl.concat_str([
            pl.col("Year").cast(pl.Utf8), 
            pl.lit("-"), 
            pl.col("Month").cast(pl.Utf8).str.zfill(2), 
            pl.lit("-"), 
            pl.col("Day").cast(pl.Utf8).str.zfill(2), 
            pl.lit(" "), 
            pl.col("Time")
        ]).str.strptime(pl.Datetime, "%Y-%m-%d %H:%M", strict=False)).alias("transaction_datetime"),
        
        # Create transaction_amount column
        (pl.col("Amount").str.replace_all(r"[\$,]", "").cast(pl.Float64)).alias("transaction_amount"),
        
        # Convert column "Is Fraud?" from string "Yes/No" to boolean field "transaction_error" with True/False
        (pl.when(pl.col("Is Fraud?") == "Yes").then(True).otherwise(False)).alias("transaction_fraud"),
        
        # Convert column "Errors?" from string "Yes" to boolean True otherse to False if value is "No" or None and rename field to "transaction_error"
        (pl.when(pl.col("Errors?") == "Yes").then(True).otherwise(False)).alias("transaction_error")
        
    ])


    # Drop unnecessary columns
    transaction_pldf = transaction_pldf.drop(columns=[
        "Year",
        "Month",
        "Day",
        "Time",
        "Amount"
    ])
    
    # Rename columns
    transaction_pldf = transaction_pldf.rename({
        "User": "customer_id",
        "Card": "card_id",
        "Use Chip": "transaction_type",
        "Merchant Name": "merchant_id",
        "Merchant City": "merchant_city",
        "Merchant State": "merchant_state",
        "Zip": "merchant_zip",
        "MCC": "mcc_code"
    })
    
    return transaction_pldf


@test
def test_output(output, *args) -> None:
    """
    Template code for testing the output of the block.
    """
    assert output is not None, 'The output is undefined'
