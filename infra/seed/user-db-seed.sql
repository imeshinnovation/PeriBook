-- ============================================================================
-- PeriBook — Seed data para user-db (Postgres)
-- Se ejecuta con: psql -h localhost -p 5433 -U peribook -d peribook_user -f user-db-seed.sql
-- ============================================================================

CREATE TABLE IF NOT EXISTS perfiles (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    alias VARCHAR(50) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE
);

-- Perfiles de prueba (vinculados a los IDs del seeder de auth-service)
INSERT INTO perfiles (id, usuario_id, email, alias, nombres, apellidos, fecha_nacimiento) VALUES
(
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '11111111-1111-1111-1111-111111111111',
    'ana@peribook.com',
    'ana_writer',
    'Ana',
    'García',
    '1995-03-15'
),
(
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '22222222-2222-2222-2222-222222222222',
    'carlos@peribook.com',
    'carlos_reader',
    'Carlos',
    'López',
    '1990-08-22'
)
ON CONFLICT (id) DO NOTHING;
<!-- 2026-07-09 -->
