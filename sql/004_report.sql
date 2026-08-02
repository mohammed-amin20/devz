CREATE TABLE IF NOT EXISTS "Report" (
  id            SERIAL PRIMARY KEY,
  reporter_id   INT NOT NULL,
  reported_type TEXT NOT NULL,
  reported_id   INT NOT NULL,
  reason        TEXT NOT NULL,
  details       TEXT DEFAULT '',
  status        TEXT NOT NULL DEFAULT 'pending',
  created_at    TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_report_status ON "Report" (status);
CREATE INDEX IF NOT EXISTS idx_report_target ON "Report" (reported_type, reported_id);
