package com.peribook.user.infrastructure.persistence;

import com.peribook.user.domain.Email;
import com.peribook.user.domain.Perfil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "perfiles")
public class PerfilEntity {

    @Id private UUID id;
    @Column(nullable = false) private UUID usuarioId;
    @Column(nullable = false, unique = true, length = 254) private String email;
    @Column(nullable = false, length = 50) private String alias;
    @Column(nullable = false, length = 100) private String nombres;
    @Column(nullable = false, length = 100) private String apellidos;
    private LocalDate fechaNacimiento;

    protected PerfilEntity() {}

    private PerfilEntity(UUID id, UUID usuarioId, String email, String alias,
                         String nombres, String apellidos, LocalDate fechaNacimiento) {
        this.id = id; this.usuarioId = usuarioId; this.email = email;
        this.alias = alias; this.nombres = nombres; this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
    }

    public static PerfilEntity fromDomain(Perfil p) {
        return new PerfilEntity(p.id(), p.usuarioId(), p.email().value(),
                p.alias(), p.nombres(), p.apellidos(), p.fechaNacimiento());
    }

    public Perfil toDomain() {
        return Perfil.reconstituir(id, usuarioId, new Email(email),
                alias, nombres, apellidos, fechaNacimiento);
    }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public String getEmail() { return email; }
    public String getAlias() { return alias; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
}
<!-- 2026-07-09 -->
