package com.peribook.like.application;

import com.peribook.like.domain.EventPublisher;
import com.peribook.like.domain.Like;
import com.peribook.like.domain.LikeRegistrado;
import com.peribook.like.domain.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DarLikeUseCaseTest {

    @Mock private LikeRepository repository;
    @Mock private EventPublisher eventPublisher;
    private DarLikeUseCase useCase;
    private final UUID pubId = UUID.randomUUID(), userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new DarLikeUseCase(repository, eventPublisher);
    }

    @Test
    @DisplayName("Debe registrar like nuevo y publicar evento")
    void debeRegistrarLikeNuevo() {
        when(repository.buscarPorPublicacionYUsuario(pubId, userId)).thenReturn(Optional.empty());
        when(repository.save(any(Like.class))).thenAnswer(inv -> inv.getArgument(0));

        DarLikeUseCase.Resultado r = useCase.ejecutar(pubId, userId);

        assertThat(r.esNuevo()).isTrue();
        verify(eventPublisher).publish(any(LikeRegistrado.class));
    }

    @Test
    @DisplayName("Debe ser idempotente: like duplicado no publica evento")
    void likeDuplicadoNoPublicaEvento() {
        Like existente = Like.dar(pubId, userId);
        when(repository.buscarPorPublicacionYUsuario(pubId, userId)).thenReturn(Optional.of(existente));

        DarLikeUseCase.Resultado r = useCase.ejecutar(pubId, userId);

        assertThat(r.esNuevo()).isFalse();
        assertThat(r.like()).isEqualTo(existente);
        verify(eventPublisher, never()).publish(any());
        verify(repository, never()).save(any());
    }
}
<!-- 2026-07-09 -->
