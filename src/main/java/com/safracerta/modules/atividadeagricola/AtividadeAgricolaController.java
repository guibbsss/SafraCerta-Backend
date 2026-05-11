package com.safracerta.modules.atividadeagricola;

import com.safracerta.modules.atividadeagricola.dto.AtividadeAgricolaRequestDto;
import com.safracerta.modules.atividadeagricola.dto.AtividadeAgricolaResponseDto;
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
@RequestMapping("/atividades-agricolas")
public class AtividadeAgricolaController {

  private final AtividadeAgricolaService atividadeAgricolaService;

  public AtividadeAgricolaController(AtividadeAgricolaService atividadeAgricolaService) {
    this.atividadeAgricolaService = atividadeAgricolaService;
  }

  @GetMapping("/talhao/{talhaoId}")
  public List<AtividadeAgricolaResponseDto> listarPorTalhao(
      Authentication auth, @PathVariable Long talhaoId) {
    return atividadeAgricolaService.listarPorTalhao(requireUsuarioId(auth), talhaoId);
  }

  @GetMapping
  public List<AtividadeAgricolaResponseDto> listar(Authentication auth) {
    return atividadeAgricolaService.listar(requireUsuarioId(auth));
  }

  @GetMapping("/{id}")
  public AtividadeAgricolaResponseDto buscar(Authentication auth, @PathVariable Long id) {
    return atividadeAgricolaService.buscar(id, requireUsuarioId(auth));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AtividadeAgricolaResponseDto criar(
      Authentication auth, @Valid @RequestBody AtividadeAgricolaRequestDto body) {
    return atividadeAgricolaService.criar(requireUsuarioId(auth), body);
  }

  @PutMapping("/{id}")
  public AtividadeAgricolaResponseDto atualizar(
      Authentication auth,
      @PathVariable Long id,
      @Valid @RequestBody AtividadeAgricolaRequestDto body) {
    return atividadeAgricolaService.atualizar(requireUsuarioId(auth), id, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(Authentication auth, @PathVariable Long id) {
    atividadeAgricolaService.excluir(requireUsuarioId(auth), id);
  }

  private static Long requireUsuarioId(Authentication auth) {
    if (auth == null || auth.getPrincipal() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação obrigatória");
    }
    return (Long) auth.getPrincipal();
  }
}
