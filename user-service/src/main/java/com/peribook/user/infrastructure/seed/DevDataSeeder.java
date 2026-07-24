package com.peribook.user.infrastructure.seed;

import com.peribook.user.domain.Email;
import com.peribook.user.domain.Perfil;
import com.peribook.user.domain.PerfilRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Sembrador de datos de desarrollo que inserta perfiles de prueba al arrancar.
 * <p>
 * Activado solo con el perfil {@code dev} — en producción no se ejecuta. Esto
 * evita que datos ficticios contaminen la base de datos real si alguien olvida
 * cambiar el perfil activo.
 * </p>
 *
 * @author Alexander Rubio Cáceres
 */
@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);
    private final PerfilRepository perfilRepository;

    public DevDataSeeder(PerfilRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    /**
     * Ejecuta la siembra de datos al iniciar la aplicación.
     * <p>
     * Decisión de diseño importante: los IDs de perfil son IGUALES a los IDs de
     * usuario del auth-service. Esto permite que el frontend (o el BFF) haga
     * {@code GET /api/users/{id}} usando directamente el {@code sub} del JWT sin
     * tener que consultar primero una tabla de mapeo entre userId y profileId.
     * Es una relación 1:1 plana — el perfil no tiene sentido sin el usuario.
     * </p>
     * <p>
     * Si el seed ya se ejecutó (detectado al buscar el primer perfil por ID),
     * se salta para no duplicar registros en cada reinicio.
     * </p>
     */
    @Override
    public void run(String... args) {
        // Estos UUIDs deben coincidir con los usuarios creados en auth-service en dev.
        // El ID del perfil es el mismo que el ID del usuario para simplificar las consultas.
        UUID anaUserId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID carlosUserId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        // Verificación idempotente: si el primer perfil ya existe, asumimos que el seed ya corrió.
        if (perfilRepository.buscarPorId(anaUserId).isPresent()) {
            log.info("Seed ya ejecutado. Saltando.");
            return;
        }

        // Perfil.id == usuarioId para que el frontend pueda buscar con el userId del JWT
        perfilRepository.save(Perfil.reconstituir(
                anaUserId, anaUserId, new Email("ana@peribook.com"), "ana_writer",
                "Ana", "García", LocalDate.of(1995, 3, 15)));

        perfilRepository.save(Perfil.reconstituir(
                carlosUserId, carlosUserId, new Email("carlos@peribook.com"), "carlos_reader",
                "Carlos", "López", LocalDate.of(1990, 8, 22)));

        log.info("Seed completado: 2 perfiles de prueba insertados.");
    }
}
