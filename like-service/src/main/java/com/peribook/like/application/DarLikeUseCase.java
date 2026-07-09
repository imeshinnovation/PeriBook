package com.peribook.like.application;

import com.peribook.like.domain.EventPublisher;
import com.peribook.like.domain.Like;
import com.peribook.like.domain.LikeRegistrado;
import com.peribook.like.domain.LikeRepository;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class DarLikeUseCase {

    private final LikeRepository repository;
    private final EventPublisher eventPublisher;

    public DarLikeUseCase(LikeRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Registra un like. Si ya existe (duplicado), es idempotente: no lanza excepción
     * ni duplica el registro — simplemente devuelve el like existente sin publicar evento.
     */
    public Resultado ejecutar(UUID publicacionId, UUID usuarioId) {
        return repository.buscarPorPublicacionYUsuario(publicacionId, usuarioId)
                .map(like -> new Resultado(like, false))  // ya existía, no se publica evento
                .orElseGet(() -> {
                    Like like = repository.save(Like.dar(publicacionId, usuarioId));
                    eventPublisher.publish(LikeRegistrado.desde(like));
                    return new Resultado(like, true);
                });
    }

    public record Resultado(Like like, boolean esNuevo) {}

    public long contarPorPublicacion(UUID publicacionId) {
        return repository.contarPorPublicacion(publicacionId);
    }
}
