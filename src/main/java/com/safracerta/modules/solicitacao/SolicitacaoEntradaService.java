package com.safracerta.modules.solicitacao;

import com.safracerta.modules.fazenda.Fazenda;
import com.safracerta.modules.fazenda.FazendaHasUsuarioRepository;
import com.safracerta.modules.fazenda.FazendaRepository;
import com.safracerta.modules.fazenda.FazendaUsuarioId;
import com.safracerta.modules.perfil.Perfil;
import com.safracerta.modules.perfil.PerfilRepository;
import com.safracerta.modules.solicitacao.dto.AprovarEntradaRequestDto;
import com.safracerta.modules.solicitacao.dto.PerfilDoUsuarioDto;
import com.safracerta.modules.solicitacao.dto.SolicitacaoEntradaResponseDto;
import com.safracerta.modules.solicitacao.projection.SolicitacaoEntradaListagemProj;
import com.safracerta.modules.user.Usuario;
import com.safracerta.modules.user.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SolicitacaoEntradaService {

  private final FazendaHasUsuarioRepository fazendaHasUsuarioRepository;
  private final UsuarioRepository usuarioRepository;
  private final FazendaRepository fazendaRepository;
  private final PerfilRepository perfilRepository;
  private final long perfilRegistroId;

  public SolicitacaoEntradaService(
      FazendaHasUsuarioRepository fazendaHasUsuarioRepository,
      UsuarioRepository usuarioRepository,
      FazendaRepository fazendaRepository,
      PerfilRepository perfilRepository,
      @Value("${app.registro.perfil-id}") long perfilRegistroId) {
    this.fazendaHasUsuarioRepository = fazendaHasUsuarioRepository;
    this.usuarioRepository = usuarioRepository;
    this.fazendaRepository = fazendaRepository;
    this.perfilRepository = perfilRepository;
    this.perfilRegistroId = perfilRegistroId;
  }

  @Transactional(readOnly = true)
  public List<SolicitacaoEntradaResponseDto> listarPorProprietario(Long proprietarioUsuarioId) {
    List<SolicitacaoEntradaListagemProj> rows =
        fazendaHasUsuarioRepository.findSolicitacoesPorProprietario(proprietarioUsuarioId);
    List<SolicitacaoEntradaResponseDto> lista = new ArrayList<>();
    for (SolicitacaoEntradaListagemProj row : rows) {
      boolean ativo = Boolean.TRUE.equals(row.getAtivo());
      String status = ativo ? "APROVADO" : "SOLICITADO";
      PerfilDoUsuarioDto perfil =
          new PerfilDoUsuarioDto(row.getPerfilId(), row.getPerfilNome());
      lista.add(
          new SolicitacaoEntradaResponseDto(
              row.getUsuarioId(),
              row.getNomeUsuario(),
              row.getFazendaId(),
              row.getNomeFazenda(),
              perfil,
              status));
    }
    return lista;
  }

  @Transactional
  public void aprovar(AprovarEntradaRequestDto body) {
    Fazenda fazenda =
        fazendaRepository
            .findById(body.fazendaId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fazenda não encontrada"));

    if (!fazenda.getProprietario().getId().equals(body.proprietarioUsuarioId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissão para esta fazenda");
    }

    if (!fazendaHasUsuarioRepository.existsById(
        new FazendaUsuarioId(body.fazendaId(), body.usuarioId()))) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Utilizador não está vinculado a esta fazenda");
    }

    Usuario usuario =
        usuarioRepository
            .findById(body.usuarioId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilizador não encontrado"));

    if (usuario.isAtivo()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Utilizador já aprovado");
    }

    if (body.perfilId().equals(perfilRegistroId)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Selecione um perfil definitivo (não o perfil temporário de registo)");
    }

    Perfil perfil =
        perfilRepository
            .findById(body.perfilId())
            .filter(p -> !p.isExcluido() && p.isAtivo())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Perfil inválido"));

    usuario.setAtivo(true);
    usuario.setPerfil(perfil);
    usuarioRepository.save(usuario);
  }
}
