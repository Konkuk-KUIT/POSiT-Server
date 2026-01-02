CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL,
                       name VARCHAR(255),
                       created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)
);