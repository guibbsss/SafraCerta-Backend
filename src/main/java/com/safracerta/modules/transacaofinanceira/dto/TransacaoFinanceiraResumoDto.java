package com.safracerta.modules.transacaofinanceira.dto;

import java.math.BigDecimal;

public record TransacaoFinanceiraResumoDto(
    BigDecimal totalReceitas,
    BigDecimal totalDespesas,
    BigDecimal saldo,
    long quantidadeReceitas,
    long quantidadeDespesas,
    long quantidadePendentes,
    long quantidadeAtrasadas) {}
