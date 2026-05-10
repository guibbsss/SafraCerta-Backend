package com.safracerta.modules.perfil;

import com.safracerta.modules.perfil.dto.PerfilRequestDto;
import com.safracerta.modules.perfil.dto.PerfilResponseDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PerfilService {

  private final PerfilRepository perfilRepository;

  public PerfilService(PerfilRepository perfilRepository) {
    this.perfilRepository = perfilRepository;
  }

  @Transactional(readOnly = true)
  public List<PerfilResponseDto> listar() {
    return perfilRepository.findAllByExcluidoFalseOrderByNomeAsc().stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public PerfilResponseDto buscar(Long id) {
    return perfilRepository
        .findById(id)
        .filter(p -> !p.isExcluido())
        .map(this::toResponse)
        .orElseThrow(this::notFound);
  }

  @Transactional
  public PerfilResponseDto criar(PerfilRequestDto dto) {
    Perfil p = new Perfil();
    p.setNome(dto.nome());
    p.setAtivo(true);
    p.setExcluido(false);
    return toResponse(perfilRepository.save(p));
  }

  @Transactional
  public PerfilResponseDto atualizar(Long id, PerfilRequestDto dto) {
    Perfil p =
        perfilRepository
            .findById(id)
            .filter(x -> !x.isExcluido())
            .orElseThrow(this::notFound);
    p.setNome(dto.nome());
    return toResponse(perfilRepository.save(p));
  }

  @Transactional
  public void excluir(Long id) {
    Perfil p = perfilRepository.findById(id).orElseThrow(this::notFound);
    p.setExcluido(true);
    perfilRepository.save(p);
  }

  private PerfilResponseDto toResponse(Perfil p) {
    return new PerfilResponseDto(p.getId(), p.getNome(), p.isAtivo(), p.isExcluido());
  }

  private ResponseStatusException notFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado");
  }
}
