package com.safracerta.modules.perfil.dto;

import jakarta.validation.constraints.NotBlank;

public record PerfilRequestDto(@NotBlank String nome) {}
