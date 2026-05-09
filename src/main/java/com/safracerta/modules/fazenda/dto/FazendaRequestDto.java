package com.safracerta.modules.fazenda.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record FazendaRequestDto(
    @NotBlank String nome, String localizacao, BigDecimal areaTotal, String proprietario) {}
