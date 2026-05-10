package com.safracerta.modules.movimentacaoestoque;

import com.safracerta.modules.insumo.Insumo;
import com.safracerta.modules.insumo.InsumoRepository;
import com.safracerta.modules.movimentacaoestoque.dto.MovimentacaoEstoqueRequestDto;
import com.safracerta.modules.safra.Safra;
import com.safracerta.modules.movimentacaoestoque.dto.MovimentacaoEstoqueResponseDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MovimentacaoEstoqueService {

  private final MovimentacaoEstoqueRepository movimentacaoRepository;
  private final InsumoRepository insumoRepository;

  public MovimentacaoEstoqueService(
      MovimentacaoEstoqueRepository movimentacaoRepository,
      InsumoRepository insumoRepository) {
    this.movimentacaoRepository = movimentacaoRepository;
    this.insumoRepository = insumoRepository;
  }

  @Transactional(readOnly = true)
  public List<MovimentacaoEstoqueResponseDto> listar() {
    return movimentacaoRepository.findAllWithDetails().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public List<MovimentacaoEstoqueResponseDto> listarPorTipo(TipoMovimentacaoEstoque tipo) {
    return movimentacaoRepository.findByTipoWithDetails(tipo).stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public MovimentacaoEstoqueResponseDto buscar(Long id) {
    return movimentacaoRepository
        .findByIdWithDetails(id)
        .map(this::toResponse)
        .orElseThrow(this::notFound);
  }

  /**
   * Regista uma saída de estoque ligada a uma safra (persistida). Valida se o insumo pertence à
   * fazenda indicada.
   */
  @Transactional
  public void registrarSaidaParaSafra(
      Long fazendaIdEsperada,
      Long insumoId,
      BigDecimal quantidade,
      Safra safra,
      LocalDateTime dataMovimentacao) {
    Insumo insumo = insumoRepository.findById(insumoId).orElseThrow(this::insumoNotFound);
    if (!insumo.getFazenda().getId().equals(fazendaIdEsperada)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Insumo não pertence à fazenda do talhão selecionado");
    }
    aplicarNoEstoque(insumo, TipoMovimentacaoEstoque.SAIDA, quantidade);
    insumoRepository.save(insumo);
    MovimentacaoEstoque m = new MovimentacaoEstoque();
    m.setInsumo(insumo);
    m.setSafra(safra);
    m.setTipoMovimentacao(TipoMovimentacaoEstoque.SAIDA);
    m.setQuantidade(quantidade);
    m.setDataMovimentacao(dataMovimentacao);
    m.setObservacao("Consumo na safra #" + safra.getId());
    if (insumo.getValorUnitarioReferencia() != null) {
      m.setValorUnitario(insumo.getValorUnitarioReferencia());
    }
    movimentacaoRepository.save(m);
  }

  @Transactional
  public MovimentacaoEstoqueResponseDto criar(MovimentacaoEstoqueRequestDto dto) {
    Insumo insumo =
        insumoRepository.findById(dto.insumoId()).orElseThrow(this::insumoNotFound);
    validarFazendaSeInformada(dto, insumo);
    aplicarNoEstoque(insumo, dto.tipoMovimentacao(), dto.quantidade());
    atualizarReferenciaPrecoSeEntrada(insumo, dto);
    insumoRepository.save(insumo);
    MovimentacaoEstoque m = new MovimentacaoEstoque();
    m.setInsumo(insumo);
    apply(m, dto);
    return toResponse(movimentacaoRepository.save(m));
  }

  @Transactional
  public MovimentacaoEstoqueResponseDto atualizar(Long id, MovimentacaoEstoqueRequestDto dto) {
    MovimentacaoEstoque m = movimentacaoRepository.findById(id).orElseThrow(this::notFound);
    Insumo insumoAnterior = m.getInsumo();
    reverterNoEstoque(insumoAnterior, m.getTipoMovimentacao(), m.getQuantidade());
    insumoRepository.save(insumoAnterior);

    Insumo insumoNovo =
        insumoRepository.findById(dto.insumoId()).orElseThrow(this::insumoNotFound);
    validarFazendaSeInformada(dto, insumoNovo);
    aplicarNoEstoque(insumoNovo, dto.tipoMovimentacao(), dto.quantidade());
    atualizarReferenciaPrecoSeEntrada(insumoNovo, dto);
    insumoRepository.save(insumoNovo);

    m.setInsumo(insumoNovo);
    apply(m, dto);
    MovimentacaoEstoque salvo = movimentacaoRepository.save(m);
    return movimentacaoRepository
        .findByIdWithDetails(salvo.getId())
        .map(this::toResponse)
        .orElseThrow(this::notFound);
  }

  @Transactional
  public void excluir(Long id) {
    MovimentacaoEstoque m = movimentacaoRepository.findById(id).orElseThrow(this::notFound);
    Insumo insumo = m.getInsumo();
    reverterNoEstoque(insumo, m.getTipoMovimentacao(), m.getQuantidade());
    insumoRepository.save(insumo);
    movimentacaoRepository.delete(m);
  }

  private void validarFazendaSeInformada(MovimentacaoEstoqueRequestDto dto, Insumo insumo) {
    if (dto.fazendaId() != null
        && !dto.fazendaId().equals(insumo.getFazenda().getId())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Insumo não pertence à fazenda selecionada");
    }
  }

  private void atualizarReferenciaPrecoSeEntrada(Insumo insumo, MovimentacaoEstoqueRequestDto dto) {
    if (dto.tipoMovimentacao() == TipoMovimentacaoEstoque.ENTRADA
        && dto.valorUnitario() != null) {
      insumo.setValorUnitarioReferencia(dto.valorUnitario());
    }
  }

  private void apply(MovimentacaoEstoque m, MovimentacaoEstoqueRequestDto dto) {
    m.setTipoMovimentacao(dto.tipoMovimentacao());
    m.setQuantidade(dto.quantidade());
    m.setDataMovimentacao(dto.dataMovimentacao());
    m.setObservacao(dto.observacao());
    m.setValorUnitario(dto.valorUnitario());
    m.setFornecedor(dto.fornecedor());
  }

  private void aplicarNoEstoque(
      Insumo insumo, TipoMovimentacaoEstoque tipo, BigDecimal quantidade) {
    BigDecimal atual = insumo.getQuantidadeAtual();
    BigDecimal novo =
        switch (tipo) {
          case ENTRADA -> atual.add(quantidade);
          case SAIDA -> atual.subtract(quantidade);
        };
    if (novo.compareTo(BigDecimal.ZERO) < 0) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Quantidade insuficiente em estoque para esta saída");
    }
    insumo.setQuantidadeAtual(novo);
  }

  private void reverterNoEstoque(
      Insumo insumo, TipoMovimentacaoEstoque tipo, BigDecimal quantidade) {
    TipoMovimentacaoEstoque oposto =
        tipo == TipoMovimentacaoEstoque.ENTRADA
            ? TipoMovimentacaoEstoque.SAIDA
            : TipoMovimentacaoEstoque.ENTRADA;
    aplicarNoEstoque(insumo, oposto, quantidade);
  }

  private MovimentacaoEstoqueResponseDto toResponse(MovimentacaoEstoque m) {
    Insumo ins = m.getInsumo();
    BigDecimal valorTotal =
        m.getValorUnitario() != null
            ? m.getQuantidade().multiply(m.getValorUnitario())
            : null;
    return new MovimentacaoEstoqueResponseDto(
        m.getId(),
        ins.getId(),
        ins.getNome(),
        ins.getFazenda().getId(),
        ins.getFazenda().getNome(),
        ins.getCategoria(),
        m.getTipoMovimentacao(),
        m.getQuantidade(),
        m.getValorUnitario(),
        valorTotal,
        m.getDataMovimentacao(),
        m.getObservacao(),
        m.getFornecedor());
  }

  private ResponseStatusException notFound() {
    return new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Movimentação de estoque não encontrada");
  }

  private ResponseStatusException insumoNotFound() {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insumo inválido");
  }
}
