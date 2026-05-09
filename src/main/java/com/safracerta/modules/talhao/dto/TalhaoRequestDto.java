package com.safracerta.modules.talhao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TalhaoRequestDto(
    @NotNull Long fazendaId,
    @NotBlank String nome,
    BigDecimal areaHectares,
    String tipoCultivo) {}
