package com.safracerta.modules.fazenda;

import com.safracerta.modules.fazenda.dto.FazendaRequestDto;
import com.safracerta.modules.fazenda.dto.FazendaResponseDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FazendaService {

  private final FazendaRepository fazendaRepository;

  public FazendaService(FazendaRepository fazendaRepository) {
    this.fazendaRepository = fazendaRepository;
  }

  @Transactional(readOnly = true)
  public List<FazendaResponseDto> listar() {
    return fazendaRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public FazendaResponseDto buscar(Long id) {
    return fazendaRepository.findById(id).map(this::toResponse).orElseThrow(this::notFound);
  }

  @Transactional
  public FazendaResponseDto criar(FazendaRequestDto dto) {
    Fazenda f = new Fazenda();
    apply(f, dto);
    return toResponse(fazendaRepository.save(f));
  }

  @Transactional
  public FazendaResponseDto atualizar(Long id, FazendaRequestDto dto) {
    Fazenda f = fazendaRepository.findById(id).orElseThrow(this::notFound);
    apply(f, dto);
    return toResponse(fazendaRepository.save(f));
  }

  @Transactional
  public void excluir(Long id) {
    if (!fazendaRepository.existsById(id)) {
      throw notFound();
    }
    fazendaRepository.deleteById(id);
  }

  private void apply(Fazenda f, FazendaRequestDto dto) {
    f.setNome(dto.nome());
    f.setLocalizacao(dto.localizacao());
    f.setAreaTotal(dto.areaTotal());
    f.setProprietario(dto.proprietario());
  }

  private FazendaResponseDto toResponse(Fazenda f) {
    return new FazendaResponseDto(
        f.getId(), f.getNome(), f.getLocalizacao(), f.getAreaTotal(), f.getProprietario());
  }

  private ResponseStatusException notFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Fazenda não encontrada");
  }
}
