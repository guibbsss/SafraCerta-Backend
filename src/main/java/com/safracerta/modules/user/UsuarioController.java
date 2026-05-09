package com.safracerta.modules.user;

import com.safracerta.modules.user.dto.UsuarioRequestDto;
import com.safracerta.modules.user.dto.UsuarioResponseDto;
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
@RequestMapping("/usuarios")
public class UsuarioController {

  private final UsuarioService usuarioService;

  public UsuarioController(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  @GetMapping
  public List<UsuarioResponseDto> listar() {
    return usuarioService.listar();
  }

  @GetMapping("/{id}")
  public UsuarioResponseDto buscar(@PathVariable Long id) {
    return usuarioService.buscar(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UsuarioResponseDto criar(@Valid @RequestBody UsuarioRequestDto body) {
    return usuarioService.criar(body);
  }

  @PutMapping("/{id}")
  public UsuarioResponseDto atualizar(
      @PathVariable Long id, @Valid @RequestBody UsuarioRequestDto body) {
    return usuarioService.atualizar(id, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void excluir(@PathVariable Long id) {
    usuarioService.excluir(id);
  }
}
