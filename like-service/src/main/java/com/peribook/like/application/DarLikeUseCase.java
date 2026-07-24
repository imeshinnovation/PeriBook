package com.peribook.like.application;

import com.peribook.like.domain.EventPublisher;
import com.peribook.like.domain.Like;
import com.peribook.like.domain.LikeRegistrado;
import com.peribook.like.domain.LikeRepository;

import java.util.UUID;

import org.springframework.stereotype.Service;

/**
 * Caso de uso: "Dar Like a una publicacion".
 * <p>
 * Este es el corazon de la logica de negocio del microservicio. Decidi mantenerlo
 * como un {@code @Service} de Spring con dependencias explicitas en el constructor
 * (constructor injection) porque asi queda claro que necesita un repositorio y un
 * publicador de eventos para funcionar. Sigue el patron Use Case de Clean Architecture:
 * orquesta las operaciones entre el dominio y los puertos de salida sin exponer
 * detalles tecnologicos.
 * </p>
 *
 * @author Alexander Rubio Caceres
 */
@Service
public class DarLikeUseCase {

    private final LikeRepository repository;
    private final EventPublisher eventPublisher;

    // Inyeccion por constructor — prefiero esto a @Autowired para que las
    // dependencias sean explicitas y el test unitario pueda pasar mocks sin
    // necesidad de reflexion.
    public DarLikeUseCase(LikeRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Ejecuta la accion de dar like. La logica es idempotente: si el usuario ya
     * habia dado like a esa publicacion, no se duplica ni se lanza excepcion,
     * simplemente se devuelve el like existente con {@code esNuevo = false}.
     * <p>
     * Cuando es realmente nuevo, persisto el like y publico el evento de dominio
     * para que otros servicios (feed, notificaciones, realtime) reaccionen.
     * </p>
     *
     * @param publicacionId ID de la publicacion
     * @param usuarioId     ID del usuario que da el like
     * @return un {@link Resultado} con el like y la bandera {@code esNuevo}
     */
    public Resultado ejecutar(UUID publicacionId, UUID usuarioId) {
        return repository.buscarPorPublicacionYUsuario(publicacionId, usuarioId)
                .map(like -> new Resultado(like, false))  // ya existia, no publico evento
                .orElseGet(() -> {
                    Like like = repository.save(Like.dar(publicacionId, usuarioId));
                    eventPublisher.publish(LikeRegistrado.desde(like));
                    return new Resultado(like, true);
                });
    }

    /**
     * Record interno que encapsula el resultado de la operacion.
     * <p>
     * Use un record anidado en lugar de una clase separada porque esta estructura
     * solo tiene sentido dentro del caso de uso. La bandera {@code esNuevo} permite
     * al controlador decidir si responder HTTP 201 (CREATED) o 200 (OK).
     * </p>
     */
    public record Resultado(Like like, boolean esNuevo) {}

    /**
     * Consulta el total de likes de una publicacion.
     * <p>
     * Decidi poner este metodo aqui en lugar de crear un caso de uso separado
     * porque la logica es trivial (delegar al repositorio). Si la consulta creciera
     * en complejidad (cache, filtros, paginacion), la extraeria a su propio caso
     * de uso.
     * </p>
     */
    public long contarPorPublicacion(UUID publicacionId) {
        return repository.contarPorPublicacion(publicacionId);
    }
}
