package com.safracerta.modules.movimentacaoestoque.dto;

import com.safracerta.modules.movimentacaoestoque.TipoMovimentacaoEstoque;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoEstoqueResponseDto(
    Long id,
    Long insumoId,
    TipoMovimentacaoEstoque tipoMovimentacao,
    BigDecimal quantidade,
    LocalDateTime dataMovimentacao,
    String observacao) {}
