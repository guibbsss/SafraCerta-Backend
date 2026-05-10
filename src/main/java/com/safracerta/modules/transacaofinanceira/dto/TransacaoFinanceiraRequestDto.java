package com.safracerta.modules.transacaofinanceira.dto;

import com.safracerta.modules.transacaofinanceira.StatusTransacaoFinanceira;
import com.safracerta.modules.transacaofinanceira.TipoTransacaoFinanceira;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoFinanceiraRequestDto(
    @NotNull Long fazendaId,
    Long safraId,
    @NotNull TipoTransacaoFinanceira tipo,
    @NotNull @DecimalMin(value = "0.01", message = "valor deve ser maior que zero") BigDecimal valor,
    @Size(max = 500) String descricao,
    @Size(max = 40) String formaPagamento,
    @NotNull StatusTransacaoFinanceira status,
    @NotNull LocalDate dataTransacao,
    LocalDate dataVencimento,
    LocalDate dataPagamento,
    @Size(max = 120) String categoria,
    @Size(max = 200) String origem,
    @Size(max = 1000) String observacoes) {}
