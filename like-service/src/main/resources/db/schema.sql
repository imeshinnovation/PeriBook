-- PL/pgSQL: procedure mínimo requerido (ADR 0002, Fase 5)
-- Registra un like evitando duplicados (ON CONFLICT DO NOTHING).
CREATE OR REPLACE FUNCTION sp_registrar_like(
    p_id UUID,
    p_publicacion_id UUID,
    p_usuario_id UUID,
    p_creado_en TIMESTAMP WITH TIME ZONE
) RETURNS UUID AS $$
BEGIN
    INSERT INTO likes (id, publicacion_id, usuario_id, creado_en)
    VALUES (p_id, p_publicacion_id, p_usuario_id, p_creado_en)
    ON CONFLICT (publicacion_id, usuario_id) DO NOTHING;

    -- Si ya existía, devolver el id existente; si no, el nuevo
    RETURN COALESCE(
        (SELECT id FROM likes WHERE publicacion_id = p_publicacion_id AND usuario_id = p_usuario_id),
        p_id
    );
END;
$$ LANGUAGE plpgsql;
<!-- 2026-07-09 -->
