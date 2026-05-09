package com.safracerta.modules.transacaofinanceira;

import com.safracerta.modules.fazenda.Fazenda;
import com.safracerta.modules.fazenda.FazendaRepository;
import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraRequestDto;
import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraResponseDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransacaoFinanceiraService {

  private final TransacaoFinanceiraRepository transacaoRepository;
  private final FazendaRepository fazendaRepository;

  public TransacaoFinanceiraService(
      TransacaoFinanceiraRepository transacaoRepository,
      FazendaRepository fazendaRepository) {
    this.transacaoRepository = transacaoRepository;
    this.fazendaRepository = fazendaRepository;
  }

  @Transactional(readOnly = true)
  public List<TransacaoFinanceiraResponseDto> listar() {
    return transacaoRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public TransacaoFinanceiraResponseDto buscar(Long id) {
    return transacaoRepository.findById(id).map(this::toResponse).orElseThrow(this::notFound);
  }

  @Transactional
  public TransacaoFinanceiraResponseDto criar(TransacaoFinanceiraRequestDto dto) {
    Fazenda fazenda =
        fazendaRepository.findById(dto.fazendaId()).orElseThrow(this::fazendaNotFound);
    TransacaoFinanceira t = new TransacaoFinanceira();
    t.setFazenda(fazenda);
    apply(t, dto);
    return toResponse(transacaoRepository.save(t));
  }

  @Transactional
  public TransacaoFinanceiraResponseDto atualizar(Long id, TransacaoFinanceiraRequestDto dto) {
    TransacaoFinanceira t = transacaoRepository.findById(id).orElseThrow(this::notFound);
    Fazenda fazenda =
        fazendaRepository.findById(dto.fazendaId()).orElseThrow(this::fazendaNotFound);
    t.setFazenda(fazenda);
    apply(t, dto);
    return toResponse(transacaoRepository.save(t));
  }

  @Transactional
  public void excluir(Long id) {
    if (!transacaoRepository.existsById(id)) {
      throw notFound();
    }
    transacaoRepository.deleteById(id);
  }

  private void apply(TransacaoFinanceira t, TransacaoFinanceiraRequestDto dto) {
    t.setTipo(dto.tipo());
    t.setValor(dto.valor());
    t.setDataTransacao(dto.dataTransacao());
    t.setCategoria(dto.categoria());
    t.setOrigem(dto.origem());
  }

  private TransacaoFinanceiraResponseDto toResponse(TransacaoFinanceira t) {
    return new TransacaoFinanceiraResponseDto(
        t.getId(),
        t.getFazenda().getId(),
        t.getTipo(),
        t.getValor(),
        t.getDataTransacao(),
        t.getCategoria(),
        t.getOrigem());
  }

  private ResponseStatusException notFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação não encontrada");
  }

  private ResponseStatusException fazendaNotFound() {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fazenda inválida");
  }
}
