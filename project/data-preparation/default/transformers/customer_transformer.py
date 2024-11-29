if 'transformer' not in globals():
    from mage_ai.data_preparation.decorators import transformer
if 'test' not in globals():
    from mage_ai.data_preparation.decorators import test
if 'pd' not in globals():
    import pandas as pd

@transformer
def transform(customer_df: pd.DataFrame, *args, **kwargs):

    # Split Person into first_name and last_name
    split_person = customer_df['Person'].str.split(' ', n=1, expand=True)
    customer_df['id'] = customer_df.index
    customer_df['first_name'] = split_person[0]
    customer_df['last_name'] = split_person[1]
  
    
    # Concat Apartment and Address if Apartment is not None 
    customer_df['address'] = customer_df.apply(
        lambda row: f"Unit {row['Apartment']}, {row['Address']}, {row['City']}, {row['State']} {row['Zipcode']}" if pd.notna(row['Apartment']) else f"{row['Address']}, {row['City']}, {row['State']} {row['Zipcode']}",
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
        "Yearly Income - Person",
        "Total Debt"
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

    # 1. Convert any float64 columns that should be numeric
    numeric_columns = ['latitude', 'longitude']
    for col in numeric_columns:
        customer_df[col] = customer_df[col].astype(float)

    # 2. Ensure string columns are actually strings
    string_columns = ['gender', 'first_name', 'last_name', 'address', 'email']
    for col in string_columns:
        customer_df[col] = customer_df[col].astype(str)

    # 3. Ensure integer columns are actually integers
    int_columns = ['current_age', 'retirement_age', 'birth_year', 'birth_month', 'credit_score', 'credit_card_count']
    for col in int_columns:
        customer_df[col] = pd.to_numeric(customer_df[col], errors='coerce').astype('Int64')

    # 4. Reset the index to ensure it's clean
    customer_df = customer_df.reset_index(drop=True)

    return customer_df


@test
def test_output(output, *args) -> None:
    """
    Template code for testing the output of the block.
    """
    assert output is not None, 'The output is undefined'
