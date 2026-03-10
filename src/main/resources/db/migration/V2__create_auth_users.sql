CREATE TABLE IF NOT EXISTS auth_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(191) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_auth_user_email UNIQUE (email)
);

-- Demo credentials for local development:
-- admin@shakeart.fr / password
-- user@shakeart.fr / password
INSERT INTO auth_user (email, password_hash, role, enabled)
VALUES
    ('admin@shakeart.fr', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi2uN1h4nT7A5jBq5UG2RgxN6E466zW', 'ADMIN', TRUE),
    ('user@shakeart.fr', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi2uN1h4nT7A5jBq5UG2RgxN6E466zW', 'USER', TRUE)
ON DUPLICATE KEY UPDATE email = VALUES(email);
