package com.safracerta.modules.auth;

import com.safracerta.modules.auth.dto.LoginRequestDto;
import com.safracerta.modules.auth.dto.LoginResponseDto;
import com.safracerta.modules.auth.dto.RegistroUsuarioRequestDto;
import com.safracerta.modules.auth.dto.RegistroUsuarioResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Login e cadastro")
public class AuthController {

  private final AuthApplicationService authApplicationService;

  public AuthController(AuthApplicationService authApplicationService) {
    this.authApplicationService = authApplicationService;
  }

  @PostMapping("/login")
  public LoginResponseDto login(@Valid @RequestBody LoginRequestDto body) {
    return authApplicationService.login(body);
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public RegistroUsuarioResponseDto register(@Valid @RequestBody RegistroUsuarioRequestDto body) {
    return authApplicationService.registrar(body);
  }
}
