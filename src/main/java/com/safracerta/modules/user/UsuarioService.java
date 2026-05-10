package com.safracerta.modules.user;

import com.safracerta.modules.perfil.Perfil;
import com.safracerta.modules.perfil.PerfilRepository;
import com.safracerta.modules.user.dto.UsuarioRequestDto;
import com.safracerta.modules.user.dto.UsuarioResponseDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final PerfilRepository perfilRepository;
  private final PasswordEncoder passwordEncoder;

  public UsuarioService(
      UsuarioRepository usuarioRepository,
      PerfilRepository perfilRepository,
      PasswordEncoder passwordEncoder) {
    this.usuarioRepository = usuarioRepository;
    this.perfilRepository = perfilRepository;
    this.passwordEncoder = passwordEncoder;
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
    u.setSenha(passwordEncoder.encode(dto.senha()));
    u.setNome(dto.nome());
    u.setPerfil(perfil);
    return toResponse(usuarioRepository.save(u));
  }

  /**
   * Atualiza dados cadastrais. O JWT armazenado em {@code usuario.autenticacao} não é alterado
   * aqui. Se no futuro validar o {@code Authorization} neste fluxo, não use apenas validação de
   * expiração do JWT ({@code JwtService#parse}) como critério único — tokens expirados ainda podem
   * precisar ser aceites para não bloquear atualizações (ex.: {@code JwtService#parseIgnoringExpiration}
   * ou comparação com o token persistido).
   */
  @Transactional
  public UsuarioResponseDto atualizar(Long id, UsuarioRequestDto dto) {
    Usuario u = usuarioRepository.findById(id).orElseThrow(this::notFound);
    Perfil perfil =
        perfilRepository.findById(dto.perfilId()).orElseThrow(this::perfilNotFound);
    u.setEmail(dto.email());
    u.setSenha(passwordEncoder.encode(dto.senha()));
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
