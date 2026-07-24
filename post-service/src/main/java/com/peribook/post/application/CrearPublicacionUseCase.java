package com.peribook.post.application;

import com.peribook.post.domain.EventPublisher;
import com.peribook.post.domain.Publicacion;
import com.peribook.post.domain.PublicacionCreada;
import com.peribook.post.domain.PublicacionRepository;

import java.util.UUID;

import org.springframework.stereotype.Service;

/**
 * Caso de uso: crear una nueva publicacion.
 * <p>
 * Este es el corazon de la operacion "escribir un post". Decidi modelarlo como un
 * caso de uso explicito (application service) en lugar de meter la logica directamente
 * en el controlador REST. Esto mantiene la capa de dominio limpia de frameworks y
 * permite que la transaccion se entienda sin necesidad de saber nada sobre HTTP o JSON.
 * <p>
 * Sigue el flujo clasico de DDD: el dominio crea la entidad con sus invariantes,
 * el repositorio la persiste y, si todo sale bien, se dispara un evento de dominio
 * para que otros servicios (feed, notificaciones, etc.) reaccionen de forma asincrona.
 *
 * @author Alexander Rubio Caceres
 */
@Service
public class CrearPublicacionUseCase {

    private final PublicacionRepository repository;
    private final EventPublisher eventPublisher;

    /**
     * Constructor con inyeccion de dependencias via Spring.
     * Uso inyeccion por constructor en lugar de @Autowired directo porque es mas facil
     * de testear (no requiere reflection) y hace las dependencias explicitas.
     */
    public CrearPublicacionUseCase(PublicacionRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Ejecuta la creacion de una publicacion.
     * <p>
     * El orden importa: primero se construye el objeto de dominio (que valida sus propias
     * reglas de negocio), luego se persiste y finalmente se publica el evento. Si la
     * validacion falla, ni siquiera se intenta acceder a la base de datos.
     *
     * @param autorId   identificador UUID del autor (viene del JWT)
     * @param contenido texto de la publicacion
     * @return la entidad Publicacion ya persistida y con sus invariantes validados
     */
    public Publicacion ejecutar(UUID autorId, String contenido) {
        Publicacion publicacion = Publicacion.crear(autorId, contenido);
        Publicacion guardada = repository.save(publicacion);
        // Disparo el evento de dominio para que otros bounded contexts reaccionen
        eventPublisher.publish(PublicacionCreada.desde(guardada));
        return guardada;
    }
}
