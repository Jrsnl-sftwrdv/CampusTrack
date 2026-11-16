-- V5: Two-stage incident workflow (student report -> worker inspection -> admin review -> dispatch by specialty)
--
-- Status values after this migration:
--   REPORTED, INSPECTING, REJECTED, PENDING_REVIEW, DECLINED, APPROVED, ASSIGNED, IN_PROGRESS, RESOLVED

-- ============================================================
-- 1. incidents: a fresh student report only has a photo and a location,
--    so everything the worker fills in at inspection must be nullable.
--    (priority_score is left as is: it is no longer used and keeps its default of 0.)
-- ============================================================
ALTER TABLE incidents
    MODIFY category    VARCHAR(50) NULL,
    MODIFY description TEXT        NULL,
    MODIFY priority    VARCHAR(50) NULL;

ALTER TABLE incidents
    ADD COLUMN photo_path       VARCHAR(255)     NULL,
    ADD COLUMN inspected_by     BIGINT UNSIGNED  NULL,
    ADD COLUMN inspected_at     TIMESTAMP        NULL DEFAULT NULL,
    ADD COLUMN asset_functional BOOLEAN          NULL,
    ADD COLUMN reviewed_by      BIGINT UNSIGNED  NULL,
    ADD COLUMN reviewed_at      TIMESTAMP        NULL DEFAULT NULL,
    ADD COLUMN approved_at      TIMESTAMP        NULL DEFAULT NULL,
    ADD COLUMN assigned_at      TIMESTAMP        NULL DEFAULT NULL,
    ADD COLUMN rejection_reason TEXT             NULL;

ALTER TABLE incidents
    ADD CONSTRAINT fk_incidents_inspected_by
        FOREIGN KEY (inspected_by) REFERENCES users (id)
            ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT fk_incidents_reviewed_by
        FOREIGN KEY (reviewed_by) REFERENCES users (id)
            ON DELETE SET NULL ON UPDATE CASCADE;

-- Speeds up the dispatch queue (APPROVED incidents ordered by priority, then approval time)
-- and the "open jobs per worker" count.
CREATE INDEX idx_incidents_queue ON incidents (status, priority, approved_at);
CREATE INDEX idx_incidents_assignee_status ON incidents (assigned_to, status);

-- ============================================================
-- 2. Map existing incidents onto the new status list.
-- ============================================================
UPDATE incidents SET status = 'PENDING_REVIEW' WHERE status = 'EVALUATED';
UPDATE incidents SET status = 'RESOLVED'       WHERE status IN ('VERIFIED', 'CLOSED');

-- ============================================================
-- 3. Users: roles were renamed USER -> STUDENT; workers get an on-duty flag
--    and the time of their last assignment (used to break ties when distributing work).
-- ============================================================
UPDATE users SET user_role = 'STUDENT' WHERE user_role = 'USER';

ALTER TABLE users
    ADD COLUMN available        BOOLEAN   NOT NULL DEFAULT FALSE,
    ADD COLUMN last_assigned_at TIMESTAMP NULL DEFAULT NULL;

-- ============================================================
-- 4. Worker specialties (a worker can have several).
-- ============================================================
CREATE TABLE worker_specialties (
    user_id   BIGINT UNSIGNED NOT NULL,
    specialty VARCHAR(50)     NOT NULL,
    PRIMARY KEY (user_id, specialty),
    CONSTRAINT fk_worker_specialties_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE ON UPDATE CASCADE
);

-- ============================================================
-- 5. Audit trail: who moved an incident to which status, and when.
-- ============================================================
CREATE TABLE incident_status_history (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    incident_id BIGINT UNSIGNED NOT NULL,
    from_status VARCHAR(100)    NULL,
    to_status   VARCHAR(100)    NOT NULL,
    changed_by  BIGINT UNSIGNED NOT NULL,
    changed_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_status_history_incident (incident_id, changed_at),
    CONSTRAINT fk_status_history_incident
        FOREIGN KEY (incident_id) REFERENCES incidents (id)
            ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_status_history_user
        FOREIGN KEY (changed_by) REFERENCES users (id)
            ON DELETE RESTRICT ON UPDATE CASCADE
);