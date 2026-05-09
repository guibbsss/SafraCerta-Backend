package com.safracerta.modules.transacaofinanceira;

import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraRequestDto;
import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraResponseDto;
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
@RequestMapping("/transacoes-financeiras")
public class TransacaoFinanceiraController {

  private final TransacaoFinanceiraService transacaoFinanceiraService;

  public TransacaoFinanceiraController(TransacaoFinanceiraService transacaoFinanceiraService) {
    this.transacaoFinanceiraService = transacaoFinanceiraService;
  }

  @GetMapping
  public List<TransacaoFinanceiraResponseDto> listar() {
    return transacaoFinanceiraService.listar();
  }

  @GetMapping("/{id}")
  public TransacaoFinanceiraResponseDto buscar(@PathVariable Long id) {
    return transacaoFinanceiraService.buscar(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TransacaoFinanceiraResponseDto criar(
      @Valid @RequestBody TransacaoFinanceiraRequestDto body) {
    return transacaoFinanceiraService.criar(body);
  }

  @PutMapping("/{id}")
  public TransacaoFinanceiraResponseDto atualizar(
      @PathVariable Long id, @Valid @RequestBody TransacaoFinanceiraRequestDto body) {
    return transacaoFinanceiraService.atualizar(id, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(@PathVariable Long id) {
    transacaoFinanceiraService.excluir(id);
  }
}
