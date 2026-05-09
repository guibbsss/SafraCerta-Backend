package com.safracerta.modules.safra.dto;

import com.safracerta.modules.safra.SafraStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SafraRequestDto(
    @NotBlank String nome,
    Long talhaoId,
    @NotBlank String cultura,
    @NotNull SafraStatus status,
    @NotNull LocalDate dataPlantio,
    LocalDate dataColheitaPrevista,
    LocalDate dataColheitaReal,
    BigDecimal producaoEstimada,
    BigDecimal producaoReal) {}
