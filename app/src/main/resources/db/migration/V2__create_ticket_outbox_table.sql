CREATE TABLE outbox (
    id SERIAL NOT NULL PRIMARY KEY,
    channel VARCHAR(32) NOT NULL,
    payload JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);