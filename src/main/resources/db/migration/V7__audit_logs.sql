CREATE TABLE audit_logs (
                            id BINARY(16) NOT NULL PRIMARY KEY,
                            action VARCHAR(50) NOT NULL,
                            entity_name VARCHAR(100) NOT NULL,
                            entity_id BINARY(16) NOT NULL,
                            description TEXT NOT NULL,
                            performed_by_id BINARY(16) NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT fk_audit_log_user FOREIGN KEY (performed_by_id) REFERENCES users(id)
);