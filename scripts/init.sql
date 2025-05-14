-- Criação das tabelas
-- Já que estamos usando spring.jpa.hibernate.ddl-auto=update, 
-- este arquivo é principalmente para inserir dados iniciais

-- Inserir roles padrão
INSERT INTO roles (name) VALUES ('ROLE_USER') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON CONFLICT (name) DO NOTHING;

-- Inserir um usuário admin inicial (senha 'admin123')
-- A senha hash corresponde a 'admin123' encriptada com BCrypt
INSERT INTO users (name, email, password, created_at, updated_at)
VALUES (
    'Admin User', 
    'admin@example.com', 
    '$2a$10$ixlPY3AAd4ty1l6E2IsQ9OFZi2ba9ZQE0bP7RFcGIWNhyFrrT3YUi',
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@example.com' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING; 