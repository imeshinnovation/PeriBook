-- ============================================================================
-- PeriBook — Seed data para auth-db (Postgres)
-- Se ejecuta con: psql -h localhost -p 5432 -U peribook -d peribook_auth -f auth-db-seed.sql
-- ============================================================================

-- Tabla de usuarios (debe coincidir con la entidad JPA UsuarioEntity)
CREATE TABLE IF NOT EXISTS usuarios (
    id UUID PRIMARY KEY,
    email VARCHAR(254) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    alias VARCHAR(50) NOT NULL
);

-- Usuarios de prueba (passwords hasheadas con BCrypt)
-- Contraseña en texto plano para los 3: "secreto123"
-- Hash generado con BCryptPasswordEncoder(12 rounds)

INSERT INTO usuarios (id, email, password_hash, alias) VALUES
(
    '11111111-1111-1111-1111-111111111111',
    'ana@peribook.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ana_writer'
),
(
    '22222222-2222-2222-2222-222222222222',
    'carlos@peribook.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'carlos_reader'
),
(
    '33333333-3333-3333-3333-333333333333',
    'admin@peribook.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'admin_root'
)
ON CONFLICT (id) DO NOTHING;
<!-- 2026-07-09 -->
