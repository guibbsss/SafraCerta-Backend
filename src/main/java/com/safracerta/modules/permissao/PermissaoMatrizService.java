package com.safracerta.modules.permissao;

import com.safracerta.modules.perfil.Perfil;
import com.safracerta.modules.perfil.PerfilRepository;
import com.safracerta.modules.permissao.dto.MatrizCategoriaDto;
import com.safracerta.modules.permissao.dto.MatrizPermissaoItemDto;
import com.safracerta.modules.permissao.dto.PermissaoToggleRequestDto;
import com.safracerta.modules.permissao.dto.PerfilPermissaoResumoDto;
import com.safracerta.modules.permissao.dto.ResumoCategoriaPermissoesDto;
import com.safracerta.modules.permissao.dto.ResumoPermissaoItemDto;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PermissaoMatrizService {

  private final PerfilRepository perfilRepository;
  private final PermissaoRepository permissaoRepository;
  private final PerfilHasPermissaoRepository perfilHasPermissaoRepository;

  public PermissaoMatrizService(
      PerfilRepository perfilRepository,
      PermissaoRepository permissaoRepository,
      PerfilHasPermissaoRepository perfilHasPermissaoRepository) {
    this.perfilRepository = perfilRepository;
    this.permissaoRepository = permissaoRepository;
    this.perfilHasPermissaoRepository = perfilHasPermissaoRepository;
  }

  @Transactional(readOnly = true)
  public List<MatrizCategoriaDto> matrizPorPerfil(Long perfilId) {
    Perfil perfil = carregarPerfilAdministracao(perfilId);
    List<Permissao> todas = permissaoRepository.findAllVisiveisOrderByCategoriaENome();
    Map<Long, Boolean> concedidaPorPermissaoId = new LinkedHashMap<>();
    for (PerfilHasPermissao h : perfilHasPermissaoRepository.findAllByIdPerfilIdAndExcluidoFalse(
        perfil.getId())) {
      concedidaPorPermissaoId.put(
          h.getId().getPermissaoId(), h.isAtivo() && !h.isExcluido());
    }

    List<MatrizCategoriaDto> resultado = new ArrayList<>();
    Long catAtual = null;
    List<MatrizPermissaoItemDto> blocoAtual = null;

    for (Permissao p : todas) {
      Long cid = p.getCategoria().getId();
      if (!Objects.equals(catAtual, cid)) {
        catAtual = cid;
        blocoAtual = new ArrayList<>();
        resultado.add(
            new MatrizCategoriaDto(
                cid, p.getCategoria().getNome(), blocoAtual));
      }
      boolean concedida =
          Boolean.TRUE.equals(concedidaPorPermissaoId.getOrDefault(p.getId(), false));
      assert blocoAtual != null;
      blocoAtual.add(
          new MatrizPermissaoItemDto(
              p.getId(), p.getNome(), p.getDescricao(), concedida));
    }

    return resultado;
  }

  @Transactional
  public void definirPermissao(
      Long perfilId, Long permissaoId, PermissaoToggleRequestDto body) {
    Perfil perfil = carregarPerfilAdministracao(perfilId);
    Permissao permissao =
        permissaoRepository
            .findById(permissaoId)
            .filter(p -> !p.isExcluido() && p.isAtivo())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permissão inválida"));

    boolean conceder = Boolean.TRUE.equals(body.ativo());
    Optional<PerfilHasPermissao> existente =
        perfilHasPermissaoRepository.findByIdPerfilIdAndIdPermissaoId(perfil.getId(), permissao.getId());

    if (conceder) {
      if (existente.isPresent()) {
        PerfilHasPermissao h = existente.get();
        h.setAtivo(true);
        h.setExcluido(false);
        perfilHasPermissaoRepository.save(h);
      } else {
        PerfilHasPermissao h = new PerfilHasPermissao();
        PerfilPermissaoId id = new PerfilPermissaoId(perfil.getId(), permissao.getId());
        h.setId(id);
        h.setPerfil(perfil);
        h.setPermissao(permissao);
        h.setAtivo(true);
        h.setExcluido(false);
        perfilHasPermissaoRepository.save(h);
      }
    } else {
      existente.ifPresent(
          h -> {
            h.setAtivo(false);
            perfilHasPermissaoRepository.save(h);
          });
    }
  }

  @Transactional(readOnly = true)
  public List<PerfilPermissaoResumoDto> resumoPerfisComPermissoesAtivas() {
    List<Perfil> perfis = perfilRepository.findAllByExcluidoFalseOrderByNomeAsc();
    List<PerfilPermissaoResumoDto> lista = new ArrayList<>();
    for (Perfil perfil : perfis) {
      List<PerfilHasPermissao> concedidas =
          perfilHasPermissaoRepository.findConcedidasVisiveisPorPerfil(perfil.getId());
      Map<String, List<ResumoPermissaoItemDto>> porCategoria = new LinkedHashMap<>();
      for (PerfilHasPermissao h : concedidas) {
        Permissao p = h.getPermissao();
        String nomeCat = p.getCategoria().getNome();
        porCategoria
            .computeIfAbsent(nomeCat, k -> new ArrayList<>())
            .add(new ResumoPermissaoItemDto(p.getNome(), p.getDescricao()));
      }
      List<ResumoCategoriaPermissoesDto> categorias =
          porCategoria.entrySet().stream()
              .map(e -> new ResumoCategoriaPermissoesDto(e.getKey(), e.getValue()))
              .collect(Collectors.toList());
      lista.add(
          new PerfilPermissaoResumoDto(perfil.getId(), perfil.getNome(), categorias));
    }
    return lista;
  }

  private Perfil carregarPerfilAdministracao(Long perfilId) {
    return perfilRepository
        .findById(perfilId)
        .filter(p -> !p.isExcluido())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado"));
  }
}
