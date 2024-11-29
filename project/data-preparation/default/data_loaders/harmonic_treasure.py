if 'data_loader' not in globals():
    from mage_ai.data_preparation.decorators import data_loader
if 'test' not in globals():
    from mage_ai.data_preparation.decorators import test
# if 'pd' not in globals():
#     import pandas as pd
if 'pl' not in globals():
    import polars as pl

@data_loader
def load_data(path, *args, **kwargs):
    """
    Template code for loading data from any source.

    Returns:
        Anything (e.g. data frame, dictionary, array, int, str, etc.)
    """

    transaction_datafile_path = f"{path}/credit_card_transactions-ibm_v2.csv"

    return pl.read_csv(transaction_datafile_path)

@test
def test_output(output, *args) -> None:
    """
    Template code for testing the output of the block.
    """
    assert output is not None, 'The output is undefined'
