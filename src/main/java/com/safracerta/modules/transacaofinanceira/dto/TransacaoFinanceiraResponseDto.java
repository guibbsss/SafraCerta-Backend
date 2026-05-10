package com.safracerta.modules.transacaofinanceira.dto;

import com.safracerta.modules.transacaofinanceira.StatusTransacaoFinanceira;
import com.safracerta.modules.transacaofinanceira.TipoTransacaoFinanceira;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoFinanceiraResponseDto(
    Long id,
    Long fazendaId,
    String fazendaNome,
    Long safraId,
    String safraNome,
    TipoTransacaoFinanceira tipo,
    BigDecimal valor,
    String descricao,
    String formaPagamento,
    StatusTransacaoFinanceira status,
    LocalDate dataTransacao,
    LocalDate dataVencimento,
    LocalDate dataPagamento,
    String categoria,
    String origem,
    String observacoes) {}
