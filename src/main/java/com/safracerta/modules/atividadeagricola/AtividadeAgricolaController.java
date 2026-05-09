package com.safracerta.modules.atividadeagricola;

import com.safracerta.modules.atividadeagricola.dto.AtividadeAgricolaRequestDto;
import com.safracerta.modules.atividadeagricola.dto.AtividadeAgricolaResponseDto;
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
@RequestMapping("/atividades-agricolas")
public class AtividadeAgricolaController {

  private final AtividadeAgricolaService atividadeAgricolaService;

  public AtividadeAgricolaController(AtividadeAgricolaService atividadeAgricolaService) {
    this.atividadeAgricolaService = atividadeAgricolaService;
  }

  @GetMapping
  public List<AtividadeAgricolaResponseDto> listar() {
    return atividadeAgricolaService.listar();
  }

  @GetMapping("/{id}")
  public AtividadeAgricolaResponseDto buscar(@PathVariable Long id) {
    return atividadeAgricolaService.buscar(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AtividadeAgricolaResponseDto criar(@Valid @RequestBody AtividadeAgricolaRequestDto body) {
    return atividadeAgricolaService.criar(body);
  }

  @PutMapping("/{id}")
  public AtividadeAgricolaResponseDto atualizar(
      @PathVariable Long id, @Valid @RequestBody AtividadeAgricolaRequestDto body) {
    return atividadeAgricolaService.atualizar(id, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(@PathVariable Long id) {
    atividadeAgricolaService.excluir(id);
  }
}
