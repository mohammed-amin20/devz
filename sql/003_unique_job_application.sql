DELETE FROM "JobApplication" a
USING "JobApplication" b
WHERE a.id > b.id
  AND a.job_id = b.job_id
  AND a.applicant_id = b.applicant_id;

CREATE UNIQUE INDEX IF NOT EXISTS idx_job_application_job_applicant
  ON "JobApplication" ("job_id", "applicant_id");
