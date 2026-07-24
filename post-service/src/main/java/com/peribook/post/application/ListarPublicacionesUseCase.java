package com.peribook.post.application;

import com.peribook.post.domain.Publicacion;
import com.peribook.post.domain.PublicacionRepository;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * Caso de uso: listar publicaciones recientes.
 * 

 * Lo mantengo separado de CrearPublicacionUseCase porque son dos operaciones
 * con diferentes regimenes de concurrencia y requisitos de escalabilidad. Crear es
 * una operacion de escritura que dispara eventos; listar es puramente de lectura y
 * puede cachearse o escalarse horizontalmente sin problemas.
 * 

 * Decidi aplicar un limite maximo de 50 resultados en lugar de confiar ciegamente
 * en el parametro que llega del cliente. Esto evita que alguien pida 10 millones de
 * registros y tumbe la base de datos — es una practica de "defense in depth" que
 * siempre aplico en las capas de aplicacion.
 *
 * @author Alexander Rubio Caceres
 */
@Service
public class ListarPublicacionesUseCase {

    private final PublicacionRepository repository;

    /**
     * Inyeccion por constructor. Al ser un caso de uso de solo lectura solo necesito
     * el repositorio, sin EventPublisher ni otras dependencias.
     */
    public ListarPublicacionesUseCase(PublicacionRepository repository) {
        this.repository = repository;
    }

    /**
     * Obtiene las publicaciones mas recientes hasta un maximo de 50.
     * 

     * El limite se acota con  para que aunque el cliente envie un
     * valor desproporcionado, el sistema no se vea afectado. La paginacion real
     * se planea implementar con cursores mas adelante.
     *
     * @param limite cantidad solicitada (se trunca internamente a 50)
     * @return lista de publicaciones ordenadas por fecha descendente
     */
    public List<Publicacion> ejecutar(int limite) {
        int max = Math.min(limite, 50); // maximo 50 por pagina — proteccion contra abusos
        return repository.listarRecientes(max);
    }
}
