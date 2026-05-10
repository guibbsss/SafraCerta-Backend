package com.safracerta.modules.movimentacaoestoque.dto;

import com.safracerta.modules.movimentacaoestoque.TipoMovimentacaoEstoque;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoEstoqueResponseDto(
    Long id,
    Long insumoId,
    String insumoNome,
    Long fazendaId,
    String fazendaNome,
    String categoria,
    TipoMovimentacaoEstoque tipoMovimentacao,
    BigDecimal quantidade,
    BigDecimal valorUnitario,
    BigDecimal valorTotal,
    LocalDateTime dataMovimentacao,
    String observacao,
    String fornecedor) {}
