package com.safracerta.modules.safra;

import com.safracerta.modules.safra.dto.SafraExclusaoRequestDto;
import com.safracerta.modules.safra.dto.SafraRequestDto;
import com.safracerta.modules.safra.dto.SafraResponseDto;
import com.safracerta.modules.talhao.Talhao;
import com.safracerta.modules.talhao.TalhaoRepository;
import com.safracerta.modules.user.Usuario;
import com.safracerta.modules.user.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SafraService {

  private final SafraRepository safraRepository;
  private final TalhaoRepository talhaoRepository;
  private final UsuarioRepository usuarioRepository;

  public SafraService(
      SafraRepository safraRepository,
      TalhaoRepository talhaoRepository,
      UsuarioRepository usuarioRepository) {
    this.safraRepository = safraRepository;
    this.talhaoRepository = talhaoRepository;
    this.usuarioRepository = usuarioRepository;
  }

  @Transactional(readOnly = true)
  public List<SafraResponseDto> listar() {
    return safraRepository.findByExcluidoFalse().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public SafraResponseDto buscar(Long id) {
    return safraRepository
        .findByIdAndExcluidoFalse(id)
        .map(this::toResponse)
        .orElseThrow(this::notFound);
  }

  @Transactional
  public SafraResponseDto criar(SafraRequestDto dto) {
    Safra s = new Safra();
    s.setTalhao(resolveTalhao(dto.talhaoId()));
    apply(s, dto);
    return toResponse(safraRepository.save(s));
  }

  @Transactional
  public SafraResponseDto atualizar(Long id, SafraRequestDto dto) {
    Safra s = safraRepository.findByIdAndExcluidoFalse(id).orElseThrow(this::notFound);
    s.setTalhao(resolveTalhao(dto.talhaoId()));
    apply(s, dto);
    return toResponse(safraRepository.save(s));
  }

  private Talhao resolveTalhao(Long talhaoId) {
    if (talhaoId == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Talhão é obrigatório");
    }
    return talhaoRepository.findById(talhaoId).orElseThrow(this::talhaoNotFound);
  }

  @Transactional
  public void excluir(Long id, SafraExclusaoRequestDto dto, Long usuarioId) {
    Safra s = safraRepository.findByIdAndExcluidoFalse(id).orElseThrow(this::notFound);
    Usuario u =
        usuarioRepository
            .findById(usuarioId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário inválido"));
    s.setExcluido(true);
    s.setExcluidoPor(u);
    s.setJustificativaExclusao(dto.justificativa().trim());
    s.setExcluidoEm(LocalDateTime.now());
    safraRepository.save(s);
  }

  private void apply(Safra s, SafraRequestDto dto) {
    s.setNome(dto.nome());
    s.setCultura(dto.cultura());
    s.setStatus(dto.status());
    s.setDataPlantio(dto.dataPlantio());
    s.setDataColheitaPrevista(dto.dataColheitaPrevista());
    s.setDataColheitaReal(dto.dataColheitaReal());
    s.setProducaoEstimada(dto.producaoEstimada());
    s.setProducaoReal(dto.producaoReal());
  }

  private SafraResponseDto toResponse(Safra s) {
    Talhao talhao = s.getTalhao();
    return new SafraResponseDto(
        s.getId(),
        s.getNome(),
        talhao != null ? talhao.getId() : null,
        talhao != null ? talhao.getNome() : null,
        s.getCultura(),
        s.getStatus(),
        s.getDataPlantio(),
        s.getDataColheitaPrevista(),
        s.getDataColheitaReal(),
        s.getProducaoEstimada(),
        s.getProducaoReal());
  }

  private ResponseStatusException notFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Safra não encontrada");
  }

  private ResponseStatusException talhaoNotFound() {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Talhão inválido");
  }
}
