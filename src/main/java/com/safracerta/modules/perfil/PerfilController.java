package com.safracerta.modules.perfil;

import com.safracerta.modules.perfil.dto.PerfilRequestDto;
import com.safracerta.modules.perfil.dto.PerfilResponseDto;
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
@RequestMapping("/perfis")
public class PerfilController {

  private final PerfilService perfilService;

  public PerfilController(PerfilService perfilService) {
    this.perfilService = perfilService;
  }

  @GetMapping
  public List<PerfilResponseDto> listar() {
    return perfilService.listar();
  }

  @GetMapping("/{id}")
  public PerfilResponseDto buscar(@PathVariable Long id) {
    return perfilService.buscar(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PerfilResponseDto criar(@Valid @RequestBody PerfilRequestDto body) {
    return perfilService.criar(body);
  }

  @PutMapping("/{id}")
  public PerfilResponseDto atualizar(
      @PathVariable Long id, @Valid @RequestBody PerfilRequestDto body) {
    return perfilService.atualizar(id, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(@PathVariable Long id) {
    perfilService.excluir(id);
  }
}
