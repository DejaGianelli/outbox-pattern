CREATE TABLE tickets (
    id SERIAL NOT NULL PRIMARY KEY,
    external_id UUID NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    dish_name VARCHAR(255) NOT NULL,
    done_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);