package com.safracerta.modules.safra.dto;

import java.math.BigDecimal;

public record SafraConsumoRespostaDto(
    Long movimentacaoId, Long insumoId, String insumoNome, BigDecimal quantidade) {}
