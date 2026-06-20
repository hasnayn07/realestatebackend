CREATE TABLE documents (
                           id BINARY(16) NOT NULL PRIMARY KEY,
                           file_name VARCHAR(255) NOT NULL,
                           original_name VARCHAR(255) NOT NULL,
                           file_type VARCHAR(100) NOT NULL,
                           file_size BIGINT NOT NULL,
                           document_category VARCHAR(100) NOT NULL,
                           related_entity_id BINARY(16) NOT NULL,
                           related_entity_type VARCHAR(100) NOT NULL,
                           uploaded_by_id BINARY(16) NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT fk_document_uploader FOREIGN KEY (uploaded_by_id) REFERENCES users(id)
);