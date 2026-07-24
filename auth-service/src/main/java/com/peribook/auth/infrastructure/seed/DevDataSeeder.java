package com.peribook.auth.infrastructure.seed;

import com.peribook.auth.domain.Email;
import com.peribook.auth.domain.Password;
import com.peribook.auth.domain.Usuario;
import com.peribook.auth.domain.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Sembrador de datos de prueba para el perfil .
 * 

 * Implementa CommandLineRunner para ejecutarse automáticamente después
 * de que el contexto de Spring se haya inicializado. Está anotado con
 * , así que solo se activa cuando el perfil activo es
 *  — en producción (perfiles , ) este
 * bean ni siquiera se crea.
 * 
 * 

 * Decidí usar UUIDs fijos para los usuarios de prueba en vez de generarlos
 * aleatoriamente. Esto es deliberado: tener IDs predecibles facilita las
 * pruebas manuales y automatizadas (sé exactamente qué ID tiene cada usuario
 * sin tener que consultar la BD). Además, los IDs fijos son útiles para
 * entornos de integración donde otros servicios necesitan referenciar estos
 * usuarios.
 * 
 * 

 * El seeder es idempotente: verifica si el usuario "ana@peribook.com" ya
 * existe antes de insertar. Esto permite reiniciar el servicio sin duplicar
 * datos cada vez.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);
    private final UsuarioRepository usuarioRepository;

    public DevDataSeeder(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Ejecuta la siembra de datos después del arranque de la aplicación.
     * 

     * Verifica primero si el seed ya se ejecutó (buscando un usuario conocido).
     * Si existe, salta la inserción para ser idempotente. Si no existe, inserta
     * tres usuarios de prueba con roles implícitos: escritor, lector y admin.
     * 
     *
     * @param args argumentos de línea de comandos (no se usan)
     */
    @Override
    public void run(String... args) {
        // Verificación de idempotencia: si el primer usuario ya existe, asumo
        // que el seed ya se ejecutó. No verifico los tres porque si el seed
        // falló a medio camino, esta comprobación tampoco es perfecta, pero
        // para un entorno dev es suficiente.
        if (usuarioRepository.findByEmail(new Email("ana@peribook.com")).isPresent()) {
            log.info("Seed ya ejecutado — usuarios de prueba existentes. Saltando.");
            return;
        }

        log.info("Insertando usuarios de prueba (profile dev)...");

        // Usuario 1: ana_writer — pensado para probar funcionalidades de escritura
        usuarioRepository.save(Usuario.reconstituir(
                java.util.UUID.fromString("11111111-1111-1111-1111-111111111111"),
                new Email("ana@peribook.com"),
                Password.fromRaw("secreto123"),
                "ana_writer"));

        // Usuario 2: carlos_reader — pensado para probar funcionalidades de lectura
        usuarioRepository.save(Usuario.reconstituir(
                java.util.UUID.fromString("22222222-2222-2222-2222-222222222222"),
                new Email("carlos@peribook.com"),
                Password.fromRaw("secreto123"),
                "carlos_reader"));

        // Usuario 3: admin_root — pensado para probar endpoints administrativos
        usuarioRepository.save(Usuario.reconstituir(
                java.util.UUID.fromString("33333333-3333-3333-3333-333333333333"),
                new Email("admin@peribook.com"),
                Password.fromRaw("admin1234"),
                "admin_root"));

        log.info("Seed completado: 3 usuarios de prueba insertados.");
    }
}
