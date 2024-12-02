if 'data_loader' not in globals():
    from mage_ai.data_preparation.decorators import data_loader
if 'test' not in globals():
    from mage_ai.data_preparation.decorators import test
if 'kagglehub' not in globals():
    import kagglehub
if 'set_global_variable' not in globals():
    from mage_ai.data_preparation.variable_manager import set_global_variable

@data_loader
def load_data(*args, **kwargs):
    """
    Template code for loading data from any source.

    Returns:
        Anything (e.g. data frame, dictionary, array, int, str, etc.)
    """
    pipeline_uuid = kwargs.get('pipeline_uuid')
    set_global_variable(pipeline_uuid, "file_path", kagglehub.dataset_download("ealtman2019/credit-card-transactions"))
    return {}

@test
def test_output(output, *args) -> None:
    """
    Template code for testing the output of the block.
    """
    assert output is not None, 'The output is undefined'