package com.safracerta.modules.auth;

import com.safracerta.modules.auth.dto.LoginRequestDto;
import com.safracerta.modules.auth.dto.LoginResponseDto;
import com.safracerta.modules.auth.dto.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Login e cadastro (stub de desenvolvimento)")
public class AuthController {

  @PostMapping("/login")
  public LoginResponseDto login(@Valid @RequestBody LoginRequestDto body) {
    UserDto user = new UserDto(1L, body.email(), "Usuário dev");
    return new LoginResponseDto("dev-token", user);
  }
}
