package com.safracerta.modules.safra;

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

  public SafraService(
      SafraRepository safraRepository,
      TalhaoRepository talhaoRepository,
      UsuarioRepository usuarioRepository,
      MovimentacaoEstoqueService movimentacaoEstoqueService,
      MovimentacaoEstoqueRepository movimentacaoEstoqueRepository) {
    this.safraRepository = safraRepository;
    this.talhaoRepository = talhaoRepository;
    this.usuarioRepository = usuarioRepository;
    this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
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
    log.info(
        "POST /safras: consumosInsumo {} itens",
        dto.consumosInsumo() == null ? "null" : String.valueOf(dto.consumosInsumo().size()));

    Talhao talhao = resolveTalhao(dto.talhaoId());
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
      log.debug("Criar safra id={}: sem consumosInsumo no pedido (nenhuma saída de stock)", salvo.getId());
    }

    return toResponse(salvo);
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
