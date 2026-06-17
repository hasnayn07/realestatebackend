-- V2__inventory.sql
-- Inventory module: projects -> blocks -> units

CREATE TABLE projects (
                          id          BINARY(16)   PRIMARY KEY,
                          name        VARCHAR(150) NOT NULL,
                          city        VARCHAR(100),
                          description VARCHAR(500),
                          created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE blocks (
                        id         BINARY(16)   PRIMARY KEY,
                        project_id BINARY(16)   NOT NULL,
                        name       VARCHAR(100) NOT NULL,
                        CONSTRAINT fk_block_project
                            FOREIGN KEY (project_id) REFERENCES projects(id)
                                ON DELETE RESTRICT
);
CREATE INDEX idx_blocks_project ON blocks(project_id);

CREATE TABLE units (
                       id           BINARY(16)   PRIMARY KEY,
                       block_id     BINARY(16)   NOT NULL,
                       unit_number  VARCHAR(40)  NOT NULL,
                       unit_type    VARCHAR(20)  NOT NULL,
                       size_value   DOUBLE,
                       size_unit    VARCHAR(10),
                       listed_price DECIMAL(14,2),
                       bedrooms     INT,
                       bathrooms    INT,
                       floor        INT,
                       status       VARCHAR(20)  NOT NULL,
                       created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       CONSTRAINT fk_unit_block
                           FOREIGN KEY (block_id) REFERENCES blocks(id)
                               ON DELETE RESTRICT
);
CREATE INDEX idx_units_block  ON units(block_id);
CREATE INDEX idx_units_status ON units(status);