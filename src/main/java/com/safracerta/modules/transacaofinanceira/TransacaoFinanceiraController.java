package com.safracerta.modules.transacaofinanceira;

import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraExclusaoRequestDto;
import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraRequestDto;
import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraResponseDto;
import com.safracerta.modules.transacaofinanceira.dto.TransacaoFinanceiraResumoDto;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/transacoes-financeiras")
public class TransacaoFinanceiraController {

  private final TransacaoFinanceiraService service;

  public TransacaoFinanceiraController(TransacaoFinanceiraService service) {
    this.service = service;
  }

  @GetMapping
  public List<TransacaoFinanceiraResponseDto> listar(
      Authentication auth,
      @RequestParam(required = false) Long fazendaId,
      @RequestParam(required = false) TipoTransacaoFinanceira tipo,
      @RequestParam(required = false) StatusTransacaoFinanceira status,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dataInicio,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dataFim) {
    return service.listar(
        requireUsuarioId(auth), fazendaId, tipo, status, dataInicio, dataFim);
  }

  @GetMapping("/resumo")
  public TransacaoFinanceiraResumoDto resumo(
      Authentication auth,
      @RequestParam(required = false) Long fazendaId,
      @RequestParam(required = false) TipoTransacaoFinanceira tipo,
      @RequestParam(required = false) StatusTransacaoFinanceira status,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dataInicio,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate dataFim) {
    return service.resumo(
        requireUsuarioId(auth), fazendaId, tipo, status, dataInicio, dataFim);
  }

  /**
   * Usa padrão numérico em {id} para nunca conflitar com rotas literais como {@code /resumo}
   * (Spring pode mapear "resumo" para {@code /{id}} e gerar 400).
   */
  @GetMapping("/{id:\\d+}")
  public TransacaoFinanceiraResponseDto buscar(Authentication auth, @PathVariable Long id) {
    return service.buscar(id, requireUsuarioId(auth));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TransacaoFinanceiraResponseDto criar(
      Authentication auth, @Valid @RequestBody TransacaoFinanceiraRequestDto body) {
    return service.criar(requireUsuarioId(auth), body);
  }

  @PutMapping("/{id:\\d+}")
  public TransacaoFinanceiraResponseDto atualizar(
      Authentication auth,
      @PathVariable Long id,
      @Valid @RequestBody TransacaoFinanceiraRequestDto body) {
    return service.atualizar(requireUsuarioId(auth), id, body);
  }

  @DeleteMapping("/{id:\\d+}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(
      Authentication auth,
      @PathVariable Long id,
      @Valid @RequestBody TransacaoFinanceiraExclusaoRequestDto body) {
    service.excluir(id, body, requireUsuarioId(auth));
  }

  private static Long requireUsuarioId(Authentication auth) {
    if (auth == null || auth.getPrincipal() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação obrigatória");
    }
    return (Long) auth.getPrincipal();
  }
}
