package com.safracerta.modules.atividadeagricola;

import com.safracerta.modules.atividadeagricola.dto.AtividadeAgricolaRequestDto;
import com.safracerta.modules.atividadeagricola.dto.AtividadeAgricolaResponseDto;
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

  public AtividadeAgricolaService(
      AtividadeAgricolaRepository atividadeRepository,
      TalhaoRepository talhaoRepository) {
    this.atividadeRepository = atividadeRepository;
    this.talhaoRepository = talhaoRepository;
  }

  @Transactional(readOnly = true)
  public List<AtividadeAgricolaResponseDto> listar() {
    return atividadeRepository.findAllByOrderByDataAtividadeDesc().stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<AtividadeAgricolaResponseDto> listarPorTalhao(Long talhaoId) {
    return atividadeRepository.findByTalhao_IdOrderByDataAtividadeDesc(talhaoId).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public AtividadeAgricolaResponseDto buscar(Long id) {
    return atividadeRepository.findWithTalhaoById(id).map(this::toResponse).orElseThrow(this::notFound);
  }

  @Transactional
  public AtividadeAgricolaResponseDto criar(AtividadeAgricolaRequestDto dto) {
    Talhao talhao =
        talhaoRepository.findById(dto.talhaoId()).orElseThrow(this::talhaoNotFound);
    AtividadeAgricola a = new AtividadeAgricola();
    a.setTalhao(talhao);
    apply(a, dto);
    return toResponse(atividadeRepository.save(a));
  }

  @Transactional
  public AtividadeAgricolaResponseDto atualizar(Long id, AtividadeAgricolaRequestDto dto) {
    AtividadeAgricola a = atividadeRepository.findById(id).orElseThrow(this::notFound);
    Talhao talhao =
        talhaoRepository.findById(dto.talhaoId()).orElseThrow(this::talhaoNotFound);
    a.setTalhao(talhao);
    apply(a, dto);
    return toResponse(atividadeRepository.save(a));
  }

  @Transactional
  public void excluir(Long id) {
    if (!atividadeRepository.existsById(id)) {
      throw notFound();
    }
    atividadeRepository.deleteById(id);
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
