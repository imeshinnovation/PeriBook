-- ============================================================================
-- PeriBook — Seed data para post-db (Postgres)
-- Se ejecuta con: psql -h localhost -p 5434 -U peribook -d peribook_post -f post-db-seed.sql
-- ============================================================================

CREATE TABLE IF NOT EXISTS publicaciones (
    id UUID PRIMARY KEY,
    autor_id UUID NOT NULL,
    contenido VARCHAR(500) NOT NULL,
    creada_en TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Procedure PL/pgSQL: sp_crear_publicacion
CREATE OR REPLACE FUNCTION sp_crear_publicacion(
    p_id UUID,
    p_autor_id UUID,
    p_contenido VARCHAR(500),
    p_creada_en TIMESTAMPTZ
) RETURNS UUID AS $$
BEGIN
    IF length(p_contenido) = 0 THEN
        RAISE EXCEPTION 'El contenido no puede estar vacío';
    END IF;
    IF length(p_contenido) > 500 THEN
        RAISE EXCEPTION 'El contenido excede los 500 caracteres';
    END IF;
    INSERT INTO publicaciones (id, autor_id, contenido, creada_en)
    VALUES (p_id, p_autor_id, p_contenido, p_creada_en);
    RETURN p_id;
END;
$$ LANGUAGE plpgsql;

-- Publicaciones de prueba
INSERT INTO publicaciones (id, autor_id, contenido, creada_en) VALUES
(
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    '11111111-1111-1111-1111-111111111111',
    '¡Bienvenidos a PeriBook! Esta es la primera publicación 🎉',
    NOW() - INTERVAL '2 hours'
),
(
    'dddddddd-dddd-dddd-dddd-dddddddddddd',
    '22222222-2222-2222-2222-222222222222',
    'Hola a todos. Me encanta esta red social verde oliva 🌿',
    NOW() - INTERVAL '1 hour'
)
ON CONFLICT (id) DO NOTHING;
<!-- 2026-07-09 -->
