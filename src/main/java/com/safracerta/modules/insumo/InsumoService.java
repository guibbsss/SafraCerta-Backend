package com.safracerta.modules.insumo;

import com.safracerta.modules.fazenda.Fazenda;
import com.safracerta.modules.fazenda.FazendaRepository;
import com.safracerta.modules.fazenda.FazendaUsuarioEscopoService;
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
  private final FazendaUsuarioEscopoService fazendaUsuarioEscopoService;

  public InsumoService(
      InsumoRepository insumoRepository,
      FazendaRepository fazendaRepository,
      FazendaUsuarioEscopoService fazendaUsuarioEscopoService) {
    this.insumoRepository = insumoRepository;
    this.fazendaRepository = fazendaRepository;
    this.fazendaUsuarioEscopoService = fazendaUsuarioEscopoService;
  }

  @Transactional(readOnly = true)
  public List<InsumoResponseDto> listar(Long usuarioId) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      return List.of();
    }
    return insumoRepository.findByFazenda_IdInOrderByNomeAsc(fazendaIds).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<InsumoResponseDto> listarPorFazenda(Long usuarioId, Long fazendaId) {
    fazendaUsuarioEscopoService.garantirEscritaNaFazenda(usuarioId, fazendaId);
    return insumoRepository.findByFazenda_IdOrderByNomeAsc(fazendaId).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public InsumoResponseDto buscar(Long id, Long usuarioId) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      throw notFound();
    }
    return insumoRepository
        .findByIdAndFazenda_IdIn(id, fazendaIds)
        .map(this::toResponse)
        .orElseThrow(this::notFound);
  }

  @Transactional
  public InsumoResponseDto criar(Long usuarioId, InsumoRequestDto dto) {
    Fazenda fazenda = resolveFazenda(dto.fazendaId());
    fazendaUsuarioEscopoService.garantirEscritaNaFazenda(usuarioId, fazenda.getId());
    Insumo i = new Insumo();
    i.setFazenda(fazenda);
    apply(i, dto);
    return toResponse(insumoRepository.save(i));
  }

  @Transactional
  public InsumoResponseDto atualizar(Long usuarioId, Long id, InsumoRequestDto dto) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      throw notFound();
    }
    Insumo i = insumoRepository.findByIdAndFazenda_IdIn(id, fazendaIds).orElseThrow(this::notFound);
    Fazenda fazenda = resolveFazenda(dto.fazendaId());
    fazendaUsuarioEscopoService.garantirEscritaNaFazenda(usuarioId, fazenda.getId());
    i.setFazenda(fazenda);
    apply(i, dto);
    return toResponse(insumoRepository.save(i));
  }

  @Transactional
  public void excluir(Long usuarioId, Long id) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      throw notFound();
    }
    Insumo i = insumoRepository.findByIdAndFazenda_IdIn(id, fazendaIds).orElseThrow(this::notFound);
    insumoRepository.delete(i);
  }

  private Fazenda resolveFazenda(Long fazendaId) {
    return fazendaRepository.findById(fazendaId).orElseThrow(this::fazendaNotFound);
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
