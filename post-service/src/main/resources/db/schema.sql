-- PL/pgSQL: procedure mínimo requerido (ADR 0002, Fase 4)
-- Crea una publicación validando que el contenido no exceda 500 caracteres.
CREATE OR REPLACE FUNCTION sp_crear_publicacion(
    p_id UUID,
    p_autor_id UUID,
    p_contenido VARCHAR(500),
    p_creada_en TIMESTAMP WITH TIME ZONE
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
<!-- 2026-07-09 -->
