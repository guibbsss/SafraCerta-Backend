package com.safracerta.modules.fazenda;

import com.safracerta.modules.fazenda.dto.FazendaRequestDto;
import com.safracerta.modules.fazenda.dto.FazendaResponseDto;
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
@RequestMapping("/fazendas")
public class FazendaController {

  private final FazendaService fazendaService;

  public FazendaController(FazendaService fazendaService) {
    this.fazendaService = fazendaService;
  }

  @GetMapping
  public List<FazendaResponseDto> listar() {
    return fazendaService.listar();
  }

  @GetMapping("/{id}")
  public FazendaResponseDto buscar(@PathVariable Long id) {
    return fazendaService.buscar(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FazendaResponseDto criar(@Valid @RequestBody FazendaRequestDto body) {
    return fazendaService.criar(body);
  }

  @PutMapping("/{id}")
  public FazendaResponseDto atualizar(
      @PathVariable Long id, @Valid @RequestBody FazendaRequestDto body) {
    return fazendaService.atualizar(id, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(@PathVariable Long id) {
    fazendaService.excluir(id);
  }
}
