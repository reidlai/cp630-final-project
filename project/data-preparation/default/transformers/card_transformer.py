if 'transformer' not in globals():
    from mage_ai.data_preparation.decorators import transformer
if 'test' not in globals():
    from mage_ai.data_preparation.decorators import test
if 'pd' not in globals():
    import pandas as pd

@transformer
def transform(customer_dbtable_df: pd.DataFrame, card_df: pd.DataFrame, *args, **kwargs):
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
    
    card_df["id"] = card_df.index
    
    # Convert expiries from mon/year to the first date of the year month in the format yyyy-mm-dd
    card_df["card_expiration_date"] = card_df["Expires"].apply(lambda x: f"{x.split('/')[1]}-{x.split('/')[0]}-01")
    card_df["account_open_date"] = card_df["Acct Open Date"].apply(lambda x: f"{x.split('/')[1]}-{x.split('/')[0]}-01")
    
    # Convert YES/NO string to boolean
    card_df["has_chip"] = card_df["Has Chip"].apply(lambda x: True if x == "YES" else False)
    card_df["card_on_dark_web"] = card_df["Card on Dark Web"].apply(lambda x: True if x == "Yes" else False)
    
    card_df["credit_limit"] = card_df["Credit Limit"].str.replace("$", "").str.replace(",", "").astype(float)

    card_df = card_df.drop(columns=[
        "Expires",
        "Has Chip",
        "Card on Dark Web",
        "Acct Open Date",
        "Credit Limit"
    ])

    card_df = card_df.rename(columns={
        "User": "customer_id",
        "Card Brand": "card_brand",
        "Card Type": "card_type",
        "Card Number": "card_number",
        "CARD INDEX": "card_index",
        "CVV": "cvv_code",
        "Cards Issued": "number_cards_issued",
        "Year PIN last Changed": "pin_last_changed_year",
    })


    return card_df


@test
def test_output(output, *args) -> None:
    """
    Template code for testing the output of the block.
    """
    assert output is not None, 'The output is undefined'
