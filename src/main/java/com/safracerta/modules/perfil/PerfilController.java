package com.safracerta.modules.perfil;

import com.safracerta.modules.perfil.dto.PerfilRequestDto;
import com.safracerta.modules.perfil.dto.PerfilResponseDto;
import com.safracerta.modules.permissao.PermissaoMatrizService;
import com.safracerta.modules.permissao.dto.MatrizCategoriaDto;
import com.safracerta.modules.permissao.dto.PermissaoToggleRequestDto;
import com.safracerta.modules.permissao.dto.PerfilPermissaoResumoDto;
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
  private final PermissaoMatrizService permissaoMatrizService;

  public PerfilController(
      PerfilService perfilService, PermissaoMatrizService permissaoMatrizService) {
    this.perfilService = perfilService;
    this.permissaoMatrizService = permissaoMatrizService;
  }

  @GetMapping
  public List<PerfilResponseDto> listar() {
    return perfilService.listar();
  }

  @GetMapping("/resumo-com-permissoes")
  public List<PerfilPermissaoResumoDto> resumoComPermissoes() {
    return permissaoMatrizService.resumoPerfisComPermissoesAtivas();
  }

  @GetMapping("/{id}/matriz-permissoes")
  public List<MatrizCategoriaDto> matrizPermissoes(@PathVariable Long id) {
    return permissaoMatrizService.matrizPorPerfil(id);
  }

  @PutMapping("/{id}/permissoes/{permissaoId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void definirPermissao(
      @PathVariable Long id,
      @PathVariable Long permissaoId,
      @Valid @RequestBody PermissaoToggleRequestDto body) {
    permissaoMatrizService.definirPermissao(id, permissaoId, body);
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
