package com.peribook.post.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

class PublicacionTest {

    @Test
    @DisplayName("Debe crear publicación válida")
    void debeCrearPublicacion() {
        UUID autor = UUID.randomUUID();
        Publicacion p = Publicacion.crear(autor, "Hola mundo");
        assertThat(p.id()).isNotNull();
        assertThat(p.autorId()).isEqualTo(autor);
        assertThat(p.contenido()).isEqualTo("Hola mundo");
        assertThat(p.creadaEn()).isNotNull();
    }

    @Test
    @DisplayName("Debe rechazar contenido vacío")
    void debeRechazarContenidoVacio() {
        assertThatThrownBy(() -> Publicacion.crear(UUID.randomUUID(), ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Debe rechazar contenido > 500 caracteres")
    void debeRechazarContenidoLargo() {
        String largo = "a".repeat(501);
        assertThatThrownBy(() -> Publicacion.crear(UUID.randomUUID(), largo))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Debe crear domain event PublicacionCreada")
    void debeCrearDomainEvent() {
        Publicacion p = Publicacion.crear(UUID.randomUUID(), "Test");
        PublicacionCreada evento = PublicacionCreada.desde(p);
        assertThat(evento.publicacionId()).isEqualTo(p.id());
        assertThat(evento.contenido()).isEqualTo("Test");
    }
}
