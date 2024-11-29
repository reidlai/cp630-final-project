
SELECT 
    customers.id as customer_id
  , address
  , birth_month
  , birth_year
  , credit_card_count
  , credit_score
  , current_age
  , email
  , first_name
  , gender
  , last_name
  , latitude
  , longitude
  , per_capita_income
  , retirement_age
  , total_debt
  , yearly_income
  , cards.id as card_id
  , card_brand
  , card_expiration_date
  , card_number
  , card_type
  , credit_limit
  , cvv_code
  , has_chip
  , number_cards_issued
  , account_open_date
  , card_index
  , card_on_dark_web
  , pin_last_changed_year
FROM 
    customers
  , cards
WHERE
      customers.id = cards.customer_id