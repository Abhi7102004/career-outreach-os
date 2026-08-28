CREATE TABLE users (
       id            BIGSERIAL      PRIMARY KEY,
       public_id     UUID           NOT NULL UNIQUE DEFAULT uuidv7(),
       email         VARCHAR(255)   NOT NULL UNIQUE,
       password_hash VARCHAR(255)   NOT NULL,
       name          VARCHAR(255)   NOT NULL,
       created_at    TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
       updated_at    TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_public_id ON users(public_id);
CREATE INDEX idx_users_email     ON users(email);