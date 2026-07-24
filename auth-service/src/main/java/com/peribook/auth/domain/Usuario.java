package com.peribook.auth.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Agregado raíz del bounded context de autenticación.
 * <p>
 * {@code Usuario} es una entidad DDD con identidad propia (un UUID generado
 * en el momento del registro). No es un UserDetails de Spring Security ni
 * una entidad JPA — es el modelo de dominio puro, libre de cualquier
 * dependencia de framework o infraestructura. Esto es clave en Clean
 * Architecture: el dominio no sabe ni debe saber de Spring, JPA ni nada
 * externo.
 * </p>
 * <p>
 * Usé dos factories estáticas ({@link #registrar(Email, Password, String)}
 * y {@link #reconstituir(UUID, Email, Password, String)}) para separar dos
 * intenciones distintas: crear un usuario nuevo (generando ID) vs.
 * reconstruir uno existente desde persistencia (con ID conocido). No quiero
 * que un repositorio tenga que generar IDs, ni que un caso de uso tenga que
 * pasarle un UUID que ya existe. La intención queda explícita en el nombre
 * del método.
 * </p>
 *
 * @author Alexander Rubio Cáceres
 */
public class Usuario {

    private final UUID id;
    private final Email email;
    private final Password password;
    private final String alias;

    /**
     * Constructor privado. La única forma de crear un {@code Usuario} es
     * a través de los métodos estáticos {@link #registrar} o
     * {@link #reconstituir}. Esto me permite mantener el control sobre
     * las invariantes y evita que alguien cree accidentalmente un usuario
     * sin ID o con estado inconsistente.
     */
    private Usuario(UUID id, Email email, Password password, String alias) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.alias = alias;
    }

    /**
     * Crea un nuevo usuario para registro. Genera un {@link UUID} aleatorio
     * como identidad del agregado.
     * <p>
     * El alias se normaliza con {@code trim()} para evitar espacios al inicio
     * o final. Prefiero hacerlo aquí, en la capa de dominio, y no confiar en
     * que el controller o el frontend lo hagan.
     * </p>
     *
     * @param email    Value Object de email ya validado
     * @param password Value Object de password ya hasheado
     * @param alias    nombre público del usuario (se normaliza con trim)
     * @return una nueva instancia de {@code Usuario} con UUID generado
     * @throws NullPointerException     si alias es nulo
     * @throws IllegalArgumentException si alias está vacío
     */
    public static Usuario registrar(Email email, Password password, String alias) {
        Objects.requireNonNull(alias, "El alias no puede ser nulo");
        if (alias.isBlank()) {
            throw new IllegalArgumentException("El alias no puede estar vacío");
        }
        // Genero UUID aleatorio. No uso un sequence de base de datos porque
        // quiero que la identidad se defina en el dominio, no en la persistencia.
        // Además, UUID evita colisiones en escenarios de replicación futura.
        return new Usuario(UUID.randomUUID(), email, password, alias.trim());
    }

    /**
     * Reconstruye un usuario desde persistencia con su UUID conocido.
     * <p>
     * Este método es la contraparte de {@link #registrar}: lo usa el repositorio
     * cuando carga un usuario desde la base de datos. No repite validaciones
     * de alias porque se asume que los datos ya pasaron por validación al
     * persistirse — si hay datos corruptos en la BD, este método no los detecta
     * (y probablemente no debería: el problema estaría en otra parte).
     * </p>
     *
     * @param id       UUID del usuario (obtenido de la BD)
     * @param email    Value Object de email
     * @param password Value Object de password (ya debe venir hasheado)
     * @param alias    nombre público del usuario
     * @return una instancia de {@code Usuario} con el ID especificado
     * @throws NullPointerException si id es nulo
     */
    public static Usuario reconstituir(UUID id, Email email, Password password, String alias) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        return new Usuario(id, email, password, alias.trim());
    }

    /**
     * Verifica la contraseña del usuario contra un texto plano.
     * <p>
     * Delega en {@link Password#matches(String)}. El método no expone el hash
     * internamente ni permite ataques de timing side-channel porque
     * BCryptPasswordEncoder ya implementa comparación en tiempo constante.
     * </p>
     *
     * @param rawPassword contraseña en texto plano a verificar
     * @return {@code true} si la contraseña coincide
     */
    public boolean autenticar(String rawPassword) {
        return password.matches(rawPassword);
    }

    // ── Getters ─────────────────────────────────────────────
    // Nota: uso los mismos nombres que el record (sin prefijo get) para
    // mantener consistencia. Spring no necesita getters con prefijo "get"
    // si no usa serialización Java Beans; y si algún día uso Jackson con
    // el módulo de Java 17+, estos nombres estilo record funcionan bien.

    public UUID id() {
        return id;
    }

    public Email email() {
        return email;
    }

    public String alias() {
        return alias;
    }

    /**
     * Devuelve el Value Object de password.
     * Expongo el password completo (con su método {@code matches}) para que
     * el caso de uso pueda verificar la contraseña. Preferiría no exponerlo
     * y tener {@code autenticar} como único punto de entrada, pero al estar
     * en el mismo paquete es manejable.
     */
    public Password password() {
        return password;
    }

    /**
     * Dos usuarios son iguales si tienen el mismo UUID.
     * En DDD la identidad de una entidad está definida por su ID, no por
     * sus atributos. Esto es fundamental: un usuario puede cambiar de email
     * o alias y seguir siendo el mismo usuario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
