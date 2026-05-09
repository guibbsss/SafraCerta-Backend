package com.safracerta.modules.safra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record SafraRequestDto(
    @NotNull Long talhaoId,
    @NotBlank String cultura,
    @NotNull LocalDate dataPlantio,
    LocalDate dataColheita) {}
