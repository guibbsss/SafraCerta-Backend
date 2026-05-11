package com.safracerta.modules.transacaofinanceira;

import com.safracerta.modules.fazenda.Fazenda;
import com.safracerta.modules.fazenda.FazendaRepository;
import com.safracerta.modules.fazenda.FazendaUsuarioEscopoService;
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
  private final FazendaUsuarioEscopoService fazendaUsuarioEscopoService;

  public TransacaoFinanceiraService(
      TransacaoFinanceiraRepository transacaoRepository,
      FazendaRepository fazendaRepository,
      SafraRepository safraRepository,
      UsuarioRepository usuarioRepository,
      FazendaUsuarioEscopoService fazendaUsuarioEscopoService) {
    this.transacaoRepository = transacaoRepository;
    this.fazendaRepository = fazendaRepository;
    this.safraRepository = safraRepository;
    this.usuarioRepository = usuarioRepository;
    this.fazendaUsuarioEscopoService = fazendaUsuarioEscopoService;
  }

  @Transactional(readOnly = true)
  public List<TransacaoFinanceiraResponseDto> listar(
      Long usuarioId,
      Long fazendaId,
      TipoTransacaoFinanceira tipo,
      StatusTransacaoFinanceira status,
      LocalDate dataInicio,
      LocalDate dataFim) {
    return lancamentosEscopados(usuarioId, fazendaId, tipo, status, dataInicio, dataFim).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public TransacaoFinanceiraResponseDto buscar(Long id, Long usuarioId) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      throw notFound();
    }
    return transacaoRepository
        .findByIdAndExcluidoFalseAndFazenda_IdIn(id, fazendaIds)
        .map(this::toResponse)
        .orElseThrow(this::notFound);
  }

  @Transactional(readOnly = true)
  public TransacaoFinanceiraResumoDto resumo(
      Long usuarioId,
      Long fazendaId,
      TipoTransacaoFinanceira tipo,
      StatusTransacaoFinanceira status,
      LocalDate dataInicio,
      LocalDate dataFim) {
    List<TransacaoFinanceira> lancamentos =
        lancamentosEscopados(usuarioId, fazendaId, tipo, status, dataInicio, dataFim);

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
  public TransacaoFinanceiraResponseDto criar(Long usuarioId, TransacaoFinanceiraRequestDto dto) {
    Fazenda fazenda = resolveFazenda(dto.fazendaId());
    fazendaUsuarioEscopoService.garantirEscritaNaFazenda(usuarioId, fazenda.getId());
    TransacaoFinanceira t = new TransacaoFinanceira();
    t.setFazenda(fazenda);
    t.setSafra(resolveSafra(dto.safraId(), fazenda.getId()));
    apply(t, dto);
    return toResponse(transacaoRepository.save(t));
  }

  @Transactional
  public TransacaoFinanceiraResponseDto atualizar(
      Long usuarioId, Long id, TransacaoFinanceiraRequestDto dto) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      throw notFound();
    }
    TransacaoFinanceira t =
        transacaoRepository
            .findByIdAndExcluidoFalseAndFazenda_IdIn(id, fazendaIds)
            .orElseThrow(this::notFound);
    Fazenda fazenda = resolveFazenda(dto.fazendaId());
    fazendaUsuarioEscopoService.garantirEscritaNaFazenda(usuarioId, fazenda.getId());
    t.setFazenda(fazenda);
    t.setSafra(resolveSafra(dto.safraId(), fazenda.getId()));
    apply(t, dto);
    return toResponse(transacaoRepository.save(t));
  }

  @Transactional
  public void excluir(Long id, TransacaoFinanceiraExclusaoRequestDto dto, Long usuarioId) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      throw notFound();
    }
    TransacaoFinanceira t =
        transacaoRepository
            .findByIdAndExcluidoFalseAndFazenda_IdIn(id, fazendaIds)
            .orElseThrow(this::notFound);
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

  private List<TransacaoFinanceira> lancamentosEscopados(
      Long usuarioId,
      Long fazendaId,
      TipoTransacaoFinanceira tipo,
      StatusTransacaoFinanceira status,
      LocalDate dataInicio,
      LocalDate dataFim) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      return List.of();
    }
    if (fazendaId != null && !fazendaIds.contains(fazendaId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem acesso a esta fazenda");
    }
    return transacaoRepository.filtrarPorFazendas(
        fazendaIds, fazendaId, tipo, status, dataInicio, dataFim);
  }

  private Fazenda resolveFazenda(Long fazendaId) {
    if (fazendaId == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fazenda é obrigatória");
    }
    return fazendaRepository.findById(fazendaId).orElseThrow(this::fazendaNotFound);
  }

  private Safra resolveSafra(Long safraId, Long fazendaIdEsperada) {
    if (safraId == null) {
      return null;
    }
    Safra safra =
        safraRepository.findByIdAndExcluidoFalse(safraId).orElseThrow(this::safraNotFound);
    Long fazendaDaSafra = safra.getTalhao().getFazenda().getId();
    if (!fazendaDaSafra.equals(fazendaIdEsperada)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Safra não pertence à fazenda indicada");
    }
    return safra;
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
