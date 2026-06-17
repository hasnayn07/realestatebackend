-- V1__init_mysql.sql
CREATE TABLE users (
                       id BINARY(16) PRIMARY KEY,
                       email VARCHAR(190) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       full_name VARCHAR(120) NOT NULL,
                       role VARCHAR(32) NOT NULL,
                       enabled TINYINT(1) NOT NULL DEFAULT 1,
                       mfa_secret VARCHAR(64),
                       mfa_enabled TINYINT(1) NOT NULL DEFAULT 0,
                       email_otp VARCHAR(6),
                       email_otp_expires_at DATETIME(6),
                       created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

CREATE TABLE password_reset_token (
                                      id BINARY(16) PRIMARY KEY,
                                      user_id BINARY(16) NOT NULL,
                                      token VARCHAR(120) NOT NULL UNIQUE,
                                      expires_at DATETIME(6) NOT NULL,
                                      used TINYINT(1) NOT NULL DEFAULT 0,
                                      CONSTRAINT fk_pr_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
