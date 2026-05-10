package com.safracerta.modules.safra;

import com.safracerta.modules.safra.dto.SafraExclusaoRequestDto;
import com.safracerta.modules.safra.dto.SafraRequestDto;
import com.safracerta.modules.safra.dto.SafraResponseDto;
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
@RequestMapping("/safras")
public class SafraController {

  private final SafraService safraService;

  public SafraController(SafraService safraService) {
    this.safraService = safraService;
  }

  @GetMapping
  public List<SafraResponseDto> listar() {
    return safraService.listar();
  }

  @GetMapping("/{id}")
  public SafraResponseDto buscar(@PathVariable Long id) {
    return safraService.buscar(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public SafraResponseDto criar(@Valid @RequestBody SafraRequestDto body) {
    return safraService.criar(body);
  }

  @PutMapping("/{id}")
  public SafraResponseDto atualizar(
      @PathVariable Long id, @Valid @RequestBody SafraRequestDto body) {
    return safraService.atualizar(id, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(
      @PathVariable Long id,
      @Valid @RequestBody SafraExclusaoRequestDto body,
      Authentication auth) {
    if (auth == null || auth.getPrincipal() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação obrigatória");
    }
    Long usuarioId = (Long) auth.getPrincipal();
    safraService.excluir(id, body, usuarioId);
  }
}
