package com.safracerta.modules.safra;

import com.safracerta.modules.fazenda.FazendaUsuarioEscopoService;
import com.safracerta.modules.movimentacaoestoque.MovimentacaoEstoqueRepository;
import com.safracerta.modules.movimentacaoestoque.MovimentacaoEstoqueService;
import com.safracerta.modules.movimentacaoestoque.TipoMovimentacaoEstoque;
import com.safracerta.modules.safra.dto.SafraConsumoInsumoDto;
import com.safracerta.modules.safra.dto.SafraConsumoRespostaDto;
import com.safracerta.modules.safra.dto.SafraExclusaoRequestDto;
import com.safracerta.modules.safra.dto.SafraRequestDto;
import com.safracerta.modules.safra.dto.SafraResponseDto;
import com.safracerta.modules.talhao.Talhao;
import com.safracerta.modules.talhao.TalhaoRepository;
import com.safracerta.modules.user.Usuario;
import com.safracerta.modules.user.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SafraService {

  private static final Logger log = LoggerFactory.getLogger(SafraService.class);

  private final SafraRepository safraRepository;
  private final TalhaoRepository talhaoRepository;
  private final UsuarioRepository usuarioRepository;
  private final MovimentacaoEstoqueService movimentacaoEstoqueService;
  private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
  private final FazendaUsuarioEscopoService fazendaUsuarioEscopoService;

  public SafraService(
      SafraRepository safraRepository,
      TalhaoRepository talhaoRepository,
      UsuarioRepository usuarioRepository,
      MovimentacaoEstoqueService movimentacaoEstoqueService,
      MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
      FazendaUsuarioEscopoService fazendaUsuarioEscopoService) {
    this.safraRepository = safraRepository;
    this.talhaoRepository = talhaoRepository;
    this.usuarioRepository = usuarioRepository;
    this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
    this.fazendaUsuarioEscopoService = fazendaUsuarioEscopoService;
  }

  @Transactional(readOnly = true)
  public List<SafraResponseDto> listar(Long usuarioId) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      return List.of();
    }
    return safraRepository
        .findByExcluidoFalseAndTalhao_Fazenda_IdInOrderByDataPlantioDesc(fazendaIds).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public SafraResponseDto buscar(Long id, Long usuarioId) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      throw notFound();
    }
    return safraRepository
        .findByIdAndExcluidoFalseAndTalhao_Fazenda_IdIn(id, fazendaIds)
        .map(this::toResponse)
        .orElseThrow(this::notFound);
  }

  @Transactional
  public SafraResponseDto criar(Long usuarioId, SafraRequestDto dto) {
    log.info(
        "POST /safras: consumosInsumo {} itens",
        dto.consumosInsumo() == null ? "null" : String.valueOf(dto.consumosInsumo().size()));

    Talhao talhao = resolveTalhaoParaUsuario(dto.talhaoId(), usuarioId);
    Safra s = new Safra();
    s.setTalhao(talhao);
    apply(s, dto);
    Safra salvo = safraRepository.save(s);

    List<SafraConsumoInsumoDto> consumos = dto.consumosInsumo();
    if (consumos != null && !consumos.isEmpty()) {
      validarConsumosSemDuplicados(consumos);
      Long fazendaId = talhao.getFazenda().getId();
      LocalDateTime agora = LocalDateTime.now();
      log.info(
          "Criar safra id={}: a registar {} saída(s) de estoque (fazendaId={})",
          salvo.getId(),
          consumos.size(),
          fazendaId);
      for (SafraConsumoInsumoDto c : consumos) {
        movimentacaoEstoqueService.registrarSaidaParaSafra(
            fazendaId, c.insumoId(), c.quantidade(), salvo, agora);
      }
    } else {
      log.debug(
          "Criar safra id={}: sem consumosInsumo no pedido (nenhuma saída de stock)", salvo.getId());
    }

    return toResponse(salvo);
  }

  @Transactional
  public SafraResponseDto atualizar(Long id, Long usuarioId, SafraRequestDto dto) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      throw notFound();
    }
    Safra s =
        safraRepository
            .findByIdAndExcluidoFalseAndTalhao_Fazenda_IdIn(id, fazendaIds)
            .orElseThrow(this::notFound);
    s.setTalhao(resolveTalhaoParaUsuario(dto.talhaoId(), usuarioId));
    apply(s, dto);
    return toResponse(safraRepository.save(s));
  }

  private Talhao resolveTalhaoParaUsuario(Long talhaoId, Long usuarioId) {
    if (talhaoId == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Talhão é obrigatório");
    }
    Talhao talhao = talhaoRepository.findById(talhaoId).orElseThrow(this::talhaoNotFound);
    fazendaUsuarioEscopoService.garantirEscritaNaFazenda(
        usuarioId, talhao.getFazenda().getId());
    return talhao;
  }

  private static void validarConsumosSemDuplicados(List<SafraConsumoInsumoDto> consumos) {
    Set<Long> ids = new HashSet<>();
    for (SafraConsumoInsumoDto c : consumos) {
      if (!ids.add(c.insumoId())) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Lista de consumos não pode repetir o mesmo insumo");
      }
    }
  }

  @Transactional
  public void excluir(Long id, SafraExclusaoRequestDto dto, Long usuarioId) {
    List<Long> fazendaIds = fazendaUsuarioEscopoService.idsFazendasAcessiveis(usuarioId);
    if (fazendaIds.isEmpty()) {
      throw notFound();
    }
    Safra s =
        safraRepository
            .findByIdAndExcluidoFalseAndTalhao_Fazenda_IdIn(id, fazendaIds)
            .orElseThrow(this::notFound);
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
    List<SafraConsumoRespostaDto> consumos =
        s.getId() != null ? consumosRespostaParaSafra(s.getId()) : List.of();
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
        s.getProducaoReal(),
        consumos);
  }

  private List<SafraConsumoRespostaDto> consumosRespostaParaSafra(Long safraId) {
    return movimentacaoEstoqueRepository.findBySafra_IdOrderByIdAsc(safraId).stream()
        .filter(m -> m.getTipoMovimentacao() == TipoMovimentacaoEstoque.SAIDA)
        .map(
            m ->
                new SafraConsumoRespostaDto(
                    m.getId(),
                    m.getInsumo().getId(),
                    m.getInsumo().getNome(),
                    m.getQuantidade()))
        .toList();
  }

  private ResponseStatusException notFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Safra não encontrada");
  }

  private ResponseStatusException talhaoNotFound() {
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Talhão inválido");
  }
}
