package com.safracerta.controller;

import com.safracerta.dto.LoginRequestDto;
import com.safracerta.dto.LoginResponseDto;
import com.safracerta.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

  @PostMapping("/login")
  public LoginResponseDto login(@Valid @RequestBody LoginRequestDto body) {
    UserDto user = new UserDto(1L, body.email(), "Usuário dev");
    return new LoginResponseDto("dev-token", user);
  }
}
