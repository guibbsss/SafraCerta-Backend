package com.safracerta.modules.safra.dto;

import java.time.LocalDate;

public record SafraResponseDto(
    Long id, Long talhaoId, String cultura, LocalDate dataPlantio, LocalDate dataColheita) {}
