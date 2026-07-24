package com.peribook.auth.domain;

import java.util.Optional;

/**
 * Puerto (interfaz) del repositorio de Usuario.
 * 

 * En Clean Architecture, esta interfaz pertenece al dominio y define el
 * contrato que la infraestructura debe implementar. El dominio depende de
 * esta abstracción, no de una implementación concreta de JPA o cualquier
 * otra tecnología de persistencia. Así cumplimos con la Inversión de
 * Dependencias (DIP): las capas externas implementan lo que las internas
 * necesitan.
 * 
 * 

 * Solo definí dos métodos porque por ahora el caso de uso de login solo
 * necesita buscar por email y guardar usuarios (registro). El día que
 * necesite listar, paginar o buscar por ID, se agregan aquí. No me gusta
 * adelantarme a necesidades que aún no existen (YAGNI).
 * 
 *
 * @author Alexander Rubio Cáceres
 */
public interface UsuarioRepository {

    /**
     * Busca un usuario por su email.
     *
     * @param email Value Object de email, ya validado
     * @return un Optional con el usuario si existe, vacío si no
     */
    Optional<Usuario> findByEmail(Email email);

    /**
     * Guarda un usuario en el repositorio.
     *
     * @param usuario el agregado Usuario a persistir
     * @return el usuario persistido (con ID intacto)
     */
    Usuario save(Usuario usuario);
}
