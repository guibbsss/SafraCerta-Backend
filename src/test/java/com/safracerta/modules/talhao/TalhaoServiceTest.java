package com.safracerta.modules.talhao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safracerta.modules.fazenda.Fazenda;
import com.safracerta.modules.fazenda.FazendaRepository;
import com.safracerta.modules.fazenda.FazendaUsuarioEscopoService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class TalhaoServiceTest {

  @Mock private TalhaoRepository talhaoRepository;
  @Mock private FazendaRepository fazendaRepository;
  @Mock private FazendaUsuarioEscopoService fazendaUsuarioEscopoService;

  private TalhaoService service;

  @BeforeEach
  void setUp() {
    service =
        new TalhaoService(talhaoRepository, fazendaRepository, fazendaUsuarioEscopoService);
  }

  @Test
  @DisplayName("listar: utilizador sem fazendas devolve lista vazia")
  void listar_semFazendas_retornaVazio() {
    when(fazendaUsuarioEscopoService.idsFazendasAcessiveis(1L)).thenReturn(List.of());

    assertThat(service.listar(1L)).isEmpty();
    verify(talhaoRepository, never()).findByFazenda_IdInOrderByNomeAsc(any());
  }

  @Test
  @DisplayName("buscar: talhão de outra fazenda → 404")
  void buscar_foraDoEscopo_lancaNotFound() {
    Fazenda fazenda = new Fazenda();
    fazenda.setId(200L);
    Talhao talhao = new Talhao();
    talhao.setId(1L);
    talhao.setFazenda(fazenda);

    when(talhaoRepository.findById(1L)).thenReturn(Optional.of(talhao));
    when(fazendaUsuarioEscopoService.usuarioPodeAcessarFazenda(1L, 200L)).thenReturn(false);

    assertThatThrownBy(() -> service.buscar(1L, 1L))
        .isInstanceOf(ResponseStatusException.class)
        .extracting(e -> ((ResponseStatusException) e).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("listarPorFazenda: sem vínculo à fazenda → 403")
  void listarPorFazenda_semVinculo_lancaForbidden() {
    org.mockito.Mockito.doThrow(
            new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem acesso a esta fazenda"))
        .when(fazendaUsuarioEscopoService)
        .garantirEscritaNaFazenda(anyLong(), anyLong());

    assertThatThrownBy(() -> service.listarPorFazenda(1L, 99L))
        .isInstanceOf(ResponseStatusException.class)
        .extracting(e -> ((ResponseStatusException) e).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }
}
