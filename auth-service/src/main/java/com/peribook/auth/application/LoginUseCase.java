package com.peribook.auth.application;

import com.peribook.auth.domain.Email;
import com.peribook.auth.domain.Usuario;
import com.peribook.auth.domain.UsuarioRepository;

/**
 * Caso de uso: autenticar un usuario con email y contraseña.
 * Devuelve un JWT si las credenciales son válidas.
 *
 * SRP: esta clase solo orquesta la autenticación. La firma del JWT
 * se delega en {@link JwtService} y el hashing en {@link Password}.
 */
public class LoginUseCase {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public LoginUseCase(UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    /**
     * Ejecuta el caso de uso de login.
     *
     * @param emailStr     email en texto plano
     * @param rawPassword  contraseña en texto plano
     * @return resultado con token JWT y datos del usuario
     * @throws AutenticacionFallidaException si las credenciales no son válidas
     */
    public LoginResult login(String emailStr, String rawPassword) {
        Email email = new Email(emailStr);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new AutenticacionFallidaException("Credenciales inválidas"));

        if (!usuario.autenticar(rawPassword)) {
            throw new AutenticacionFallidaException("Credenciales inválidas");
        }

        String token = jwtService.generate(usuario.id().toString(), usuario.email().value());

        return new LoginResult(token, usuario.id().toString(), usuario.alias());
    }

    /**
     * Resultado exitoso del login.
     */
    public record LoginResult(String token, String userId, String alias) {}
}
