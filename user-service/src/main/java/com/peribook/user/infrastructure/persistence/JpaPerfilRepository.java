package com.peribook.user.infrastructure.persistence;

import com.peribook.user.domain.Perfil;
import com.peribook.user.domain.PerfilRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador JPA del puerto PerfilRepository (patrón Repository / Hexagonal).
 * 

 * Esta clase es el "puente" entre el mundo del dominio (donde solo existen
 * , , etc.) y el mundo de la infraestructura (JPA,
 * tablas, entidades). Implementa la interfaz de dominio 
 * usando JpaRepository de Spring Data para la parte técnica.
 * 
 * 

 * Decidí heredar de ambas interfaces ( y )
 * en lugar de crear una clase separada que las componga. Esto evita el boilerplate
 * de una clase intermedia y, al usar métodos , Spring Data las reconoce
 * y las convierte en beans transaccionales sin problemas. La conversión entre
 *  y  ocurre aquí mismo, en la frontera del
 * hexágono.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
@Repository
public interface JpaPerfilRepository extends PerfilRepository, JpaRepository<PerfilEntity, UUID> {

    /**
     * Busca por ID delegando en JPA y mapea la entidad JPA al agregado de dominio.
     * Uso  en la interfaz para que Spring Data pueda crear el proxy
     * transaccional sin necesidad de una clase concreta.
     */
    @Override
    default Optional<Perfil> buscarPorId(UUID id) {
        return this.findById(id).map(PerfilEntity::toDomain);
    }

    /**
     * Persiste el agregado de dominio convirtiéndolo a entidad JPA primero.
     * Uso  en lugar de  para obtener el estado
     * actualizado inmediatamente — en un microservicio con transacciones cortas
     * prefiero la consistencia inmediata al rendimiento diferido.
     */
    @Override
    default Perfil save(Perfil perfil) {
        return this.saveAndFlush(PerfilEntity.fromDomain(perfil)).toDomain();
    }
}
