package com.safracerta.modules.talhao;

import com.safracerta.modules.fazenda.Fazenda;
import com.safracerta.modules.fazenda.FazendaRepository;
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

  public TalhaoService(TalhaoRepository talhaoRepository, FazendaRepository fazendaRepository) {
    this.talhaoRepository = talhaoRepository;
    this.fazendaRepository = fazendaRepository;
  }

  @Transactional(readOnly = true)
  public List<TalhaoResponseDto> listar() {
    return talhaoRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public TalhaoResponseDto buscar(Long id) {
    return talhaoRepository.findById(id).map(this::toResponse).orElseThrow(this::notFound);
  }

  @Transactional
  public TalhaoResponseDto criar(TalhaoRequestDto dto) {
    Fazenda fazenda =
        fazendaRepository.findById(dto.fazendaId()).orElseThrow(this::fazendaNotFound);
    Talhao t = new Talhao();
    t.setFazenda(fazenda);
    apply(t, dto);
    return toResponse(talhaoRepository.save(t));
  }

  @Transactional
  public TalhaoResponseDto atualizar(Long id, TalhaoRequestDto dto) {
    Talhao t = talhaoRepository.findById(id).orElseThrow(this::notFound);
    Fazenda fazenda =
        fazendaRepository.findById(dto.fazendaId()).orElseThrow(this::fazendaNotFound);
    t.setFazenda(fazenda);
    apply(t, dto);
    return toResponse(talhaoRepository.save(t));
  }

  @Transactional
  public void excluir(Long id) {
    if (!talhaoRepository.existsById(id)) {
      throw notFound();
    }
    talhaoRepository.deleteById(id);
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
