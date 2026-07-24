package com.peribook.post.infrastructure.persistence;

import com.peribook.post.domain.Publicacion;
import com.peribook.post.domain.PublicacionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador JPA del puerto PublicacionRepository (Arquitectura Hexagonal).
 * 

 * Esta interfaz es la joya de la adaptacion: extiende tanto el puerto de dominio
 * (PublicacionRepository) como la interfaz tecnica de Spring Data JPA
 * (JpaRepository). Esto me permite implementar los metodos del dominio
 * usando consultas derivadas de Spring Data sin perder la abstraccion del dominio.
 * 

 * Use metodos  en la interfaz para hacer la conversion entre
 * PublicacionEntity (JPA) y Publicacion (dominio). Prefiero esta
 * estrategia antes que crear una clase separada "RepositoryAdapter" porque mantiene
 * todo en un solo lugar y es mas facil de leer. La conversion la delego a los
 * metodos  y  de la entidad JPA.
 * 

 * Llame  en lugar de  para forzar el flush
 * inmediato y asegurar que el ID generado (aunque lo generamos en dominio) se
 * refleje en la base de datos antes de publicar el evento. Esto evita condiciones
 * de carrera si un consumidor del evento intenta leer la publicacion antes de que
 * se haya completado el flush.
 *
 * @author Alexander Rubio Caceres
 */
@Repository
public interface JpaPublicacionRepository
        extends PublicacionRepository, JpaRepository<PublicacionEntity, UUID> {

    /**
     * Persiste una publicacion: convierte de dominio a entidad JPA, guarda y
     * reconvierte a dominio. El flush forzado garantiza consistencia inmediata.
     */
    @Override
    default Publicacion save(Publicacion p) {
        return this.saveAndFlush(PublicacionEntity.fromDomain(p)).toDomain();
    }

    /**
     * Busca por ID: usa el metodo findById de Spring Data JPA y mapea el resultado
     * a la entidad de dominio si existe.
     */
    @Override
    default Optional<Publicacion> buscarPorId(UUID id) {
        return this.findById(id).map(PublicacionEntity::toDomain);
    }

    /**
     * Lista las publicaciones mas recientes usando una query method de Spring Data,
     * limitando el resultado en memoria (por ahora). El limite se pasa desde el
     * caso de uso que ya lo acoto a 50.
     * 

     * Se que hacer el limit en memoria no es ideal para grandes volumenes de datos,
     * pero para la escala inicial de PeriBook es suficiente. Cuando crezca, cambiare
     * esto por una consulta nativa con  o paginacion con cursores.
     */
    @Override
    default List<Publicacion> listarRecientes(int limite) {
        return this.findAllByOrderByCreadaEnDesc()
                .stream()
                .limit(limite)
                .map(PublicacionEntity::toDomain)
                .toList();
    }

    /**
     * Metodo de query derivado de Spring Data: genera automaticamente
     * .
     * La convencion de nombre de Spring Data me evita escribir JPQL manual.
     */
    List<PublicacionEntity> findAllByOrderByCreadaEnDesc();
}
