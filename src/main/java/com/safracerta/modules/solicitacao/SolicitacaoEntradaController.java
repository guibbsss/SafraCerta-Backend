package com.safracerta.modules.solicitacao;

import com.safracerta.modules.solicitacao.dto.AprovarEntradaRequestDto;
import com.safracerta.modules.solicitacao.dto.SolicitacaoEntradaResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/solicitacoes-entrada")
@Tag(name = "Solicitações de entrada", description = "Pedidos de registo nas fazendas do proprietário")
public class SolicitacaoEntradaController {

  private final SolicitacaoEntradaService solicitacaoEntradaService;

  public SolicitacaoEntradaController(SolicitacaoEntradaService solicitacaoEntradaService) {
    this.solicitacaoEntradaService = solicitacaoEntradaService;
  }

  @GetMapping
  public List<SolicitacaoEntradaResponseDto> listar(
      @RequestParam("proprietarioUsuarioId") Long proprietarioUsuarioId) {
    return solicitacaoEntradaService.listarPorProprietario(proprietarioUsuarioId);
  }

  @PostMapping("/aprovar")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void aprovar(@Valid @RequestBody AprovarEntradaRequestDto body) {
    solicitacaoEntradaService.aprovar(body);
  }
}
