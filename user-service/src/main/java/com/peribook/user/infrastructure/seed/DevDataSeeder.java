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

@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);
    private final PerfilRepository perfilRepository;

    public DevDataSeeder(PerfilRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    @Override
    public void run(String... args) {
        // IDs de perfil = IDs de usuario (auth-service) para que GET /api/users/{id} funcione
        UUID anaUserId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID carlosUserId = UUID.fromString("22222222-2222-2222-2222-222222222222");

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
<!-- 2026-07-09 -->
