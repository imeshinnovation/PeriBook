package com.peribook.auth.application;

import com.peribook.auth.domain.Email;
import com.peribook.auth.domain.Password;
import com.peribook.auth.domain.Usuario;
import com.peribook.auth.domain.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    private LoginUseCase loginUseCase;

    private Usuario usuarioDePrueba;
    private static final String RAW_PASSWORD = "secreto123";

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(usuarioRepository, jwtService);

        usuarioDePrueba = Usuario.registrar(
                new Email("ana@peribook.com"),
                Password.fromRaw(RAW_PASSWORD),
                "ana_writer");
    }

    @Test
    @DisplayName("Debe devolver token JWT cuando las credenciales son correctas")
    void debeAutenticarConCredencialesCorrectas() {
        when(usuarioRepository.findByEmail(any(Email.class)))
                .thenReturn(Optional.of(usuarioDePrueba));
        when(jwtService.generate(
                usuarioDePrueba.id().toString(),
                usuarioDePrueba.email().value()))
                .thenReturn("jwt.token.firmado");

        LoginUseCase.LoginResult result = loginUseCase.login("ana@peribook.com", RAW_PASSWORD);

        assertThat(result.token()).isEqualTo("jwt.token.firmado");
        assertThat(result.userId()).isEqualTo(usuarioDePrueba.id().toString());
        assertThat(result.alias()).isEqualTo("ana_writer");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    void debeFallarConUsuarioInexistente() {
        when(usuarioRepository.findByEmail(any(Email.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.login("noexiste@peribook.com", "cualquiera"))
                .isInstanceOf(AutenticacionFallidaException.class)
                .hasMessageContaining("Credenciales inválidas");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la contraseña es incorrecta")
    void debeFallarConPasswordIncorrecto() {
        when(usuarioRepository.findByEmail(any(Email.class)))
                .thenReturn(Optional.of(usuarioDePrueba));

        assertThatThrownBy(() -> loginUseCase.login("ana@peribook.com", "wrong-password"))
                .isInstanceOf(AutenticacionFallidaException.class)
                .hasMessageContaining("Credenciales inválidas");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el email tiene formato inválido")
    void debeFallarConEmailInvalido() {
        assertThatThrownBy(() -> loginUseCase.login("email-invalido", "cualquiera"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
<!-- 2026-07-09 -->
