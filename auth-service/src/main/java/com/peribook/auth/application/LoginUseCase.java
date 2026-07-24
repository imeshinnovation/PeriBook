package com.peribook.auth.application;

import com.peribook.auth.domain.Email;
import com.peribook.auth.domain.Usuario;
import com.peribook.auth.domain.UsuarioRepository;

/**
 * Caso de uso: "Autenticar usuario".
 * 

 * Representa la operación de login siguiendo el patrón Use Case de
 * Clean Architecture / DDD. Esta clase orquesta el flujo completo:
 * valida el email, busca el usuario en el repositorio, verifica la
 * contraseña delegando en Usuario#autenticar(String) y genera
 * el JWT a través de JwtService.
 * 
 * 

 * Aplico el Principio de Responsabilidad Única (SRP): esta clase solo
 * se ocupa de orquestar la autenticación. La lógica de hashing está en
 * com.peribook.auth.domain.Password, la búsqueda en el repositorio
 * está abstraída detrás de UsuarioRepository, y la generación del
 * token está en JwtService. Si mañana cambia el algoritmo de firma
 * o la fuente de datos, no toco esta clase.
 * 
 * 

 * Está anotada con  de Spring, pero eso es un detalle de
 * infraestructura para que el contenedor la registre como bean. La clase
 * en sí no usa nada de Spring excepto la anotación — si migro a Micronaut
 * o Quarkus, solo cambio la anotación.
 * 
 *
 * @author Alexander Rubio Cáceres
 */
import org.springframework.stereotype.Service;

@Service
public class LoginUseCase {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    /**
     * Inyección por constructor — la forma que recomiendo porque hace
     * explícitas las dependencias, facilita el testing con mocks y evita
     * la complejidad de  en campos privados.
     */
    public LoginUseCase(UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    /**
     * Ejecuta el caso de uso de inicio de sesión.
     * 

     * El flujo es simple pero crítico:
     * 
     *    * - Crea un Value Object Email (lanza excepción si el formato es inválido)
     *    * - Busca el usuario en el repositorio — si no existe, falla con
     *       AutenticacionFallidaException sin revelar si el email existe o no
     *    * - Verifica la contraseña contra el hash BCrypt almacenado
     *    * - Genera el JWT con RS256
     *    * - Devuelve un LoginResult inmutable con token y datos del usuario
     * 
     * 
     *
     * @param emailStr    email en texto plano (se valida al construir el Value Object)
     * @param rawPassword contraseña en texto plano para verificar
     * @return resultado exitoso con token, userId y alias
     * @throws AutenticacionFallidaException si las credenciales son inválidas
     */
    public LoginResult login(String emailStr, String rawPassword) {
        // Construir el Value Object lanza excepción si el formato no es válido.
        // Esto ocurre antes de tocar el repositorio — fail fast.
        Email email = new Email(emailStr);

        // Buscar por email: si no existe, el mensaje de error es genérico.
        // Nunca digo "email no encontrado" para evitar enumeración.
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new AutenticacionFallidaException("Credenciales inválidas"));

        // Verificar contraseña: misma excepción, mismo mensaje genérico.
        if (!usuario.autenticar(rawPassword)) {
            throw new AutenticacionFallidaException("Credenciales inválidas");
        }

        // Generar JWT y devolver resultado.
        String token = jwtService.generate(usuario.id().toString(), usuario.email().value());

        return new LoginResult(token, usuario.id().toString(), usuario.alias());
    }

    /**
     * Resultado exitoso del login.
     * 

     * Usé un  interno (Java 21) porque es inmutable, conciso
     * y no necesita una clase separada en otro archivo — su contexto es
     * únicamente el caso de uso de login. Si en el futuro necesito más datos
     * en la respuesta (roles, permisos, etc.), este record evoluciona junto
     * con el caso de uso.
     * 
     */
    public record LoginResult(String token, String userId, String alias) {}
}
