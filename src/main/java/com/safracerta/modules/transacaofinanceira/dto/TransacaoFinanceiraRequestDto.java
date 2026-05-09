package com.safracerta.modules.transacaofinanceira.dto;

import com.safracerta.modules.transacaofinanceira.TipoTransacaoFinanceira;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoFinanceiraRequestDto(
    @NotNull Long fazendaId,
    @NotNull TipoTransacaoFinanceira tipo,
    @NotNull BigDecimal valor,
    @NotNull LocalDate dataTransacao,
    String categoria,
    String origem) {}
