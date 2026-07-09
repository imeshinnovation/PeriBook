-- ============================================================================
-- PeriBook — Seed data para like-db (Postgres)
-- Se ejecuta con: psql -h localhost -p 5435 -U peribook -d peribook_like -f like-db-seed.sql
-- ============================================================================

CREATE TABLE IF NOT EXISTS likes (
    id UUID PRIMARY KEY,
    publicacion_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (publicacion_id, usuario_id)
);

-- Procedure PL/pgSQL: sp_registrar_like (anti-duplicados)
CREATE OR REPLACE FUNCTION sp_registrar_like(
    p_id UUID,
    p_publicacion_id UUID,
    p_usuario_id UUID,
    p_creado_en TIMESTAMPTZ
) RETURNS UUID AS $$
BEGIN
    INSERT INTO likes (id, publicacion_id, usuario_id, creado_en)
    VALUES (p_id, p_publicacion_id, p_usuario_id, p_creado_en)
    ON CONFLICT (publicacion_id, usuario_id) DO NOTHING;

    RETURN COALESCE(
        (SELECT id FROM likes
         WHERE publicacion_id = p_publicacion_id AND usuario_id = p_usuario_id),
        p_id
    );
END;
$$ LANGUAGE plpgsql;

-- Likes de prueba
INSERT INTO likes (id, publicacion_id, usuario_id, creado_en) VALUES
(
    'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    '22222222-2222-2222-2222-222222222222',
    NOW() - INTERVAL '30 minutes'
)
ON CONFLICT (id) DO NOTHING;
