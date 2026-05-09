package com.safracerta.modules.fazenda.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record FazendaUpdateDto(
    @NotBlank String nome, String localizacao, BigDecimal areaTotal) {}
