package com.peribook.auth.infrastructure.persistence;

import com.peribook.auth.domain.Email;
import com.peribook.auth.domain.Password;
import com.peribook.auth.domain.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Entidad JPA que mapea la tabla  en la base de datos.
 * 

 * Esta clase es el "traductor" entre el mundo del dominio y la persistencia.
 * No es un agregado de dominio ni un Value Object — es un detalle técnico
 * que solo existe para que JPA pueda hacer su trabajo. Por eso tiene getters
 * con prefijo "get" (los necesita JPA/Hibernate), mientras que el dominio
 * usa nomenclatura tipo record.
 * 
 * 

 * Es importante entender que Usuario (dominio) y 
 * (infraestructura) son clases distintas. No mezclo anotaciones JPA en el
 * dominio. El precio es tener que convertir de una a otra con
 * #fromDomain(Usuario) y #toDomain(), pero ese precio es
 * bajo comparado con el beneficio de tener un dominio limpio de
 * acoplamiento tecnológico.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    // No uso @GeneratedValue porque el ID se genera en el dominio (UUID).
    // La BD solo almacena el valor que el dominio le envía.
    private UUID id;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    /**
     * Almaceno solo el hash BCrypt, nunca la contraseña en texto plano.
     * La columna no tiene límite de longitud fijo porque BCrypt produce
     * hashes de 60 caracteres, pero si algún día cambiamos de algoritmo
     * la longitud podría variar.
     */
    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 50)
    private String alias;

    /**
     * Constructor vacío requerido por JPA. Es  en vez de
     *  para que Hibernate pueda acceder sin reflectividad
     * agresiva, pero sigue sin ser accesible desde fuera del paquete.
     */
    protected UsuarioEntity() {
        // JPA necesita este constructor vacío. No debe usarse directamente.
    }

    /**
     * Constructor privado para crear la entidad con todos los campos.
     * Solo se usa desde #fromDomain(Usuario).
     */
    private UsuarioEntity(UUID id, String email, String passwordHash, String alias) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.alias = alias;
    }

    /**
     * Convierte un agregado de dominio Usuario a una entidad JPA.
     * 

     * Extrae los valores de los Value Objects (Email#value(),
     * Password#hash()) para aplanarlos en columnas. Este es el
     * momento donde el modelo rico del dominio se "serializa" al modelo
     * plano de la base de datos.
     * 
     *
     * @param usuario el agregado raíz del dominio
     * @return entidad JPA lista para persistir
     */
    public static UsuarioEntity fromDomain(Usuario usuario) {
        return new UsuarioEntity(
                usuario.id(),
                usuario.email().value(),
                usuario.password().hash(),
                usuario.alias());
    }

    /**
     * Reconstruye un agregado de dominio Usuario desde esta entidad JPA.
     * 

     * Reconstituye los Value Objects Email y Password a partir
     * de los valores planos almacenados. Es el inverso de #fromDomain.
     * 
     *
     * @return un agregado  limpio, sin dependencias JPA
     */
    public Usuario toDomain() {
        return Usuario.reconstituir(
                id,
                new Email(email),
                new Password(passwordHash),
                alias);
    }

    // ── Getters JPA ──────────────────────────────────────
    // Nota: Hibernate necesita getters con prefijo "get" para el mapeo
    // de propiedades. No es negociable si uso acceso por propiedad en vez
    // de acceso por campo.

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getAlias() {
        return alias;
    }
}
