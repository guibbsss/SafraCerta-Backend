package com.safracerta.modules.movimentacaoestoque;

import com.safracerta.modules.insumo.Insumo;
import com.safracerta.modules.insumo.InsumoRepository;
import com.safracerta.modules.movimentacaoestoque.dto.MovimentacaoEstoqueRequestDto;
import com.safracerta.modules.movimentacaoestoque.dto.MovimentacaoEstoqueResponseDto;
import java.math.BigDecimal;
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
    return movimentacaoRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public MovimentacaoEstoqueResponseDto buscar(Long id) {
    return movimentacaoRepository.findById(id).map(this::toResponse).orElseThrow(this::notFound);
  }

  @Transactional
  public MovimentacaoEstoqueResponseDto criar(MovimentacaoEstoqueRequestDto dto) {
    Insumo insumo =
        insumoRepository.findById(dto.insumoId()).orElseThrow(this::insumoNotFound);
    aplicarNoEstoque(insumo, dto.tipoMovimentacao(), dto.quantidade());
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
    aplicarNoEstoque(insumoNovo, dto.tipoMovimentacao(), dto.quantidade());
    insumoRepository.save(insumoNovo);

    m.setInsumo(insumoNovo);
    apply(m, dto);
    return toResponse(movimentacaoRepository.save(m));
  }

  @Transactional
  public void excluir(Long id) {
    MovimentacaoEstoque m = movimentacaoRepository.findById(id).orElseThrow(this::notFound);
    Insumo insumo = m.getInsumo();
    reverterNoEstoque(insumo, m.getTipoMovimentacao(), m.getQuantidade());
    insumoRepository.save(insumo);
    movimentacaoRepository.delete(m);
  }

  private void apply(MovimentacaoEstoque m, MovimentacaoEstoqueRequestDto dto) {
    m.setTipoMovimentacao(dto.tipoMovimentacao());
    m.setQuantidade(dto.quantidade());
    m.setDataMovimentacao(dto.dataMovimentacao());
    m.setObservacao(dto.observacao());
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
    return new MovimentacaoEstoqueResponseDto(
        m.getId(),
        m.getInsumo().getId(),
        m.getTipoMovimentacao(),
        m.getQuantidade(),
        m.getDataMovimentacao(),
        m.getObservacao());
  }

  private ResponseStatusException notFound() {
    return new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Movimentação de estoque não encontrada");
  }

  private ResponseStatusException insumoNotFound() {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insumo inválido");
  }
}
