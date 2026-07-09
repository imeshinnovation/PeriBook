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

@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);
    private final UsuarioRepository usuarioRepository;

    public DevDataSeeder(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByEmail(new Email("ana@peribook.com")).isPresent()) {
            log.info("Seed ya ejecutado — usuarios de prueba existentes. Saltando.");
            return;
        }

        log.info("Insertando usuarios de prueba (profile dev)...");

        usuarioRepository.save(Usuario.reconstituir(
                java.util.UUID.fromString("11111111-1111-1111-1111-111111111111"),
                new Email("ana@peribook.com"),
                Password.fromRaw("secreto123"),
                "ana_writer"));

        usuarioRepository.save(Usuario.reconstituir(
                java.util.UUID.fromString("22222222-2222-2222-2222-222222222222"),
                new Email("carlos@peribook.com"),
                Password.fromRaw("secreto123"),
                "carlos_reader"));

        usuarioRepository.save(Usuario.reconstituir(
                java.util.UUID.fromString("33333333-3333-3333-3333-333333333333"),
                new Email("admin@peribook.com"),
                Password.fromRaw("admin1234"),
                "admin_root"));

        log.info("Seed completado: 3 usuarios de prueba insertados.");
    }
}
