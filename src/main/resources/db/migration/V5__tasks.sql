CREATE TABLE tasks (
                       id BINARY(16) NOT NULL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       due_date DATE NOT NULL,
                       status VARCHAR(50) NOT NULL,
                       priority VARCHAR(50) NOT NULL,
                       assigned_to_id BINARY(16) NOT NULL,
                       related_entity_id BINARY(16),
                       related_entity_type VARCHAR(100),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       CONSTRAINT fk_task_user FOREIGN KEY (assigned_to_id) REFERENCES users(id)
);