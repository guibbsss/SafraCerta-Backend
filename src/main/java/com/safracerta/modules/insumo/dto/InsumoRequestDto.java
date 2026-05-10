package com.safracerta.modules.insumo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record InsumoRequestDto(
    @NotNull Long fazendaId,
    @NotBlank String nome,
    String categoria,
    @NotNull BigDecimal quantidadeAtual,
    @NotBlank String unidadeMedida,
    BigDecimal valorUnitarioReferencia) {}
