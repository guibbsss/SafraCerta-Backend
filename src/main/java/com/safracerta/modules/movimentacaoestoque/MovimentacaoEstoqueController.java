package com.safracerta.modules.movimentacaoestoque;

import com.safracerta.modules.movimentacaoestoque.dto.MovimentacaoEstoqueRequestDto;
import com.safracerta.modules.movimentacaoestoque.dto.MovimentacaoEstoqueResponseDto;
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
@RequestMapping("/movimentacoes-estoque")
public class MovimentacaoEstoqueController {

  private final MovimentacaoEstoqueService movimentacaoEstoqueService;

  public MovimentacaoEstoqueController(MovimentacaoEstoqueService movimentacaoEstoqueService) {
    this.movimentacaoEstoqueService = movimentacaoEstoqueService;
  }

  @GetMapping
  public List<MovimentacaoEstoqueResponseDto> listar(Authentication auth) {
    return movimentacaoEstoqueService.listar(requireUsuarioId(auth));
  }

  @GetMapping("/tipo/{tipo}")
  public List<MovimentacaoEstoqueResponseDto> listarPorTipo(
      Authentication auth, @PathVariable TipoMovimentacaoEstoque tipo) {
    return movimentacaoEstoqueService.listarPorTipo(requireUsuarioId(auth), tipo);
  }

  @GetMapping("/{id}")
  public MovimentacaoEstoqueResponseDto buscar(Authentication auth, @PathVariable Long id) {
    return movimentacaoEstoqueService.buscar(id, requireUsuarioId(auth));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public MovimentacaoEstoqueResponseDto criar(
      Authentication auth, @Valid @RequestBody MovimentacaoEstoqueRequestDto body) {
    return movimentacaoEstoqueService.criar(requireUsuarioId(auth), body);
  }

  @PutMapping("/{id}")
  public MovimentacaoEstoqueResponseDto atualizar(
      Authentication auth,
      @PathVariable Long id,
      @Valid @RequestBody MovimentacaoEstoqueRequestDto body) {
    return movimentacaoEstoqueService.atualizar(id, requireUsuarioId(auth), body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(Authentication auth, @PathVariable Long id) {
    movimentacaoEstoqueService.excluir(id, requireUsuarioId(auth));
  }

  private static Long requireUsuarioId(Authentication auth) {
    if (auth == null || auth.getPrincipal() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação obrigatória");
    }
    return (Long) auth.getPrincipal();
  }
}
