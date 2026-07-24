package com.peribook.user.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Agregado raíz del bounded context de perfiles de usuario.
 * 

 * Cada  está vinculado 1:1 a un  del bounded context de auth,
 * pero no contiene una referencia directa al objeto Usuario — solo su UUID. Esto es
 * intencional: en un sistema con microservicios separados, no quiero que el perfil dependa
 * de un objeto que vive en otro servicio. La relación es conceptual y se resuelve en
 * el BFF o en el frontend, no en la base de datos del user-service.
 * 
 * 

 * Usé constructor privado con fábricas estáticas (Email, String, String, String, LocalDate)
 * y UUID, Email, String, String, String, LocalDate)) en lugar de
 * un constructor público o un builder porque quiero dejar claro que hay dos formas legítimas
 * de obtener un Perfil: una creándolo desde cero (nuevo agregado) y otra reconstituyéndolo
 * desde persistencia (agregado existente). Cada una tiene validaciones distintas.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
public class Perfil {

    /** Identificador único del perfil. Inmutable. */
    private final UUID id;

    /**
     * Referencia al Usuario del auth-service.
     * No es una clave foránea JPA — es un identificador conceptual que el BFF usa para
     * cruzar información entre servicios.
     */
    private final UUID usuarioId;

    /** Value Object de email con validación incorporada. */
    private final Email email;

    /** Seudónimo público. Opcional, se normaliza a cadena vacía si es null. */
    private final String alias;

    /** Nombre(s) real(es). Obligatorio, se trimea al crear. */
    private final String nombres;

    /** Apellido(s). Obligatorio, se trimea al crear. */
    private final String apellidos;

    /** Fecha de nacimiento. Opcional. */
    private final LocalDate fechaNacimiento;

    /**
     * Constructor privado — la única forma de instanciar un Perfil es a través de
     * las fábricas estáticas. Esto evita que alguien cree un Perfil directamente
     * sin pasar por las validaciones de dominio.
     */
    private Perfil(UUID id, UUID usuarioId, Email email, String alias,
                   String nombres, String apellidos, LocalDate fechaNacimiento) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.email = email;
        this.alias = alias;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Fabrica un Perfil nuevo para un usuario que se acaba de registrar.
     * Genera un UUID aleatorio como identidad del perfil, valida y normaliza los
     * campos de texto (trim) y deja el alias vacío si no se proporcionó.
     * Esta validación es de dominio — no confío en que la capa de aplicación
     * o el controlador hayan limpiado los datos.
     *
     * @param usuarioId       UUID del usuario en auth-service (obligatorio)
     * @param email           Value Object Email ya validado
     * @param alias           Seudónimo (opcional, se usa cadena vacía si es null)
     * @param nombres         Nombre(s) real(es) (obligatorio, no vacío)
     * @param apellidos       Apellido(s) (obligatorio, no vacío)
     * @param fechaNacimiento Fecha de nacimiento (opcional)
     * @return Perfil nuevo con UUID generado
     */
    public static Perfil crear(UUID usuarioId, Email email, String alias,
                               String nombres, String apellidos, LocalDate fechaNacimiento) {
        Objects.requireNonNull(usuarioId, "usuarioId no puede ser nulo");
        Objects.requireNonNull(nombres, "nombres no puede ser nulo");
        Objects.requireNonNull(apellidos, "apellidos no puede ser nulo");
        if (nombres.isBlank()) throw new IllegalArgumentException("nombres no puede estar vacío");
        if (apellidos.isBlank()) throw new IllegalArgumentException("apellidos no puede estar vacío");
        return new Perfil(UUID.randomUUID(), usuarioId, email,
                alias != null ? alias.trim() : "", nombres.trim(), apellidos.trim(), fechaNacimiento);
    }

    /**
     * Reconstituye un Perfil desde persistencia (base de datos o evento).
     * No valida los campos porque asumimos que ya pasaron validación al crearse
     * originalmente. Sí valida que el ID no sea nulo — si el repositorio devuelve
     * un ID nulo, algo está mal en la capa de infraestructura.
     * 

     * Decidí mantener esta ruta separada de Email, String, String, String, LocalDate)
     * porque la reconstitución puede venir de un event store, una proyección o un snapshot,
     * y en esos casos no quiero que se generen nuevos UUIDs ni se trimeen textos que
     * ya deberían estar normalizados.
     * 
     *
     * @param id              UUID existente del perfil (obligatorio)
     * @param usuarioId       UUID del usuario asociado
     * @param email           Value Object Email
     * @param alias           Seudónimo
     * @param nombres         Nombre(s)
     * @param apellidos       Apellido(s)
     * @param fechaNacimiento Fecha de nacimiento
     * @return Perfil reconstituido con el ID proporcionado
     */
    public static Perfil reconstituir(UUID id, UUID usuarioId, Email email, String alias,
                                      String nombres, String apellidos, LocalDate fechaNacimiento) {
        Objects.requireNonNull(id, "id no puede ser nulo");
        return new Perfil(id, usuarioId, email, alias, nombres, apellidos, fechaNacimiento);
    }

    // -- Getters de estilo "componente de record" (porque Perfil no es un record, pero me gusta la nomenclatura)

    public UUID id() { return id; }
    public UUID usuarioId() { return usuarioId; }
    public Email email() { return email; }
    public String alias() { return alias; }
    public String nombres() { return nombres; }
    public String apellidos() { return apellidos; }
    public LocalDate fechaNacimiento() { return fechaNacimiento; }

    /**
     * Dos Perfiles son iguales si comparten el mismo ID. Esto es clave en DDD:
     * la identidad del agregado la define su ID, no sus atributos.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Perfil perfil)) return false;
        return id.equals(perfil.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
