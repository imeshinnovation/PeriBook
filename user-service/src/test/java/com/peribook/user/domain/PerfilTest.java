package com.peribook.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

class PerfilTest {

    @Test
    @DisplayName("Debe crear un perfil válido")
    void debeCrearPerfilValido() {
        UUID userId = UUID.randomUUID();
        Perfil perfil = Perfil.crear(userId, new Email("ana@peribook.com"),
                "ana_w", "Ana", "García", LocalDate.of(1995, 3, 15));

        assertThat(perfil.id()).isNotNull();
        assertThat(perfil.usuarioId()).isEqualTo(userId);
        assertThat(perfil.nombres()).isEqualTo("Ana");
        assertThat(perfil.apellidos()).isEqualTo("García");
        assertThat(perfil.fechaNacimiento()).isEqualTo(LocalDate.of(1995, 3, 15));
    }

    @Test
    @DisplayName("Debe reconstituir perfil desde persistencia")
    void debeReconstituir() {
        UUID id = UUID.randomUUID();
        Perfil perfil = Perfil.reconstituir(id, UUID.randomUUID(),
                new Email("x@x.com"), "alias", "A", "B", null);

        assertThat(perfil.id()).isEqualTo(id);
    }

    @Test
    @DisplayName("Dos perfiles con mismo ID son iguales")
    void mismosIdDebenSerIguales() {
        UUID id = UUID.randomUUID();
        Perfil a = Perfil.reconstituir(id, UUID.randomUUID(), new Email("a@a.com"), "a", "A", "B", null);
        Perfil b = Perfil.reconstituir(id, UUID.randomUUID(), new Email("b@b.com"), "b", "C", "D", null);
        assertThat(a).isEqualTo(b);
    }

    @Test
    @DisplayName("Debe rechazar nombres vacíos")
    void debeRechazarNombresVacios() {
        assertThatThrownBy(() -> Perfil.crear(UUID.randomUUID(),
                new Email("a@a.com"), "a", "", "García", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
<!-- 2026-07-09 -->
