package com.safracerta.modules.transacaofinanceira.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TransacaoFinanceiraExclusaoRequestDto(
    @NotBlank @Size(min = 5, max = 500) String justificativa) {}
