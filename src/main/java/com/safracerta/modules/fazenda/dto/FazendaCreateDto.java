package com.safracerta.modules.fazenda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record FazendaCreateDto(
    @NotBlank String nome,
    String localizacao,
    BigDecimal areaTotal,
    @NotNull Long proprietarioId) {}
