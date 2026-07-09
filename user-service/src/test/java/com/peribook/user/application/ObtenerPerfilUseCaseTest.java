package com.peribook.user.application;

import com.peribook.user.domain.Email;
import com.peribook.user.domain.Perfil;
import com.peribook.user.domain.PerfilRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerPerfilUseCaseTest {

    @Mock private PerfilRepository perfilRepository;
    private ObtenerPerfilUseCase useCase;
    private Perfil perfilPrueba;
    private final UUID perfilId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new ObtenerPerfilUseCase(perfilRepository);
        perfilPrueba = Perfil.crear(UUID.randomUUID(),
                new Email("ana@peribook.com"), "ana_w", "Ana", "García",
                LocalDate.of(1995, 3, 15));
    }

    @Test
    @DisplayName("Debe devolver perfil cuando existe")
    void debeObtenerPerfilExistente() {
        when(perfilRepository.buscarPorId(perfilId)).thenReturn(Optional.of(perfilPrueba));
        Perfil result = useCase.obtener(perfilId);
        assertThat(result).isNotNull();
        assertThat(result.alias()).isEqualTo("ana_w");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando no existe")
    void debeFallarConPerfilInexistente() {
        when(perfilRepository.buscarPorId(perfilId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.obtener(perfilId))
                .isInstanceOf(PerfilNoEncontradoException.class);
    }
}
