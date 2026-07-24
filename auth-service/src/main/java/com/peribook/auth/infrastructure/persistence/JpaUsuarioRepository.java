package com.peribook.auth.infrastructure.persistence;

import com.peribook.auth.domain.Email;
import com.peribook.auth.domain.Usuario;
import com.peribook.auth.domain.UsuarioRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de persistencia que implementa el puerto {@link UsuarioRepository}
 * usando Spring Data JPA.
 * <p>
 * Esta clase es el "adapter secundario" en Hexagonal Architecture: implementa
 * una interfaz definida en el dominio usando tecnología de infraestructura (JPA).
 * El dominio no sabe que existe JPA, ni esta clase sabe del dominio más allá
 * de implementar su interfaz — exactamente como dicta el Principio de Inversión
 * de Dependencias.
 * </p>
 * <p>
 * Extiendo tanto {@link UsuarioRepository} (mi interfaz de dominio) como
 * {@link JpaRepository} (la interfaz de Spring Data) para heredar los métodos
 * CRUD estándar. La magia está en que Spring Data genera automáticamente la
 * implementación de {@code findByEmailIgnoreCase}, y yo solo implemento los
 * métodos del repositorio de dominio usando {@code default} methods para
 * sobreescribir y traducir entre dominio y entidad JPA.
 * </p>
 * <p>
 * Uso {@code saveAndFlush} en lugar de {@code save} para forzar la escritura
 * inmediata en la BD, asegurando que el ID generado (aunque en nuestro caso
 * se genera en el dominio) esté disponible de inmediato. Es una decisión
 * conservadora: prefiero una escritura síncrona aquí a tener bugs intermitentes
 * por lazy flushing en escenarios complejos.
 * </p>
 *
 * @author Alexander Rubio Cáceres
 */
@Repository
public interface JpaUsuarioRepository extends UsuarioRepository, JpaRepository<UsuarioEntity, UUID> {

    /**
     * Busca por email ignorando mayúsculas/minúsculas.
     * Lo delego a Spring Data JPA que genera la query automáticamente
     * basándose en el nombre del método.
     */
    Optional<UsuarioEntity> findByEmailIgnoreCase(String email);

    /**
     * Implementa la búsqueda del dominio ({@link UsuarioRepository#findByEmail}).
     * Traduce el Value Object {@link Email} a String para la consulta JPA,
     * y mapea el resultado de entidad JPA a agregado de dominio.
     *
     * @param email el Value Object de email del dominio
     * @return el usuario de dominio si existe, Optional vacío si no
     */
    @Override
    default Optional<Usuario> findByEmail(Email email) {
        return findByEmailIgnoreCase(email.value())
                .map(UsuarioEntity::toDomain);
    }

    /**
     * Implementa el guardado del dominio ({@link UsuarioRepository#save}).
     * Convierte el agregado de dominio a entidad JPA, persiste con
     * {@code saveAndFlush} y devuelve el agregado reconstituido.
     *
     * @param usuario el agregado Usuario del dominio a persistir
     * @return el usuario persistido convertido de vuelta a agregado de dominio
     */
    @Override
    default Usuario save(Usuario usuario) {
        UsuarioEntity entity = UsuarioEntity.fromDomain(usuario);
        // saveAndFlush fuerza el flush inmediato. Prefiero esto a depender del
        // flush automático de JPA que puede diferir la escritura y causar
        // inconsistencias si otro servicio consulta inmediatamente después.
        UsuarioEntity saved = this.saveAndFlush(entity);
        return saved.toDomain();
    }
}
