package com.safracerta.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroUsuarioRequestDto(
    @NotBlank @Size(max = 200) String nome,
    @NotBlank @Email @Size(max = 180) String email,
    @NotBlank @Size(min = 6, max = 255) String senha,
    @NotBlank @Size(min = 10, max = 15) String codigoAcesso) {}
