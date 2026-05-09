package com.safracerta.modules.safra.dto;

import com.safracerta.modules.safra.SafraStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SafraResponseDto(
    Long id,
    String nome,
    Long talhaoId,
    String talhaoNome,
    String cultura,
    SafraStatus status,
    LocalDate dataPlantio,
    LocalDate dataColheitaPrevista,
    LocalDate dataColheitaReal,
    BigDecimal producaoEstimada,
    BigDecimal producaoReal) {}
