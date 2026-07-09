package com.peribook.like.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class LikeTest {

    @Test
    @DisplayName("Debe crear like válido")
    void debeCrearLike() {
        UUID pubId = UUID.randomUUID(), userId = UUID.randomUUID();
        Like like = Like.dar(pubId, userId);
        assertThat(like.id()).isNotNull();
        assertThat(like.publicacionId()).isEqualTo(pubId);
        assertThat(like.usuarioId()).isEqualTo(userId);
        assertThat(like.creadoEn()).isNotNull();
    }

    @Test
    @DisplayName("Debe crear domain event LikeRegistrado")
    void debeCrearDomainEvent() {
        Like like = Like.dar(UUID.randomUUID(), UUID.randomUUID());
        LikeRegistrado evento = LikeRegistrado.desde(like);
        assertThat(evento.likeId()).isEqualTo(like.id());
        assertThat(evento.publicacionId()).isEqualTo(like.publicacionId());
    }
}
