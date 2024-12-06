CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    current_age INT NOT NULL,
    retirement_age INT NOT NULL,
    birth_year INT NOT NULL,
    birth_month INT NOT NULL,
    gender VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    per_capita_income FLOAT NOT NULL,
    yearly_income FLOAT NOT NULL,
    total_debt FLOAT NOT NULL,
    credit_score INT NOT NULL,
    credit_card_count INT NOT NULL
);

CREATE TABLE IF NOT EXISTS cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_number VARCHAR(255) NOT NULL,
    card_brand VARCHAR(255) NOT NULL,
    card_type VARCHAR(255) NOT NULL,
    card_expiration_date DATE NOT NULL,
    cvv_code VARCHAR(255) NOT NULL,
    has_chip BOOLEAN NOT NULL,
    number_cards_issued INT NOT NULL,
    credit_limit FLOAT NOT NULL,
    customer_id BIGINT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    transaction_datetime TIMESTAMP NOT NULL,
    card_id BIGINT NOT NULL,
    transaction_amount FLOAT NOT NULL,
    transaction_type VARCHAR(255) NOT NULL,
    merchant_id BIGINT NOT NULL,
    merchant_city VARCHAR(255) NOT NULL,
    merchant_state VARCHAR(255) NOT NULL,
    merchant_zip VARCHAR(255) NOT NULL,
    merchant_mcc_code VARCHAR(255) NOT NULL,
    currency_code VARCHAR(255) NOT NULL,
    transaction_error VARCHAR(255),
    fraud_detected BOOLEAN NOT NULL,
    status VARCHAR(255) NOT NULL,
    FOREIGN KEY (card_id) REFERENCES cards(id)
);