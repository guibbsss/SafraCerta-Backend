package com.safracerta.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequestDto(
    @NotBlank @Email String email,
    @NotBlank String senha,
    @NotBlank String nome,
    @NotNull Long perfilId) {}
