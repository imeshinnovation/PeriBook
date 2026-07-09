package com.peribook.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioTest {

    @Test
    @DisplayName("Debe crear un usuario con UUID, email y alias válidos")
    void debeCrearUsuarioValido() {
        Email email = new Email("ana@peribook.com");
        Password password = Password.fromRaw("secreto123");

        Usuario usuario = Usuario.registrar(email, password, "ana");

        assertThat(usuario.id()).isNotNull();
        assertThat(usuario.email()).isEqualTo(email);
        assertThat(usuario.alias()).isEqualTo("ana");
    }

    @Test
    @DisplayName("Debe autenticar con contraseña correcta")
    void debeAutenticarConPasswordCorrecto() {
        Password password = Password.fromRaw("secreto123");
        Usuario usuario = Usuario.registrar(
                new Email("ana@peribook.com"), password, "ana");

        assertThat(usuario.autenticar("secreto123")).isTrue();
    }

    @Test
    @DisplayName("Debe rechazar autenticación con contraseña incorrecta")
    void debeRechazarPasswordIncorrecto() {
        Password password = Password.fromRaw("secreto123");
        Usuario usuario = Usuario.registrar(
                new Email("ana@peribook.com"), password, "ana");

        assertThat(usuario.autenticar("wrong-password")).isFalse();
    }

    @Test
    @DisplayName("Debe reconstituir un usuario desde persistencia con ID conocido")
    void debeReconstituirUsuario() {
        java.util.UUID id = java.util.UUID.randomUUID();
        Usuario usuario = Usuario.reconstituir(
                id,
                new Email("ana@peribook.com"),
                Password.fromRaw("secreto123"),
                "ana");

        assertThat(usuario.id()).isEqualTo(id);
    }

    @Test
    @DisplayName("Dos usuarios con mismo ID deben ser iguales")
    void mismosIdDebenSerIguales() {
        java.util.UUID id = java.util.UUID.randomUUID();
        Usuario a = Usuario.reconstituir(id,
                new Email("a@peribook.com"), Password.fromRaw("secreto1"), "a");
        Usuario b = Usuario.reconstituir(id,
                new Email("b@peribook.com"), Password.fromRaw("secreto2"), "b");

        assertThat(a).isEqualTo(b);
    }
}

class PasswordTest {

    @Test
    @DisplayName("Debe crear hash desde texto plano")
    void debeHashearPassword() {
        Password password = Password.fromRaw("secreto123");
        assertThat(password.hash()).isNotNull();
        assertThat(password.hash()).isNotEqualTo("secreto123");
    }

    @Test
    @DisplayName("Debe verificar coincidencia correcta")
    void debeVerificarMatch() {
        Password password = Password.fromRaw("secreto123");
        assertThat(password.matches("secreto123")).isTrue();
    }

    @Test
    @DisplayName("Debe rechazar texto plano que no coincide")
    void debeRechazarNoMatch() {
        Password password = Password.fromRaw("secreto123");
        assertThat(password.matches("otra-clave")).isFalse();
    }

    @Test
    @DisplayName("Debe rechazar password menor a 8 caracteres")
    void debeRechazarPasswordCorto() {
        assertThatThrownBy(() -> Password.fromRaw("1234567"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("8 caracteres");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Debe rechazar password nulo o vacío")
    void debeRechazarPasswordNuloOVacio(String raw) {
        assertThatThrownBy(() -> Password.fromRaw(raw))
                .isInstanceOfAny(IllegalArgumentException.class, NullPointerException.class);
    }
}

class EmailTest {

    @Test
    @DisplayName("Debe crear email válido")
    void debeCrearEmailValido() {
        Email email = new Email("ana@peribook.com");
        assertThat(email.value()).isEqualTo("ana@peribook.com");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "sinarroba", "@sinlocal.com", "sin-dominio@"})
    @DisplayName("Debe rechazar emails con formato inválido")
    void debeRechazarEmailInvalido(String emailStr) {
        assertThatThrownBy(() -> new Email(emailStr))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Debe rechazar email nulo")
    void debeRechazarEmailNulo() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(NullPointerException.class);
    }
}
<!-- 2026-07-09 -->
