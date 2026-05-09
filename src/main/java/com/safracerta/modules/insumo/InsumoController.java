package com.safracerta.modules.insumo;

import com.safracerta.modules.insumo.dto.InsumoRequestDto;
import com.safracerta.modules.insumo.dto.InsumoResponseDto;
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
@RequestMapping("/insumos")
public class InsumoController {

  private final InsumoService insumoService;

  public InsumoController(InsumoService insumoService) {
    this.insumoService = insumoService;
  }

  @GetMapping
  public List<InsumoResponseDto> listar() {
    return insumoService.listar();
  }

  @GetMapping("/{id}")
  public InsumoResponseDto buscar(@PathVariable Long id) {
    return insumoService.buscar(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public InsumoResponseDto criar(@Valid @RequestBody InsumoRequestDto body) {
    return insumoService.criar(body);
  }

  @PutMapping("/{id}")
  public InsumoResponseDto atualizar(
      @PathVariable Long id, @Valid @RequestBody InsumoRequestDto body) {
    return insumoService.atualizar(id, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(@PathVariable Long id) {
    insumoService.excluir(id);
  }
}
