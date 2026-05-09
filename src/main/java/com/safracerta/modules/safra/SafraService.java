package com.safracerta.modules.safra;

import com.safracerta.modules.safra.dto.SafraRequestDto;
import com.safracerta.modules.safra.dto.SafraResponseDto;
import com.safracerta.modules.talhao.Talhao;
import com.safracerta.modules.talhao.TalhaoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SafraService {

  private final SafraRepository safraRepository;
  private final TalhaoRepository talhaoRepository;

  public SafraService(SafraRepository safraRepository, TalhaoRepository talhaoRepository) {
    this.safraRepository = safraRepository;
    this.talhaoRepository = talhaoRepository;
  }

  @Transactional(readOnly = true)
  public List<SafraResponseDto> listar() {
    return safraRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public SafraResponseDto buscar(Long id) {
    return safraRepository.findById(id).map(this::toResponse).orElseThrow(this::notFound);
  }

  @Transactional
  public SafraResponseDto criar(SafraRequestDto dto) {
    Talhao talhao =
        talhaoRepository.findById(dto.talhaoId()).orElseThrow(this::talhaoNotFound);
    Safra s = new Safra();
    s.setTalhao(talhao);
    apply(s, dto);
    return toResponse(safraRepository.save(s));
  }

  @Transactional
  public SafraResponseDto atualizar(Long id, SafraRequestDto dto) {
    Safra s = safraRepository.findById(id).orElseThrow(this::notFound);
    Talhao talhao =
        talhaoRepository.findById(dto.talhaoId()).orElseThrow(this::talhaoNotFound);
    s.setTalhao(talhao);
    apply(s, dto);
    return toResponse(safraRepository.save(s));
  }

  @Transactional
  public void excluir(Long id) {
    if (!safraRepository.existsById(id)) {
      throw notFound();
    }
    safraRepository.deleteById(id);
  }

  private void apply(Safra s, SafraRequestDto dto) {
    s.setCultura(dto.cultura());
    s.setDataPlantio(dto.dataPlantio());
    s.setDataColheita(dto.dataColheita());
  }

  private SafraResponseDto toResponse(Safra s) {
    return new SafraResponseDto(
        s.getId(),
        s.getTalhao().getId(),
        s.getCultura(),
        s.getDataPlantio(),
        s.getDataColheita());
  }

  private ResponseStatusException notFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Safra não encontrada");
  }

  private ResponseStatusException talhaoNotFound() {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Talhão inválido");
  }
}
