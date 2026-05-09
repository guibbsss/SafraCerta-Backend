package com.safracerta.modules.talhao;

import com.safracerta.modules.talhao.dto.TalhaoRequestDto;
import com.safracerta.modules.talhao.dto.TalhaoResponseDto;
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
@RequestMapping("/talhoes")
public class TalhaoController {

  private final TalhaoService talhaoService;

  public TalhaoController(TalhaoService talhaoService) {
    this.talhaoService = talhaoService;
  }

  @GetMapping
  public List<TalhaoResponseDto> listar() {
    return talhaoService.listar();
  }

  @GetMapping("/{id}")
  public TalhaoResponseDto buscar(@PathVariable Long id) {
    return talhaoService.buscar(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TalhaoResponseDto criar(@Valid @RequestBody TalhaoRequestDto body) {
    return talhaoService.criar(body);
  }

  @PutMapping("/{id}")
  public TalhaoResponseDto atualizar(
      @PathVariable Long id, @Valid @RequestBody TalhaoRequestDto body) {
    return talhaoService.atualizar(id, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(@PathVariable Long id) {
    talhaoService.excluir(id);
  }
}
