package com.safracerta.modules.movimentacaoestoque.dto;

import com.safracerta.modules.movimentacaoestoque.TipoMovimentacaoEstoque;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoEstoqueRequestDto(
    /** Opcional: se enviado, o backend valida se coincide com a fazenda do insumo. */
    Long fazendaId,
    @NotNull Long insumoId,
    @NotNull TipoMovimentacaoEstoque tipoMovimentacao,
    @NotNull BigDecimal quantidade,
    @NotNull LocalDateTime dataMovimentacao,
    String observacao,
    BigDecimal valorUnitario,
    String fornecedor) {}
