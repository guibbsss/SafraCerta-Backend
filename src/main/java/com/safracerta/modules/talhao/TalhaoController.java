package com.safracerta.modules.talhao;

import com.safracerta.modules.talhao.dto.TalhaoRequestDto;
import com.safracerta.modules.talhao.dto.TalhaoResponseDto;
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
@RequestMapping("/talhoes")
public class TalhaoController {

  private final TalhaoService talhaoService;

  public TalhaoController(TalhaoService talhaoService) {
    this.talhaoService = talhaoService;
  }

  @GetMapping("/fazenda/{fazendaId}")
  public List<TalhaoResponseDto> listarPorFazenda(
      Authentication auth, @PathVariable Long fazendaId) {
    return talhaoService.listarPorFazenda(requireUsuarioId(auth), fazendaId);
  }

  @GetMapping
  public List<TalhaoResponseDto> listar(Authentication auth) {
    return talhaoService.listar(requireUsuarioId(auth));
  }

  @GetMapping("/{id}")
  public TalhaoResponseDto buscar(Authentication auth, @PathVariable Long id) {
    return talhaoService.buscar(id, requireUsuarioId(auth));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TalhaoResponseDto criar(Authentication auth, @Valid @RequestBody TalhaoRequestDto body) {
    return talhaoService.criar(requireUsuarioId(auth), body);
  }

  @PutMapping("/{id}")
  public TalhaoResponseDto atualizar(
      Authentication auth, @PathVariable Long id, @Valid @RequestBody TalhaoRequestDto body) {
    return talhaoService.atualizar(id, requireUsuarioId(auth), body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(Authentication auth, @PathVariable Long id) {
    talhaoService.excluir(id, requireUsuarioId(auth));
  }

  private static Long requireUsuarioId(Authentication auth) {
    if (auth == null || auth.getPrincipal() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação obrigatória");
    }
    return (Long) auth.getPrincipal();
  }
}
