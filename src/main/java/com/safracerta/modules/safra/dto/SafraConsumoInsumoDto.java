package com.safracerta.modules.safra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record SafraConsumoInsumoDto(
    @JsonProperty("insumoId") @NotNull Long insumoId,
    @JsonProperty("quantidade") @NotNull @Positive BigDecimal quantidade) {}
