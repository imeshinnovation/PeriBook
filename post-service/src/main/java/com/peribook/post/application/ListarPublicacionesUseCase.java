package com.peribook.post.application;

import com.peribook.post.domain.Publicacion;
import com.peribook.post.domain.PublicacionRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ListarPublicacionesUseCase {

    private final PublicacionRepository repository;

    public ListarPublicacionesUseCase(PublicacionRepository repository) {
        this.repository = repository;
    }

    public List<Publicacion> ejecutar(int limite) {
        int max = Math.min(limite, 50); // máximo 50 por página
        return repository.listarRecientes(max);
    }
}
<!-- 2026-07-09 -->
