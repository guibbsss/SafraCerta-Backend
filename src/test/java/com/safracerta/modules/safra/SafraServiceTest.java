package com.safracerta.modules.safra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safracerta.modules.movimentacaoestoque.MovimentacaoEstoqueRepository;
import com.safracerta.modules.movimentacaoestoque.MovimentacaoEstoqueService;
import com.safracerta.modules.safra.dto.SafraExclusaoRequestDto;
import com.safracerta.modules.safra.dto.SafraRequestDto;
import com.safracerta.modules.safra.dto.SafraResponseDto;
import com.safracerta.modules.talhao.Talhao;
import com.safracerta.modules.talhao.TalhaoRepository;
import com.safracerta.modules.user.Usuario;
import com.safracerta.modules.user.UsuarioRepository;
import java.time.LocalDate;
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

/**
 * Testes unitários para {@link SafraService}.
 *
 * <p>Cobre: listagem ignora registros excluídos, criação sem talhão lança 400, soft-delete grava
 * usuário, justificativa e timestamp.
 */
@ExtendWith(MockitoExtension.class)
class SafraServiceTest {

  @Mock private SafraRepository safraRepository;
  @Mock private TalhaoRepository talhaoRepository;
  @Mock private UsuarioRepository usuarioRepository;
  @Mock private MovimentacaoEstoqueService movimentacaoEstoqueService;
  @Mock private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

  private SafraService service;

  @BeforeEach
  void setUp() {
    service =
        new SafraService(
            safraRepository,
            talhaoRepository,
            usuarioRepository,
            movimentacaoEstoqueService,
            movimentacaoEstoqueRepository);
  }

  @Test
  @DisplayName("listar: usa findByExcluidoFalse (ignora soft-deleted)")
  void listar_ignoraExcluidos() {
    Talhao talhao = new Talhao();
    talhao.setId(1L);
    talhao.setNome("Talhão A");
    Safra ativa = novaSafra(10L, talhao);

    when(safraRepository.findByExcluidoFalse()).thenReturn(List.of(ativa));

    List<SafraResponseDto> result = service.listar();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).id()).isEqualTo(10L);
    assertThat(result.get(0).talhaoNome()).isEqualTo("Talhão A");
    verify(safraRepository).findByExcluidoFalse();
  }

  @Test
  @DisplayName("criar: talhaoId nulo lança 400 BAD_REQUEST")
  void criar_semTalhao_lancaBadRequest() {
    SafraRequestDto dto =
        new SafraRequestDto(
            "Safra 2025", null, "Soja", SafraStatus.PLANTADA,
            LocalDate.now(), null, null, null, null, null);

    assertThatThrownBy(() -> service.criar(dto))
        .isInstanceOf(ResponseStatusException.class)
        .extracting(e -> ((ResponseStatusException) e).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);

    verify(safraRepository, never()).save(any());
  }

  @Test
  @DisplayName("criar: talhão inexistente lança 400 BAD_REQUEST")
  void criar_talhaoInexistente_lancaBadRequest() {
    when(talhaoRepository.findById(999L)).thenReturn(Optional.empty());
    SafraRequestDto dto =
        new SafraRequestDto(
            "Safra 2025", 999L, "Soja", SafraStatus.PLANTADA,
            LocalDate.now(), null, null, null, null, null);

    assertThatThrownBy(() -> service.criar(dto))
        .isInstanceOf(ResponseStatusException.class)
        .extracting(e -> ((ResponseStatusException) e).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);

    verify(safraRepository, never()).save(any());
  }

  @Test
  @DisplayName("excluir: soft-delete grava usuário, justificativa, excluido_em e excluido=true")
  void excluir_softDelete_gravaCamposDeAuditoria() {
    Talhao talhao = new Talhao();
    talhao.setId(1L);
    talhao.setNome("Talhão A");
    Safra safra = novaSafra(20L, talhao);
    when(safraRepository.findByIdAndExcluidoFalse(20L)).thenReturn(Optional.of(safra));

    Usuario usuario = new Usuario();
    usuario.setId(3L);
    when(usuarioRepository.findById(3L)).thenReturn(Optional.of(usuario));

    service.excluir(20L, new SafraExclusaoRequestDto("Cultura perdida por geada"), 3L);

    assertThat(safra.isExcluido()).isTrue();
    assertThat(safra.getExcluidoPor()).isSameAs(usuario);
    assertThat(safra.getJustificativaExclusao()).isEqualTo("Cultura perdida por geada");
    assertThat(safra.getExcluidoEm()).isNotNull();
    verify(safraRepository).save(safra);
  }

  @Test
  @DisplayName("excluir: safra inexistente lança 404 NOT_FOUND")
  void excluir_safraInexistente_lancaNotFound() {
    when(safraRepository.findByIdAndExcluidoFalse(404L)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> service.excluir(404L, new SafraExclusaoRequestDto("qualquer"), 1L))
        .isInstanceOf(ResponseStatusException.class)
        .extracting(e -> ((ResponseStatusException) e).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  private Safra novaSafra(long id, Talhao talhao) {
    Safra s = new Safra();
    s.setId(id);
    s.setTalhao(talhao);
    s.setNome("Safra " + id);
    s.setCultura("Soja");
    s.setStatus(SafraStatus.PLANTADA);
    s.setDataPlantio(LocalDate.of(2025, 1, 1));
    return s;
  }
}
