package com.safracerta.modules.fazenda;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FazendaUsuarioEscopoService {

  private final FazendaRepository fazendaRepository;
  private final FazendaHasUsuarioRepository fazendaHasUsuarioRepository;

  public FazendaUsuarioEscopoService(
      FazendaRepository fazendaRepository,
      FazendaHasUsuarioRepository fazendaHasUsuarioRepository) {
    this.fazendaRepository = fazendaRepository;
    this.fazendaHasUsuarioRepository = fazendaHasUsuarioRepository;
  }

  public List<Long> idsFazendasAcessiveis(Long usuarioId) {
    return fazendaRepository.findAllByUsuarioVinculado(usuarioId).stream()
        .map(Fazenda::getId)
        .toList();
  }

  public boolean usuarioPodeAcessarFazenda(Long usuarioId, Long fazendaId) {
    return fazendaHasUsuarioRepository.existsById(new FazendaUsuarioId(fazendaId, usuarioId));
  }

  /**
   * Para operações de escrita: utilizador não vinculado à fazenda não deve alterar dados dessa
   * fazenda.
   */
  public void garantirEscritaNaFazenda(Long usuarioId, Long fazendaId) {
    if (!usuarioPodeAcessarFazenda(usuarioId, fazendaId)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Sem acesso a esta fazenda");
    }
  }
}
