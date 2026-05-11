package com.safracerta.modules.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safracerta.modules.auth.dto.LoginRequestDto;
import com.safracerta.modules.auth.dto.LoginResponseDto;
import com.safracerta.modules.auth.dto.RegistroUsuarioRequestDto;
import com.safracerta.modules.auth.dto.RegistroUsuarioResponseDto;
import com.safracerta.modules.fazenda.Fazenda;
import com.safracerta.modules.fazenda.FazendaHasUsuario;
import com.safracerta.modules.fazenda.FazendaHasUsuarioRepository;
import com.safracerta.modules.fazenda.FazendaRepository;
import com.safracerta.modules.perfil.Perfil;
import com.safracerta.modules.perfil.PerfilRepository;
import com.safracerta.modules.permissao.PerfilHasPermissaoRepository;
import com.safracerta.modules.user.Usuario;
import com.safracerta.modules.user.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

/**
 * Testes unitários (Mockito puro) para {@link AuthApplicationService}.
 *
 * <p>Cobre: registro com email duplicado, registro com código de fazenda inválido, registro de
 * sucesso (devolve usuário inativo), login com senha errada e login bem-sucedido.
 */
@ExtendWith(MockitoExtension.class)
class AuthApplicationServiceTest {

  @Mock private UsuarioRepository usuarioRepository;
  @Mock private PerfilHasPermissaoRepository perfilHasPermissaoRepository;
  @Mock private FazendaRepository fazendaRepository;
  @Mock private FazendaHasUsuarioRepository fazendaHasUsuarioRepository;
  @Mock private PerfilRepository perfilRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;

  private AuthApplicationService service;

  private static final long PERFIL_REGISTRO_ID = 2L;

  @BeforeEach
  void setUp() {
    service =
        new AuthApplicationService(
            usuarioRepository,
            perfilHasPermissaoRepository,
            fazendaRepository,
            fazendaHasUsuarioRepository,
            perfilRepository,
            passwordEncoder,
            jwtService,
            PERFIL_REGISTRO_ID);
  }

  @Test
  @DisplayName("registrar: email já cadastrado lança 409 CONFLICT")
  void registrar_emailDuplicado_lancaConflict() {
    when(usuarioRepository.existsByEmail("joao@safra.com")).thenReturn(true);
    var dto =
        new RegistroUsuarioRequestDto(
            "João da Silva", "joao@safra.com", "senha123", "CODFAZENDA");

    assertThatThrownBy(() -> service.registrar(dto))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Email já cadastrado")
        .extracting(e -> ((ResponseStatusException) e).getStatusCode())
        .isEqualTo(HttpStatus.CONFLICT);

    verify(usuarioRepository, never()).save(any());
  }

  @Test
  @DisplayName("registrar: código de fazenda inválido lança 400 BAD_REQUEST")
  void registrar_codigoFazendaInvalido_lancaBadRequest() {
    when(usuarioRepository.existsByEmail("ana@safra.com")).thenReturn(false);
    when(fazendaRepository.findByCodFazenda("INEXISTENTE")).thenReturn(Optional.empty());
    var dto =
        new RegistroUsuarioRequestDto("Ana Souza", "ana@safra.com", "senha123", "INEXISTENTE");

    assertThatThrownBy(() -> service.registrar(dto))
        .isInstanceOf(ResponseStatusException.class)
        .extracting(e -> ((ResponseStatusException) e).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);

    verify(usuarioRepository, never()).save(any());
  }

  @Test
  @DisplayName("registrar: caso de sucesso persiste usuário inativo e devolve mensagem de espera")
  void registrar_sucesso_persisteUsuarioInativo() {
    Fazenda fazenda = new Fazenda();
    fazenda.setId(10L);
    Perfil perfil = new Perfil();
    perfil.setId(PERFIL_REGISTRO_ID);

    when(usuarioRepository.existsByEmail("maria@safra.com")).thenReturn(false);
    when(fazendaRepository.findByCodFazenda("FAZENDA001")).thenReturn(Optional.of(fazenda));
    when(perfilRepository.findById(PERFIL_REGISTRO_ID)).thenReturn(Optional.of(perfil));
    when(passwordEncoder.encode("senha123")).thenReturn("hashSimulado");
    when(usuarioRepository.save(any(Usuario.class)))
        .thenAnswer(
            inv -> {
              Usuario u = inv.getArgument(0);
              if (u.getId() == null) u.setId(55L);
              return u;
            });
    when(jwtService.generateToken(55L, "maria@safra.com", false)).thenReturn("jwt-mock");

    var dto =
        new RegistroUsuarioRequestDto("Maria Lima", "maria@safra.com", "senha123", "FAZENDA001");
    RegistroUsuarioResponseDto resp = service.registrar(dto);

    assertThat(resp.id()).isEqualTo(55L);
    assertThat(resp.email()).isEqualTo("maria@safra.com");
    assertThat(resp.ativo()).isFalse();
    assertThat(resp.mensagem()).contains("liberação");

    verify(usuarioRepository, times(2)).save(any(Usuario.class));
    verify(fazendaHasUsuarioRepository).save(any(FazendaHasUsuario.class));
  }

  @Test
  @DisplayName("login: senha errada lança 401 UNAUTHORIZED")
  void login_senhaErrada_lancaUnauthorized() {
    Usuario u = new Usuario();
    u.setId(1L);
    u.setEmail("joao@safra.com");
    u.setSenha("hashCerto");
    u.setAtivo(true);

    when(usuarioRepository.findByEmail("joao@safra.com")).thenReturn(Optional.of(u));
    when(passwordEncoder.matches("senhaErrada", "hashCerto")).thenReturn(false);

    assertThatThrownBy(() -> service.login(new LoginRequestDto("joao@safra.com", "senhaErrada")))
        .isInstanceOf(ResponseStatusException.class)
        .extracting(e -> ((ResponseStatusException) e).getStatusCode())
        .isEqualTo(HttpStatus.UNAUTHORIZED);

    verify(jwtService, never()).generateToken(anyLong(), anyString(), anyBoolean());
  }

  @Test
  @DisplayName("login: credenciais válidas retornam token e UserDto")
  void login_sucesso_retornaToken() {
    Perfil perfil = new Perfil();
    perfil.setId(2L);
    Usuario u = new Usuario();
    u.setId(7L);
    u.setEmail("joao@safra.com");
    u.setSenha("hashOk");
    u.setNome("João");
    u.setAtivo(true);
    u.setPerfil(perfil);

    when(usuarioRepository.findByEmail("joao@safra.com")).thenReturn(Optional.of(u));
    when(passwordEncoder.matches("senhaCerta", "hashOk")).thenReturn(true);
    when(jwtService.generateToken(7L, "joao@safra.com", true)).thenReturn("token-final");
    when(perfilHasPermissaoRepository.findConcedidasVisiveisPorPerfil(2L))
        .thenReturn(java.util.List.of());

    LoginResponseDto resp = service.login(new LoginRequestDto("joao@safra.com", "senhaCerta"));

    assertThat(resp.token()).isEqualTo("token-final");
    assertThat(resp.user().email()).isEqualTo("joao@safra.com");
    assertThat(resp.user().ativo()).isTrue();
    verify(usuarioRepository).save(u);
  }
}
