package com.safracerta.modules.movimentacaoestoque;

import com.safracerta.modules.movimentacaoestoque.dto.MovimentacaoEstoqueRequestDto;
import com.safracerta.modules.movimentacaoestoque.dto.MovimentacaoEstoqueResponseDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movimentacoes-estoque")
public class MovimentacaoEstoqueController {

  private final MovimentacaoEstoqueService movimentacaoEstoqueService;

  public MovimentacaoEstoqueController(MovimentacaoEstoqueService movimentacaoEstoqueService) {
    this.movimentacaoEstoqueService = movimentacaoEstoqueService;
  }

  @GetMapping
  public List<MovimentacaoEstoqueResponseDto> listar() {
    return movimentacaoEstoqueService.listar();
  }

  @GetMapping("/tipo/{tipo}")
  public List<MovimentacaoEstoqueResponseDto> listarPorTipo(
      @PathVariable TipoMovimentacaoEstoque tipo) {
    return movimentacaoEstoqueService.listarPorTipo(tipo);
  }

  @GetMapping("/{id}")
  public MovimentacaoEstoqueResponseDto buscar(@PathVariable Long id) {
    return movimentacaoEstoqueService.buscar(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public MovimentacaoEstoqueResponseDto criar(
      @Valid @RequestBody MovimentacaoEstoqueRequestDto body) {
    return movimentacaoEstoqueService.criar(body);
  }

  @PutMapping("/{id}")
  public MovimentacaoEstoqueResponseDto atualizar(
      @PathVariable Long id, @Valid @RequestBody MovimentacaoEstoqueRequestDto body) {
    return movimentacaoEstoqueService.atualizar(id, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(@PathVariable Long id) {
    movimentacaoEstoqueService.excluir(id);
  }
}
