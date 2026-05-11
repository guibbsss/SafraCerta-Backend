package com.safracerta.modules.atividadeagricola;

import com.safracerta.modules.atividadeagricola.dto.AtividadeAgricolaRequestDto;
import com.safracerta.modules.atividadeagricola.dto.AtividadeAgricolaResponseDto;
import com.safracerta.modules.fazenda.FazendaUsuarioEscopoService;
import com.safracerta.modules.talhao.Talhao;
import com.safracerta.modules.talhao.TalhaoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AtividadeAgricolaService {

  private final AtividadeAgricolaRepository atividadeRepository;
  private final TalhaoRepository talhaoRepository;
  private final FazendaUsuarioEscopoService fazendaUsuarioEscopoService;

  public AtividadeAgricolaService(
      AtividadeAgricolaRepository atividadeRepository,
      TalhaoRepository talhaoRepository,
      FazendaUsuarioEscopoService fazendaUsuarioEscopoService) {
    this.atividadeRepository = atividadeRepository;
    this.talhaoRepository = talhaoRepository;
    this.fazendaUsuarioEscopoService = fazendaUsuarioEscopoService;
  }

  @Transactional(readOnly = true)
  public List<AtividadeAgricolaResponseDto> listar(Long usuarioId) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      return List.of();
    }
    return atividadeRepository.findByTalhao_Fazenda_IdInOrderByDataAtividadeDesc(fazendaIds).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<AtividadeAgricolaResponseDto> listarPorTalhao(Long usuarioId, Long talhaoId) {
    Talhao talhao = talhaoRepository.findById(talhaoId).orElseThrow(this::talhaoNotFound);
    fazendaUsuarioEscopoService.garantirEscritaNaFazenda(
        usuarioId, talhao.getFazenda().getId());
    return atividadeRepository.findByTalhao_IdOrderByDataAtividadeDesc(talhaoId).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public AtividadeAgricolaResponseDto buscar(Long id, Long usuarioId) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      throw notFound();
    }
    return atividadeRepository
        .findWithTalhaoByIdAndTalhao_Fazenda_IdIn(id, fazendaIds)
        .map(this::toResponse)
        .orElseThrow(this::notFound);
  }

  @Transactional
  public AtividadeAgricolaResponseDto criar(Long usuarioId, AtividadeAgricolaRequestDto dto) {
    Talhao talhao = resolveTalhaoParaUsuario(dto.talhaoId(), usuarioId);
    AtividadeAgricola a = new AtividadeAgricola();
    a.setTalhao(talhao);
    apply(a, dto);
    return toResponse(atividadeRepository.save(a));
  }

  @Transactional
  public AtividadeAgricolaResponseDto atualizar(
      Long usuarioId, Long id, AtividadeAgricolaRequestDto dto) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      throw notFound();
    }
    AtividadeAgricola a =
        atividadeRepository
            .findWithTalhaoByIdAndTalhao_Fazenda_IdIn(id, fazendaIds)
            .orElseThrow(this::notFound);
    Talhao talhao = resolveTalhaoParaUsuario(dto.talhaoId(), usuarioId);
    a.setTalhao(talhao);
    apply(a, dto);
    return toResponse(atividadeRepository.save(a));
  }

  @Transactional
  public void excluir(Long usuarioId, Long id) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      throw notFound();
    }
    AtividadeAgricola a =
        atividadeRepository
            .findWithTalhaoByIdAndTalhao_Fazenda_IdIn(id, fazendaIds)
            .orElseThrow(this::notFound);
    atividadeRepository.delete(a);
  }

  private Talhao resolveTalhaoParaUsuario(Long talhaoId, Long usuarioId) {
    Talhao talhao = talhaoRepository.findById(talhaoId).orElseThrow(this::talhaoNotFound);
    fazendaUsuarioEscopoService.garantirEscritaNaFazenda(
        usuarioId, talhao.getFazenda().getId());
    return talhao;
  }

  private void apply(AtividadeAgricola a, AtividadeAgricolaRequestDto dto) {
    a.setTipoOperacao(dto.tipoOperacao());
    a.setDataAtividade(dto.dataAtividade());
    a.setDescricao(dto.descricao());
  }

  private AtividadeAgricolaResponseDto toResponse(AtividadeAgricola a) {
    Talhao t = a.getTalhao();
    return new AtividadeAgricolaResponseDto(
        a.getId(),
        t != null ? t.getId() : null,
        t != null ? t.getNome() : null,
        a.getTipoOperacao(),
        a.getDataAtividade(),
        a.getDescricao());
  }

  private ResponseStatusException notFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Atividade não encontrada");
  }

  private ResponseStatusException talhaoNotFound() {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Talhão inválido");
  }
}
