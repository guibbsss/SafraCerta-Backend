package com.safracerta.modules.transacaofinanceira;

import com.safracerta.modules.fazenda.Fazenda;
import com.safracerta.modules.fazenda.FazendaRepository;
import com.safracerta.modules.safra.Safra;
import com.safracerta.modules.safra.SafraRepository;
import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraExclusaoRequestDto;
import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraRequestDto;
import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraResponseDto;
import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraResumoDto;
import com.safracerta.modules.user.Usuario;
import com.safracerta.modules.user.UsuarioRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransacaoFinanceiraService {

  private final TransacaoFinanceiraRepository transacaoRepository;
  private final FazendaRepository fazendaRepository;
  private final SafraRepository safraRepository;
  private final UsuarioRepository usuarioRepository;

  public TransacaoFinanceiraService(
      TransacaoFinanceiraRepository transacaoRepository,
      FazendaRepository fazendaRepository,
      SafraRepository safraRepository,
      UsuarioRepository usuarioRepository) {
    this.transacaoRepository = transacaoRepository;
    this.fazendaRepository = fazendaRepository;
    this.safraRepository = safraRepository;
    this.usuarioRepository = usuarioRepository;
  }

  @Transactional(readOnly = true)
  public List<TransacaoFinanceiraResponseDto> listar(
      Long fazendaId,
      TipoTransacaoFinanceira tipo,
      StatusTransacaoFinanceira status,
      LocalDate dataInicio,
      LocalDate dataFim) {
    return transacaoRepository.filtrar(fazendaId, tipo, status, dataInicio, dataFim).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public TransacaoFinanceiraResponseDto buscar(Long id) {
    return transacaoRepository
        .findByIdAndExcluidoFalse(id)
        .map(this::toResponse)
        .orElseThrow(this::notFound);
  }

  @Transactional(readOnly = true)
  public TransacaoFinanceiraResumoDto resumo(
      Long fazendaId,
      TipoTransacaoFinanceira tipo,
      StatusTransacaoFinanceira status,
      LocalDate dataInicio,
      LocalDate dataFim) {
    // Agregamos em memória para evitar gotchas com SUM(CASE WHEN ...) no JPQL/Hibernate.
    // Volume previsto é o conjunto de lançamentos que aparecem na tela financeira da fazenda,
    // então o custo é desprezível e o cálculo fica idêntico ao que o usuário vê na grade.
    List<TransacaoFinanceira> lancamentos =
        transacaoRepository.filtrar(fazendaId, tipo, status, dataInicio, dataFim);

    BigDecimal totalReceitas = BigDecimal.ZERO;
    BigDecimal totalDespesas = BigDecimal.ZERO;
    long qtdReceitas = 0;
    long qtdDespesas = 0;
    long qtdPendentes = 0;
    long qtdAtrasadas = 0;

    for (TransacaoFinanceira t : lancamentos) {
      BigDecimal valor = t.getValor() != null ? t.getValor() : BigDecimal.ZERO;
      if (t.getTipo() == TipoTransacaoFinanceira.RECEITA) {
        totalReceitas = totalReceitas.add(valor);
        qtdReceitas++;
      } else if (t.getTipo() == TipoTransacaoFinanceira.DESPESA) {
        totalDespesas = totalDespesas.add(valor);
        qtdDespesas++;
      }
      if (t.getStatus() == StatusTransacaoFinanceira.PENDENTE) qtdPendentes++;
      if (t.getStatus() == StatusTransacaoFinanceira.ATRASADO) qtdAtrasadas++;
    }

    return new TransacaoFinanceiraResumoDto(
        totalReceitas,
        totalDespesas,
        totalReceitas.subtract(totalDespesas),
        qtdReceitas,
        qtdDespesas,
        qtdPendentes,
        qtdAtrasadas);
  }

  @Transactional
  public TransacaoFinanceiraResponseDto criar(TransacaoFinanceiraRequestDto dto) {
    TransacaoFinanceira t = new TransacaoFinanceira();
    t.setFazenda(resolveFazenda(dto.fazendaId()));
    t.setSafra(resolveSafra(dto.safraId()));
    apply(t, dto);
    return toResponse(transacaoRepository.save(t));
  }

  @Transactional
  public TransacaoFinanceiraResponseDto atualizar(Long id, TransacaoFinanceiraRequestDto dto) {
    TransacaoFinanceira t =
        transacaoRepository.findByIdAndExcluidoFalse(id).orElseThrow(this::notFound);
    t.setFazenda(resolveFazenda(dto.fazendaId()));
    t.setSafra(resolveSafra(dto.safraId()));
    apply(t, dto);
    return toResponse(transacaoRepository.save(t));
  }

  @Transactional
  public void excluir(Long id, TransacaoFinanceiraExclusaoRequestDto dto, Long usuarioId) {
    TransacaoFinanceira t =
        transacaoRepository.findByIdAndExcluidoFalse(id).orElseThrow(this::notFound);
    Usuario u =
        usuarioRepository
            .findById(usuarioId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário inválido"));
    t.setExcluido(true);
    t.setExcluidoPor(u);
    t.setJustificativaExclusao(dto.justificativa().trim());
    t.setExcluidoEm(LocalDateTime.now());
    transacaoRepository.save(t);
  }

  private Fazenda resolveFazenda(Long fazendaId) {
    if (fazendaId == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fazenda é obrigatória");
    }
    return fazendaRepository.findById(fazendaId).orElseThrow(this::fazendaNotFound);
  }

  private Safra resolveSafra(Long safraId) {
    if (safraId == null) {
      return null;
    }
    return safraRepository.findByIdAndExcluidoFalse(safraId).orElseThrow(this::safraNotFound);
  }

  private void apply(TransacaoFinanceira t, TransacaoFinanceiraRequestDto dto) {
    t.setTipo(dto.tipo());
    t.setValor(dto.valor());
    t.setDescricao(trimOrNull(dto.descricao()));
    t.setFormaPagamento(trimOrNull(dto.formaPagamento()));
    t.setStatus(dto.status());
    t.setDataTransacao(dto.dataTransacao());
    t.setDataVencimento(dto.dataVencimento());
    t.setDataPagamento(dto.dataPagamento());
    t.setCategoria(trimOrNull(dto.categoria()));
    t.setOrigem(trimOrNull(dto.origem()));
    t.setObservacoes(trimOrNull(dto.observacoes()));
  }

  private String trimOrNull(String value) {
    if (value == null) return null;
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  private TransacaoFinanceiraResponseDto toResponse(TransacaoFinanceira t) {
    Fazenda f = t.getFazenda();
    Safra s = t.getSafra();
    return new TransacaoFinanceiraResponseDto(
        t.getId(),
        f != null ? f.getId() : null,
        f != null ? f.getNome() : null,
        s != null ? s.getId() : null,
        s != null ? s.getNome() : null,
        t.getTipo(),
        t.getValor(),
        t.getDescricao(),
        t.getFormaPagamento(),
        t.getStatus(),
        t.getDataTransacao(),
        t.getDataVencimento(),
        t.getDataPagamento(),
        t.getCategoria(),
        t.getOrigem(),
        t.getObservacoes());
  }

  private ResponseStatusException notFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação não encontrada");
  }

  private ResponseStatusException fazendaNotFound() {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fazenda inválida");
  }

  private ResponseStatusException safraNotFound() {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Safra inválida");
  }
}
