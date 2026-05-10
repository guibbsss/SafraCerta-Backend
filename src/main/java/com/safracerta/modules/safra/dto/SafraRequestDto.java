package com.safracerta.modules.safra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.safracerta.modules.safra.SafraStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SafraRequestDto(
    @JsonProperty("nome") @NotBlank String nome,
    @JsonProperty("talhaoId") @NotNull Long talhaoId,
    @JsonProperty("cultura") @NotBlank String cultura,
    @JsonProperty("status") @NotNull SafraStatus status,
    @JsonProperty("dataPlantio") @NotNull LocalDate dataPlantio,
    @JsonProperty("dataColheitaPrevista") LocalDate dataColheitaPrevista,
    @JsonProperty("dataColheitaReal") LocalDate dataColheitaReal,
    @JsonProperty("producaoEstimada") BigDecimal producaoEstimada,
    @JsonProperty("producaoReal") BigDecimal producaoReal,
    /** Apenas POST /safras; ignorado em PUT. Null ou vazio = sem saídas de estoque. */
    @JsonProperty("consumosInsumo") List<@Valid SafraConsumoInsumoDto> consumosInsumo) {}
