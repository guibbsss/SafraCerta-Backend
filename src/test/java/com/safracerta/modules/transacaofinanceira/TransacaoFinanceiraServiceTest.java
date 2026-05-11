package com.safracerta.modules.transacaofinanceira;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safracerta.modules.fazenda.Fazenda;
import com.safracerta.modules.fazenda.FazendaRepository;
import com.safracerta.modules.fazenda.FazendaUsuarioEscopoService;
import com.safracerta.modules.safra.SafraRepository;
import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraExclusaoRequestDto;
import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraRequestDto;
import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraResponseDto;
import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraResumoDto;
import com.safracerta.modules.user.Usuario;
import com.safracerta.modules.user.UsuarioRepository;
import java.math.BigDecimal;
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
 * Testes unitários para {@link TransacaoFinanceiraService}.
 *
 * <p>Cobre: agregação correta do resumo financeiro (receitas, despesas, saldo, contagens por
 * status), repasse dos filtros ao repository com escopo por fazenda, validações de fazenda e
 * soft-delete.
 */
@ExtendWith(MockitoExtension.class)
class TransacaoFinanceiraServiceTest {

  private static final long USUARIO_ID = 1L;

  @Mock private TransacaoFinanceiraRepository transacaoRepository;
  @Mock private FazendaRepository fazendaRepository;
  @Mock private SafraRepository safraRepository;
  @Mock private UsuarioRepository usuarioRepository;
  @Mock private FazendaUsuarioEscopoService fazendaUsuarioEscopoService;

  private TransacaoFinanceiraService service;

  @BeforeEach
  void setUp() {
    service =
        new TransacaoFinanceiraService(
            transacaoRepository,
            fazendaRepository,
            safraRepository,
            usuarioRepository,
            fazendaUsuarioEscopoService);
  }

  @Test
  @DisplayName("resumo: soma receitas e despesas e calcula saldo corretamente")
  void resumo_calculaTotaisESaldo() {
    when(fazendaUsuarioEscopoService.idsFazendasAcessiveis(USUARIO_ID)).thenReturn(List.of(1L));
    when(transacaoRepository.filtrarPorFazendas(
            eq(List.of(1L)), isNull(), isNull(), isNull(), isNull(), isNull()))
        .thenReturn(
            List.of(
                lancamento(TipoTransacaoFinanceira.RECEITA, "100.00", StatusTransacaoFinanceira.PAGO),
                lancamento(TipoTransacaoFinanceira.RECEITA, "250.00", StatusTransacaoFinanceira.PAGO),
                lancamento(TipoTransacaoFinanceira.DESPESA, "75.50", StatusTransacaoFinanceira.PAGO)));

    TransacaoFinanceiraResumoDto resumo =
        service.resumo(USUARIO_ID, null, null, null, null, null);

    assertThat(resumo.totalReceitas()).isEqualByComparingTo("350.00");
    assertThat(resumo.totalDespesas()).isEqualByComparingTo("75.50");
    assertThat(resumo.saldo()).isEqualByComparingTo("274.50");
    assertThat(resumo.quantidadeReceitas()).isEqualTo(2);
    assertThat(resumo.quantidadeDespesas()).isEqualTo(1);
  }

  @Test
  @DisplayName("resumo: conta pendentes e atrasadas")
  void resumo_contaPendentesEAtrasadas() {
    when(fazendaUsuarioEscopoService.idsFazendasAcessiveis(USUARIO_ID)).thenReturn(List.of(1L));
    when(transacaoRepository.filtrarPorFazendas(
            eq(List.of(1L)), isNull(), isNull(), isNull(), isNull(), isNull()))
        .thenReturn(
            List.of(
                lancamento(TipoTransacaoFinanceira.RECEITA, "100", StatusTransacaoFinanceira.PENDENTE),
                lancamento(TipoTransacaoFinanceira.DESPESA, "50", StatusTransacaoFinanceira.ATRASADO),
                lancamento(TipoTransacaoFinanceira.DESPESA, "20", StatusTransacaoFinanceira.ATRASADO),
                lancamento(TipoTransacaoFinanceira.DESPESA, "10", StatusTransacaoFinanceira.PAGO)));

    TransacaoFinanceiraResumoDto resumo =
        service.resumo(USUARIO_ID, null, null, null, null, null);

    assertThat(resumo.quantidadePendentes()).isEqualTo(1);
    assertThat(resumo.quantidadeAtrasadas()).isEqualTo(2);
  }

