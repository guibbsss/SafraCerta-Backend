package com.safracerta.modules.transacaofinanceira.dto;

import com.safracerta.modules.transacaofinanceira.TipoTransacaoFinanceira;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoFinanceiraResponseDto(
    Long id,
    Long fazendaId,
    TipoTransacaoFinanceira tipo,
    BigDecimal valor,
    LocalDate dataTransacao,
    String categoria,
    String origem) {}
