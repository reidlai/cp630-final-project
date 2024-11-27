if 'transformer' not in globals():
    from mage_ai.data_preparation.decorators import transformer
if 'test' not in globals():
    from mage_ai.data_preparation.decorators import test
if 'pd' not in globals():
    import pandas as pd

@transformer
def transform(customer_df: pd, *args, **kwargs):

    # Split Person into first_name and last_name
    split_person = customer_df['Person'].str.split(' ', n=1, expand=True)

    customer_df['first_name'] = split_person[0]
    customer_df['last_name'] = split_person[1]
  
    
    # Concat Apartment and Address if Apartment is not None 
    customer_df['address'] = customer_df.apply(
        lambda row: f"{row['Apartment']} {row['Address']}, {row['City']}, {row['State']} {row['Zipcode']}" if pd.notna(row['Apartment']) else row['Address'],
        axis=1
    )
        
    # Create fake email based on first_name and last_name
    customer_df["email"] = customer_df["first_name"].str.lower() + "." + customer_df["last_name"].str.lower() + "@example.com"

    # Convert money format into float
    customer_df["per_capita_income"] = customer_df["Per Capita Income - Zipcode"].str.replace("$", "").str.replace(",", "").astype(float)
    customer_df["yearly_income"] = customer_df["Yearly Income - Person"].str.replace("$", "").str.replace(",", "").astype(float)
    customer_df["total_debt"] = customer_df["Total Debt"].str.replace("$", "").str.replace(",", "").astype(float)

    # Drop Person column
    customer_df = customer_df.drop(columns = [
        "Person",
        "Address", 
        "Apartment", 
        "City", 
        "State", 
        "Zipcode", 
        "Per Capita Income - Zipcode", 
        "Yearly Income - Person"
    ])
    
    # Rename columns
    customer_df = customer_df.rename(columns={
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
    
    
    return customer_df


@test
def test_output(output, *args) -> None:
    """
    Template code for testing the output of the block.
    """
    assert output is not None, 'The output is undefined'
