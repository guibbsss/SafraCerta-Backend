package com.safracerta.modules.insumo.dto;

import java.math.BigDecimal;

public record InsumoResponseDto(
    Long id,
    Long fazendaId,
    String fazendaNome,
    String nome,
    String categoria,
    BigDecimal quantidadeAtual,
    String unidadeMedida,
    BigDecimal valorUnitarioReferencia,
    BigDecimal valorTotalEstimado) {}