  @Test
  @DisplayName("listar: repassa filtros ao repository e mapeia para DTO")
  void listar_repassaFiltrosERetornaDto() {
    LocalDate inicio = LocalDate.of(2025, 1, 1);
    LocalDate fim = LocalDate.of(2025, 12, 31);
    when(fazendaUsuarioEscopoService.idsFazendasAcessiveis(USUARIO_ID)).thenReturn(List.of(7L));
    when(transacaoRepository.filtrarPorFazendas(
            eq(List.of(7L)),
            eq(7L),
            eq(TipoTransacaoFinanceira.DESPESA),
            eq(StatusTransacaoFinanceira.PAGO),
            eq(inicio),
            eq(fim)))
        .thenReturn(
            List.of(lancamento(TipoTransacaoFinanceira.DESPESA, "30", StatusTransacaoFinanceira.PAGO)));

    List<TransacaoFinanceiraResponseDto> result =
        service.listar(
            USUARIO_ID,
            7L,
            TipoTransacaoFinanceira.DESPESA,
            StatusTransacaoFinanceira.PAGO,
            inicio,
            fim);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).tipo()).isEqualTo(TipoTransacaoFinanceira.DESPESA);
    verify(transacaoRepository)
        .filtrarPorFazendas(
            eq(List.of(7L)),
            eq(7L),
            eq(TipoTransacaoFinanceira.DESPESA),
            eq(StatusTransacaoFinanceira.PAGO),
            eq(inicio),
            eq(fim));
  }

  @Test
  @DisplayName("listar/resumo: utilizador sem fazendas não consulta o repositório")
  void escopo_semFazendas_retornaVazioSemQuery() {
    when(fazendaUsuarioEscopoService.idsFazendasAcessiveis(USUARIO_ID)).thenReturn(List.of());

    assertThat(service.listar(USUARIO_ID, null, null, null, null, null)).isEmpty();
    TransacaoFinanceiraResumoDto resumo =
        service.resumo(USUARIO_ID, null, null, null, null, null);
    assertThat(resumo.totalReceitas()).isEqualByComparingTo(BigDecimal.ZERO);
    verify(transacaoRepository, never()).filtrarPorFazendas(any(), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("listar: fazendaId na query não pertencente ao utilizador → 403")
  void listar_fazendaForaDoEscopo_lancaForbidden() {
    when(fazendaUsuarioEscopoService.idsFazendasAcessiveis(USUARIO_ID)).thenReturn(List.of(1L));

    assertThatThrownBy(
            () ->
                service.listar(
                    USUARIO_ID,
                    99L,
                    null,
                    null,
                    null,
                    null))
        .isInstanceOf(ResponseStatusException.class)
        .extracting(e -> ((ResponseStatusException) e).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);

    verify(transacaoRepository, never()).filtrarPorFazendas(any(), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("criar: fazendaId nulo lança 400 BAD_REQUEST")
  void criar_semFazenda_lancaBadRequest() {
    var dto =
        new TransacaoFinanceiraRequestDto(
            null,
            null,
            TipoTransacaoFinanceira.RECEITA,
            new BigDecimal("100"),
            "Venda de soja",
            "PIX",
            StatusTransacaoFinanceira.PAGO,
            LocalDate.now(),
            null,
            null,
            "Vendas",
            "Cliente A",
            null);

    assertThatThrownBy(() -> service.criar(USUARIO_ID, dto))
        .isInstanceOf(ResponseStatusException.class)
        .extracting(e -> ((ResponseStatusException) e).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);

    verify(transacaoRepository, never()).save(any());
  }

  @Test
  @DisplayName("excluir: soft-delete grava justificativa, usuário, timestamp e excluido=true")
  void excluir_softDelete_gravaAuditoria() {
    TransacaoFinanceira t = lancamento(TipoTransacaoFinanceira.RECEITA, "100", StatusTransacaoFinanceira.PAGO);
    t.setId(99L);
    when(fazendaUsuarioEscopoService.idsFazendasAcessiveis(USUARIO_ID)).thenReturn(List.of(1L));
    when(transacaoRepository.findByIdAndExcluidoFalseAndFazenda_IdIn(99L, List.of(1L)))
        .thenReturn(Optional.of(t));

    Usuario user = new Usuario();
    user.setId(1L);
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user));

    service.excluir(99L, new TransacaoFinanceiraExclusaoRequestDto("Lançamento duplicado"), USUARIO_ID);

    assertThat(t.isExcluido()).isTrue();
    assertThat(t.getExcluidoPor()).isSameAs(user);
    assertThat(t.getJustificativaExclusao()).isEqualTo("Lançamento duplicado");
    assertThat(t.getExcluidoEm()).isNotNull();
    verify(transacaoRepository).save(t);
  }

  private TransacaoFinanceira lancamento(
      TipoTransacaoFinanceira tipo, String valor, StatusTransacaoFinanceira status) {
    Fazenda f = new Fazenda();
    f.setId(1L);
    f.setNome("Fazenda Teste");

    TransacaoFinanceira t = new TransacaoFinanceira();
    t.setFazenda(f);
    t.setTipo(tipo);
    t.setValor(new BigDecimal(valor));
    t.setStatus(status);
    t.setDataTransacao(LocalDate.of(2025, 6, 1));
    return t;
  }
}
