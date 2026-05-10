package com.safracerta.modules.insumo;

import com.safracerta.modules.fazenda.Fazenda;
import com.safracerta.modules.fazenda.FazendaRepository;
import com.safracerta.modules.insumo.dto.InsumoRequestDto;
import com.safracerta.modules.insumo.dto.InsumoResponseDto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class InsumoService {

  private final InsumoRepository insumoRepository;
  private final FazendaRepository fazendaRepository;

  public InsumoService(InsumoRepository insumoRepository, FazendaRepository fazendaRepository) {
    this.insumoRepository = insumoRepository;
    this.fazendaRepository = fazendaRepository;
  }

  @Transactional(readOnly = true)
  public List<InsumoResponseDto> listar() {
    return insumoRepository.findAllWithFazenda().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public List<InsumoResponseDto> listarPorFazenda(Long fazendaId) {
    return insumoRepository.findByFazenda_IdOrderByNomeAsc(fazendaId).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public InsumoResponseDto buscar(Long id) {
    return insumoRepository.findById(id).map(this::toResponse).orElseThrow(this::notFound);
  }

  @Transactional
  public InsumoResponseDto criar(InsumoRequestDto dto) {
    Fazenda fazenda =
        fazendaRepository.findById(dto.fazendaId()).orElseThrow(this::fazendaNotFound);
    Insumo i = new Insumo();
    i.setFazenda(fazenda);
    apply(i, dto);
    return toResponse(insumoRepository.save(i));
  }

  @Transactional
  public InsumoResponseDto atualizar(Long id, InsumoRequestDto dto) {
    Insumo i = insumoRepository.findById(id).orElseThrow(this::notFound);
    Fazenda fazenda =
        fazendaRepository.findById(dto.fazendaId()).orElseThrow(this::fazendaNotFound);
    i.setFazenda(fazenda);
    apply(i, dto);
    return toResponse(insumoRepository.save(i));
  }

  @Transactional
  public void excluir(Long id) {
    if (!insumoRepository.existsById(id)) {
      throw notFound();
    }
    insumoRepository.deleteById(id);
  }

  private void apply(Insumo i, InsumoRequestDto dto) {
    i.setNome(dto.nome());
    i.setCategoria(dto.categoria());
    i.setQuantidadeAtual(dto.quantidadeAtual());
    i.setUnidadeMedida(dto.unidadeMedida());
    if (dto.valorUnitarioReferencia() != null) {
      i.setValorUnitarioReferencia(dto.valorUnitarioReferencia());
    }
  }

  private InsumoResponseDto toResponse(Insumo i) {
    BigDecimal valorTotalEstimado = calcularValorTotalEstimado(i);
    return new InsumoResponseDto(
        i.getId(),
        i.getFazenda().getId(),
        i.getFazenda().getNome(),
        i.getNome(),
        i.getCategoria(),
        i.getQuantidadeAtual(),
        i.getUnidadeMedida(),
        i.getValorUnitarioReferencia(),
        valorTotalEstimado);
  }

  private static BigDecimal calcularValorTotalEstimado(Insumo i) {
    if (i.getValorUnitarioReferencia() == null || i.getQuantidadeAtual() == null) {
      return null;
    }
    return i.getQuantidadeAtual().multiply(i.getValorUnitarioReferencia());
  }

  private ResponseStatusException notFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo não encontrado");
  }

  private ResponseStatusException fazendaNotFound() {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fazenda inválida");
  }
}
