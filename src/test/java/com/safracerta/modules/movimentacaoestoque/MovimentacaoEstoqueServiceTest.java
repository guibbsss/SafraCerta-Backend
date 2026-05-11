package com.safracerta.modules.movimentacaoestoque;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safracerta.modules.fazenda.FazendaUsuarioEscopoService;
import com.safracerta.modules.insumo.InsumoRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovimentacaoEstoqueServiceTest {

  @Mock private MovimentacaoEstoqueRepository movimentacaoRepository;
  @Mock private InsumoRepository insumoRepository;
  @Mock private FazendaUsuarioEscopoService fazendaUsuarioEscopoService;

  private MovimentacaoEstoqueService service;

  @BeforeEach
  void setUp() {
    service =
        new MovimentacaoEstoqueService(
            movimentacaoRepository, insumoRepository, fazendaUsuarioEscopoService);
  }

  @Test
  @DisplayName("listar: utilizador sem fazendas não consulta o repositório")
  void listar_semFazendas_retornaVazioSemQuery() {
    when(fazendaUsuarioEscopoService.idsFazendasAcessiveis(1L)).thenReturn(List.of());

    assertThat(service.listar(1L)).isEmpty();
    verify(movimentacaoRepository, never()).findAllWithDetailsForFazendas(any());
  }
}
