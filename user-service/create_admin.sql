-- Créer un compte ADMIN
INSERT INTO users (first_name, last_name, email, password, phone_number, active, email_verified, created_at, updated_at)
VALUES ('Admin', 'Test', 'admin.test@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+33612345678', 1, 1, NOW(), NOW());

-- Attribuer le rôle ADMIN (ID du rôle ADMIN = 4)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, 4 FROM users u WHERE u.email = 'admin.test@gmail.com';

-- Créer un compte SUPER_ADMIN (différent de celui existant)
INSERT INTO users (first_name, last_name, email, password, phone_number, active, email_verified, created_at, updated_at)
VALUES ('SuperAdmin', 'Test', 'superadmin.test@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '+33612345679', 1, 1, NOW(), NOW());

-- Attribuer le rôle SUPER_ADMIN (ID du rôle SUPER_ADMIN = 5)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, 5 FROM users u WHERE u.email = 'superadmin.test@gmail.com';
