package com.safracerta.modules.auth;

import com.safracerta.modules.auth.dto.LoginRequestDto;
import com.safracerta.modules.auth.dto.LoginResponseDto;
import com.safracerta.modules.auth.dto.RegistroUsuarioRequestDto;
import com.safracerta.modules.auth.dto.RegistroUsuarioResponseDto;
import com.safracerta.modules.auth.dto.UserDto;
import com.safracerta.modules.fazenda.Fazenda;
import com.safracerta.modules.fazenda.FazendaHasUsuario;
import com.safracerta.modules.fazenda.FazendaHasUsuarioRepository;
import com.safracerta.modules.fazenda.FazendaRepository;
import com.safracerta.modules.fazenda.FazendaUsuarioId;
import com.safracerta.modules.perfil.Perfil;
import com.safracerta.modules.perfil.PerfilRepository;
import com.safracerta.modules.user.Usuario;
import com.safracerta.modules.user.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthApplicationService {

  private static final Logger log = LoggerFactory.getLogger(AuthApplicationService.class);

  private final UsuarioRepository usuarioRepository;
  private final FazendaRepository fazendaRepository;
  private final FazendaHasUsuarioRepository fazendaHasUsuarioRepository;
  private final PerfilRepository perfilRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final long perfilRegistroId;

  public AuthApplicationService(
      UsuarioRepository usuarioRepository,
      FazendaRepository fazendaRepository,
      FazendaHasUsuarioRepository fazendaHasUsuarioRepository,
      PerfilRepository perfilRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      @Value("${app.registro.perfil-id}") long perfilRegistroId) {
    this.usuarioRepository = usuarioRepository;
    this.fazendaRepository = fazendaRepository;
    this.fazendaHasUsuarioRepository = fazendaHasUsuarioRepository;
    this.perfilRepository = perfilRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.perfilRegistroId = perfilRegistroId;
  }

  @Transactional
  public RegistroUsuarioResponseDto registrar(RegistroUsuarioRequestDto dto) {
    String emailNormalizado = dto.email().trim().toLowerCase();
    if (usuarioRepository.existsByEmail(emailNormalizado)) {
      log.warn(
          "POST /auth/register → 409 CONFLICT | motivo=email_ja_cadastrado | email={}",
          emailNormalizado);
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
    }

    String codigo = dto.codigoAcesso().trim();
    Fazenda fazenda =
        fazendaRepository
            .findByCodFazenda(codigo)
            .orElseThrow(
                () -> {
                  log.warn(
                      "POST /auth/register → 400 BAD_REQUEST | motivo=codigo_fazenda_invalido | codigoAcesso(len={}, prefix={})",
                      codigo.length(),
                      codigo.length() > 3 ? codigo.substring(0, 3) + "…" : "***");
                  return new ResponseStatusException(
                      HttpStatus.BAD_REQUEST, "Código de acesso da fazenda inválido");
                });

    Perfil perfil =
        perfilRepository
            .findById(perfilRegistroId)
            .orElseThrow(
                () -> {
                  log.error(
                      "POST /auth/register → 500 | motivo=perfil_registro_inexistente | app.registro.perfil-id={}",
                      perfilRegistroId);
                  return new ResponseStatusException(
                      HttpStatus.INTERNAL_SERVER_ERROR,
                      "Perfil de cadastro não configurado (app.registro.perfil-id)");
                });

    Usuario u = new Usuario();
    u.setNome(dto.nome().trim());
    u.setEmail(dto.email().trim().toLowerCase());
    u.setSenha(passwordEncoder.encode(dto.senha()));
    u.setPerfil(perfil);
    u.setAtivo(false);

    Usuario saved = usuarioRepository.save(u);

    fazendaHasUsuarioRepository.save(
        new FazendaHasUsuario(new FazendaUsuarioId(fazenda.getId(), saved.getId())));

    String jwt = jwtService.generateToken(saved.getId(), saved.getEmail(), false);
    saved.setAutenticacao(jwt);
    usuarioRepository.save(saved);

    return new RegistroUsuarioResponseDto(
        saved.getId(),
        saved.getNome(),
        saved.getEmail(),
        false,
        "Cadastro recebido. Aguarde a liberação do acesso para entrar.");
  }

  @Transactional
  public LoginResponseDto login(LoginRequestDto body) {
    Usuario u =
        usuarioRepository
            .findByEmail(body.email().trim().toLowerCase())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

    if (!passwordEncoder.matches(body.password(), u.getSenha())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
    }

    if (!u.isAtivo()) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Conta aguardando ativação pelo administrador.");
    }

    String jwt = jwtService.generateToken(u.getId(), u.getEmail(), true);
    u.setAutenticacao(jwt);
    usuarioRepository.save(u);

    UserDto userDto =
        new UserDto(u.getId(), u.getEmail(), u.getNome(), u.isAtivo());
    return new LoginResponseDto(jwt, userDto);
  }
}
