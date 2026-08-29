CREATE TABLE companies (
   id              BIGSERIAL PRIMARY KEY,
   name            VARCHAR(255) NOT NULL,
   normalized_name VARCHAR(255) NOT NULL UNIQUE,
   created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE jobs (
      id          BIGSERIAL PRIMARY KEY,
      company_id  BIGINT REFERENCES companies(id) ON DELETE SET NULL,
      url         TEXT         NOT NULL UNIQUE,
      title       VARCHAR(500),
      raw_html    TEXT,
      parsed_at   TIMESTAMPTZ,
      created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_jobs_company_id ON jobs(company_id);