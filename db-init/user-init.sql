-- =============================================
-- GDLDV User Database Initialization
-- =============================================

-- Set character set
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- The database is already created by Docker environment variables
-- USE gdldv_user_db;

-- Tables will be auto-created by JPA/Hibernate with ddl-auto=update

-- Optional: Insert default admin user
-- Password should be encrypted with BCrypt in real application
-- Default password: admin123 (BCrypt hash below)

/*
INSERT INTO user (id, email, password, first_name, last_name, phone, role, status, created_at, updated_at)
VALUES
(1, 'admin@gdldv.com', '$2a$10$N9qo8uLOickgx2ZMRZoMeO7iAfQ6YEmXB7QPnJULlNvJKPQ.8fJ5O', 'Admin', 'GDLDV', '+221000000000', 'ADMIN', 'ACTIVE', NOW(), NOW());
*/

SET FOREIGN_KEY_CHECKS = 1;
