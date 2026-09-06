CREATE TABLE connections (
         id            BIGSERIAL PRIMARY KEY,
         first_name    VARCHAR(255),
         last_name     VARCHAR(255),
         profile_url   TEXT UNIQUE,
         email         VARCHAR(255),
         company_id    BIGINT REFERENCES companies(id) ON DELETE SET NULL,
         company_raw   VARCHAR(255),
         position      VARCHAR(500),
         connected_on  DATE,
         created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_connections_company_id ON connections(company_id);
CREATE INDEX idx_connections_last_name  ON connections(last_name);