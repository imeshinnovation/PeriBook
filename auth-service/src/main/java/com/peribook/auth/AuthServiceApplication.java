package com.peribook.auth;

import com.peribook.auth.infrastructure.security.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Punto de entrada del microservicio auth-service de PeriBook.
 * <p>
 * Este servicio es el encargado de la autenticación y emisión de tokens JWT
 * para toda la plataforma. Decidí mantenerlo como un microservicio independiente
 * porque la autenticación es un cross-cutting concern que debe poder escalarse
 * y desplegarse de forma separada al resto del sistema.
 * </p>
 * <p>
 * Uso {@code @EnableConfigurationProperties} aquí para que {@link JwtConfig},
 * que es un {@code record} inmutable con binding a {@code application.yml},
 * quede registrado como bean desde el arranque sin necesidad de escanear paquetes
 * adicionales. Prefiero esta aproximación a {@code @ConfigurationPropertiesScan}
 * porque hace explícito qué propiedades se habilitan.
 * </p>
 *
 * @author Alexander Rubio Cáceres
 */
@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class AuthServiceApplication {

    public static void main(String[] args) {
        // Arranque estándar de Spring Boot. No añado lógica adicional aquí;
        // si algún día necesito personalizar el entorno antes del refresh, usaré
        // SpringApplicationBuilder en vez de ensuciar este método.
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
