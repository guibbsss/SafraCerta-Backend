package com.safracerta.modules.insumo;

import com.safracerta.modules.insumo.dto.InsumoRequestDto;
import com.safracerta.modules.insumo.dto.InsumoResponseDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/insumos")
public class InsumoController {

  private final InsumoService insumoService;

  public InsumoController(InsumoService insumoService) {
    this.insumoService = insumoService;
  }

  /** Antes de `/{id}` para não capturar o literal com um path variable genérico. */
  @GetMapping("/fazenda/{fazendaId}")
  public List<InsumoResponseDto> listarPorFazenda(
      Authentication auth, @PathVariable Long fazendaId) {
    return insumoService.listarPorFazenda(requireUsuarioId(auth), fazendaId);
  }

  @GetMapping
  public List<InsumoResponseDto> listar(Authentication auth) {
    return insumoService.listar(requireUsuarioId(auth));
  }

  @GetMapping("/{id}")
  public InsumoResponseDto buscar(Authentication auth, @PathVariable Long id) {
    return insumoService.buscar(id, requireUsuarioId(auth));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public InsumoResponseDto criar(Authentication auth, @Valid @RequestBody InsumoRequestDto body) {
    return insumoService.criar(requireUsuarioId(auth), body);
  }

  @PutMapping("/{id}")
  public InsumoResponseDto atualizar(
      Authentication auth,
      @PathVariable Long id,
      @Valid @RequestBody InsumoRequestDto body) {
    return insumoService.atualizar(requireUsuarioId(auth), id, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(Authentication auth, @PathVariable Long id) {
    insumoService.excluir(requireUsuarioId(auth), id);
  }

  private static Long requireUsuarioId(Authentication auth) {
    if (auth == null || auth.getPrincipal() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação obrigatória");
    }
    return (Long) auth.getPrincipal();
  }
}
