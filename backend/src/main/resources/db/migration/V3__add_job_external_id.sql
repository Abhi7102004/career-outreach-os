ALTER TABLE jobs ADD COLUMN external_job_id VARCHAR(255);
CREATE INDEX idx_jobs_external_job_id ON jobs(external_job_id);