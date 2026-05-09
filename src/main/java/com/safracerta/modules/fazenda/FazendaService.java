package com.safracerta.modules.fazenda;

import com.safracerta.modules.fazenda.dto.FazendaCreateDto;
import com.safracerta.modules.fazenda.dto.FazendaResponseDto;
import com.safracerta.modules.fazenda.dto.FazendaUpdateDto;
import com.safracerta.modules.user.Usuario;
import com.safracerta.modules.user.UsuarioRepository;
import java.security.SecureRandom;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FazendaService {

  private static final String CODIGO_CHARSET =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final int CODIGO_LEN = 10;
  private static final int MAX_GERACAO_TENTATIVAS = 16;

  private final FazendaRepository fazendaRepository;
  private final UsuarioRepository usuarioRepository;
  private final FazendaHasUsuarioRepository fazendaHasUsuarioRepository;
  private final SecureRandom secureRandom = new SecureRandom();

  public FazendaService(
      FazendaRepository fazendaRepository,
      UsuarioRepository usuarioRepository,
      FazendaHasUsuarioRepository fazendaHasUsuarioRepository) {
    this.fazendaRepository = fazendaRepository;
    this.usuarioRepository = usuarioRepository;
    this.fazendaHasUsuarioRepository = fazendaHasUsuarioRepository;
  }

  @Transactional(readOnly = true)
  public List<FazendaResponseDto> listar() {
    return fazendaRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public List<FazendaResponseDto> listarPorUsuario(Long usuarioId) {
    return fazendaRepository.findAllByUsuarioVinculado(usuarioId).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public FazendaResponseDto buscar(Long id) {
    return fazendaRepository.findById(id).map(this::toResponse).orElseThrow(this::notFound);
  }

  @Transactional
  public FazendaResponseDto criar(FazendaCreateDto dto) {
    Usuario proprietario =
        usuarioRepository
            .findById(dto.proprietarioId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Usuário proprietário não encontrado"));

    Fazenda f = new Fazenda();
    f.setNome(dto.nome());
    f.setLocalizacao(dto.localizacao());
    f.setAreaTotal(dto.areaTotal());
    f.setProprietario(proprietario);
    f.setCodFazenda(gerarCodigoFazendaUnico());

    Fazenda saved = fazendaRepository.save(f);
    fazendaHasUsuarioRepository.save(
        new FazendaHasUsuario(
            new FazendaUsuarioId(saved.getId(), proprietario.getId())));

    return toResponse(saved);
  }

  @Transactional
  public FazendaResponseDto atualizar(Long id, FazendaUpdateDto dto) {
    Fazenda f = fazendaRepository.findById(id).orElseThrow(this::notFound);
    f.setNome(dto.nome());
    f.setLocalizacao(dto.localizacao());
    f.setAreaTotal(dto.areaTotal());
    return toResponse(fazendaRepository.save(f));
  }

  @Transactional
  public void excluir(Long id) {
    if (!fazendaRepository.existsById(id)) {
      throw notFound();
    }
    fazendaRepository.deleteById(id);
  }

  private String gerarCodigoFazendaUnico() {
    for (int tentativa = 0; tentativa < MAX_GERACAO_TENTATIVAS; tentativa++) {
      String codigo = gerarCodigoAleatorio();
      if (!fazendaRepository.existsByCodFazenda(codigo)) {
        return codigo;
      }
    }
    throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível gerar código único para a fazenda");
  }

  private String gerarCodigoAleatorio() {
    StringBuilder sb = new StringBuilder(CODIGO_LEN);
    for (int i = 0; i < CODIGO_LEN; i++) {
      sb.append(CODIGO_CHARSET.charAt(secureRandom.nextInt(CODIGO_CHARSET.length())));
    }
    return sb.toString();
  }

  private FazendaResponseDto toResponse(Fazenda f) {
    Long proprietarioId = f.getProprietario() != null ? f.getProprietario().getId() : null;
    return new FazendaResponseDto(
        f.getId(),
        f.getNome(),
        f.getLocalizacao(),
        f.getAreaTotal(),
        f.getCodFazenda(),
        proprietarioId);
  }

  private ResponseStatusException notFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Fazenda não encontrada");
  }
}
