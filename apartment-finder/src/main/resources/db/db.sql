CREATE TABLE IF NOT EXISTS apartments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2),
    address VARCHAR(255),
    district VARCHAR(100),
    rooms INT,
    area DECIMAL(10, 2),
    floor INT,
    total_floors INT,
    description CLOB,
    contact_phone VARCHAR(20),
    olx_url VARCHAR(255),
    posted_date TIMESTAMP,
    is_realtor BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS currency_rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    currency_code VARCHAR(3) NOT NULL,
    rate DECIMAL(10, 4) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);