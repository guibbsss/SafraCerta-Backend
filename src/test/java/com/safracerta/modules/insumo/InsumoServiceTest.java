package com.safracerta.modules.insumo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safracerta.modules.fazenda.FazendaRepository;
import com.safracerta.modules.fazenda.FazendaUsuarioEscopoService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InsumoServiceTest {

  private static final long USUARIO_ID = 1L;

  @Mock private InsumoRepository insumoRepository;
  @Mock private FazendaRepository fazendaRepository;
  @Mock private FazendaUsuarioEscopoService fazendaUsuarioEscopoService;

  private InsumoService service;

  @BeforeEach
  void setUp() {
    service =
        new InsumoService(insumoRepository, fazendaRepository, fazendaUsuarioEscopoService);
  }

  @Test
  @DisplayName("listar: utilizador sem fazendas não consulta insumos por IN")
  void listar_semFazendas_retornaVazioSemQuery() {
    when(fazendaUsuarioEscopoService.idsFazendasAcessiveis(USUARIO_ID)).thenReturn(List.of());

    assertThat(service.listar(USUARIO_ID)).isEmpty();
    verify(insumoRepository, never()).findByFazenda_IdInOrderByNomeAsc(any());
  }

  @Test
  @DisplayName("listar: repassa IDs ao repositório")
  void listar_comEscopo_chamaFindByFazendaIdIn() {
    when(fazendaUsuarioEscopoService.idsFazendasAcessiveis(USUARIO_ID)).thenReturn(List.of(10L, 20L));
    when(insumoRepository.findByFazenda_IdInOrderByNomeAsc(List.of(10L, 20L))).thenReturn(List.of());

    assertThat(service.listar(USUARIO_ID)).isEmpty();
    verify(insumoRepository).findByFazenda_IdInOrderByNomeAsc(List.of(10L, 20L));
  }
}
