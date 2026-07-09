package com.peribook.auth.infrastructure.persistence;

import com.peribook.auth.domain.Email;
import com.peribook.auth.domain.Password;
import com.peribook.auth.domain.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 50)
    private String alias;

    protected UsuarioEntity() {
        // JPA
    }

    private UsuarioEntity(UUID id, String email, String passwordHash, String alias) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.alias = alias;
    }

    /**
     * Convierte un agregado de dominio a entidad JPA.
     */
    public static UsuarioEntity fromDomain(Usuario usuario) {
        return new UsuarioEntity(
                usuario.id(),
                usuario.email().value(),
                usuario.password().hash(),
                usuario.alias());
    }

    /**
     * Convierte la entidad JPA a agregado de dominio.
     */
    public Usuario toDomain() {
        return Usuario.reconstituir(
                id,
                new Email(email),
                new Password(passwordHash),
                alias);
    }

    // ── Getters JPA ──────────────────────────────────────

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
