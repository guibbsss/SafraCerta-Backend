package com.safracerta.modules.movimentacaoestoque.dto;

import com.safracerta.modules.movimentacaoestoque.TipoMovimentacaoEstoque;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoEstoqueRequestDto(
    @NotNull Long insumoId,
    @NotNull TipoMovimentacaoEstoque tipoMovimentacao,
    @NotNull BigDecimal quantidade,
    @NotNull LocalDateTime dataMovimentacao,
    String observacao) {}
