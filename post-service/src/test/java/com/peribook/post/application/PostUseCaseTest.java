package com.peribook.post.application;

import com.peribook.post.domain.EventPublisher;
import com.peribook.post.domain.Publicacion;
import com.peribook.post.domain.PublicacionCreada;
import com.peribook.post.domain.PublicacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearPublicacionUseCaseTest {

    @Mock private PublicacionRepository repository;
    @Mock private EventPublisher eventPublisher;
    private CrearPublicacionUseCase useCase;
    private final UUID autor = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new CrearPublicacionUseCase(repository, eventPublisher);
    }

    @Test
    @DisplayName("Debe guardar y publicar evento al crear publicación")
    void debeCrearYPublicarEvento() {
        when(repository.save(any(Publicacion.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Publicacion result = useCase.ejecutar(autor, "Hola PeriBook");

        assertThat(result.contenido()).isEqualTo("Hola PeriBook");
        verify(repository).save(any(Publicacion.class));
        verify(eventPublisher).publish(any(PublicacionCreada.class));
    }
}

@ExtendWith(MockitoExtension.class)
class ListarPublicacionesUseCaseTest {

    @Mock private PublicacionRepository repository;
    private ListarPublicacionesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListarPublicacionesUseCase(repository);
    }

    @Test
    @DisplayName("Debe respetar límite máximo de 50")
    void debeLimitarA50() {
        when(repository.listarRecientes(50)).thenReturn(List.of());
        useCase.ejecutar(100);
        verify(repository).listarRecientes(50);
    }
}
