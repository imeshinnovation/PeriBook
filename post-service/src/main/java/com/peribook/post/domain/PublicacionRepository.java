package com.peribook.post.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PublicacionRepository {
    Publicacion save(Publicacion publicacion);
    Optional<Publicacion> buscarPorId(UUID id);
    List<Publicacion> listarRecientes(int limite);
}
<!-- 2026-07-09 -->
