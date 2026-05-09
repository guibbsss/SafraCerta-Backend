package com.safracerta.modules.user;

import com.safracerta.modules.perfil.Perfil;
import com.safracerta.modules.perfil.PerfilRepository;
import com.safracerta.modules.user.dto.UsuarioRequestDto;
import com.safracerta.modules.user.dto.UsuarioResponseDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final PerfilRepository perfilRepository;

  public UsuarioService(UsuarioRepository usuarioRepository, PerfilRepository perfilRepository) {
    this.usuarioRepository = usuarioRepository;
    this.perfilRepository = perfilRepository;
  }

  @Transactional(readOnly = true)
  public List<UsuarioResponseDto> listar() {
    return usuarioRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public UsuarioResponseDto buscar(Long id) {
    return usuarioRepository.findById(id).map(this::toResponse).orElseThrow(this::notFound);
  }

  @Transactional
  public UsuarioResponseDto criar(UsuarioRequestDto dto) {
    Perfil perfil =
        perfilRepository.findById(dto.perfilId()).orElseThrow(this::perfilNotFound);
    Usuario u = new Usuario();
    u.setEmail(dto.email());
    u.setSenha(dto.senha());
    u.setNome(dto.nome());
    u.setPerfil(perfil);
    return toResponse(usuarioRepository.save(u));
  }

  @Transactional
  public UsuarioResponseDto atualizar(Long id, UsuarioRequestDto dto) {
    Usuario u = usuarioRepository.findById(id).orElseThrow(this::notFound);
    Perfil perfil =
        perfilRepository.findById(dto.perfilId()).orElseThrow(this::perfilNotFound);
    u.setEmail(dto.email());
    u.setSenha(dto.senha());
    u.setNome(dto.nome());
    u.setPerfil(perfil);
    return toResponse(usuarioRepository.save(u));
  }

  @Transactional
  public void excluir(Long id) {
    if (!usuarioRepository.existsById(id)) {
      throw notFound();
    }
    usuarioRepository.deleteById(id);
  }

  private UsuarioResponseDto toResponse(Usuario u) {
    return new UsuarioResponseDto(
        u.getId(), u.getEmail(), u.getNome(), u.getPerfil().getId());
  }

  private ResponseStatusException notFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
  }

  private ResponseStatusException perfilNotFound() {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Perfil inválido");
  }
}
