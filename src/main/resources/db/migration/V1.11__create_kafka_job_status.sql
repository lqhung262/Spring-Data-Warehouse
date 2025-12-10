CREATE TABLE kafka_job_status
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id            VARCHAR(255) NOT NULL UNIQUE,
    entity_type       VARCHAR(100) NOT NULL,
    operation_type    VARCHAR(50)  NOT NULL,
    total_records     INT          NOT NULL,
    processed_records INT       DEFAULT 0,
    success_count     INT       DEFAULT 0,
    failure_count     INT       DEFAULT 0,
    status            VARCHAR(50)  NOT NULL,
    result_json       TEXT,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at      TIMESTAMP    NULL,
    INDEX idx_job_id (job_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;