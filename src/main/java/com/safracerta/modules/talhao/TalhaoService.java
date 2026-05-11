package com.safracerta.modules.talhao;

import com.safracerta.modules.fazenda.Fazenda;
import com.safracerta.modules.fazenda.FazendaRepository;
import com.safracerta.modules.fazenda.FazendaUsuarioEscopoService;
import com.safracerta.modules.talhao.dto.TalhaoRequestDto;
import com.safracerta.modules.talhao.dto.TalhaoResponseDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TalhaoService {

  private final TalhaoRepository talhaoRepository;
  private final FazendaRepository fazendaRepository;
  private final FazendaUsuarioEscopoService fazendaUsuarioEscopoService;

  public TalhaoService(
      TalhaoRepository talhaoRepository,
      FazendaRepository fazendaRepository,
      FazendaUsuarioEscopoService fazendaUsuarioEscopoService) {
    this.talhaoRepository = talhaoRepository;
    this.fazendaRepository = fazendaRepository;
    this.fazendaUsuarioEscopoService = fazendaUsuarioEscopoService;
  }

  @Transactional(readOnly = true)
  public List<TalhaoResponseDto> listar(Long usuarioId) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      return List.of();
    }
    return talhaoRepository.findByFazenda_IdInOrderByNomeAsc(fazendaIds).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<TalhaoResponseDto> listarPorFazenda(Long usuarioId, Long fazendaId) {
    fazendaUsuarioEscopoService.garantirEscritaNaFazenda(usuarioId, fazendaId);
    return talhaoRepository.findByFazenda_IdOrderByNomeAsc(fazendaId).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public TalhaoResponseDto buscar(Long id, Long usuarioId) {
    Talhao t = talhaoRepository.findById(id).orElseThrow(this::notFound);
    if (!fazendaUsuarioEscopoService.usuarioPodeAcessarFazenda(
        usuarioId, t.getFazenda().getId())) {
      throw notFound();
    }
    return toResponse(t);
  }

  @Transactional
  public TalhaoResponseDto criar(Long usuarioId, TalhaoRequestDto dto) {
    fazendaUsuarioEscopoService.garantirEscritaNaFazenda(usuarioId, dto.fazendaId());
    Fazenda fazenda =
        fazendaRepository.findById(dto.fazendaId()).orElseThrow(this::fazendaNotFound);
    Talhao talhao = new Talhao();
    talhao.setFazenda(fazenda);
    apply(talhao, dto);
    return toResponse(talhaoRepository.save(talhao));
  }

  @Transactional
  public TalhaoResponseDto atualizar(Long id, Long usuarioId, TalhaoRequestDto dto) {
    Talhao t = talhaoRepository.findById(id).orElseThrow(this::notFound);
    fazendaUsuarioEscopoService.garantirEscritaNaFazenda(usuarioId, t.getFazenda().getId());
    fazendaUsuarioEscopoService.garantirEscritaNaFazenda(usuarioId, dto.fazendaId());
    Fazenda fazenda =
        fazendaRepository.findById(dto.fazendaId()).orElseThrow(this::fazendaNotFound);
    t.setFazenda(fazenda);
    apply(t, dto);
    return toResponse(talhaoRepository.save(t));
  }

  @Transactional
  public void excluir(Long id, Long usuarioId) {
    Talhao t = talhaoRepository.findById(id).orElseThrow(this::notFound);
    fazendaUsuarioEscopoService.garantirEscritaNaFazenda(usuarioId, t.getFazenda().getId());
    talhaoRepository.delete(t);
  }

  private void apply(Talhao t, TalhaoRequestDto dto) {
    t.setNome(dto.nome());
    t.setAreaHectares(dto.areaHectares());
    t.setTipoCultivo(dto.tipoCultivo());
  }

  private TalhaoResponseDto toResponse(Talhao t) {
    Fazenda f = t.getFazenda();
    return new TalhaoResponseDto(
        t.getId(),
        f != null ? f.getId() : null,
        f != null ? f.getNome() : null,
        t.getNome(),
        t.getAreaHectares(),
        t.getTipoCultivo());
  }

  private ResponseStatusException notFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Talhão não encontrado");
  }

  private ResponseStatusException fazendaNotFound() {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fazenda inválida");
  }
}
