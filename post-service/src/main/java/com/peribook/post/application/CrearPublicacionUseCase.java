package com.peribook.post.application;

import com.peribook.post.domain.EventPublisher;
import com.peribook.post.domain.Publicacion;
import com.peribook.post.domain.PublicacionCreada;
import com.peribook.post.domain.PublicacionRepository;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class CrearPublicacionUseCase {

    private final PublicacionRepository repository;
    private final EventPublisher eventPublisher;

    public CrearPublicacionUseCase(PublicacionRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public Publicacion ejecutar(UUID autorId, String contenido) {
        Publicacion publicacion = Publicacion.crear(autorId, contenido);
        Publicacion guardada = repository.save(publicacion);
        eventPublisher.publish(PublicacionCreada.desde(guardada));
        return guardada;
    }
}
