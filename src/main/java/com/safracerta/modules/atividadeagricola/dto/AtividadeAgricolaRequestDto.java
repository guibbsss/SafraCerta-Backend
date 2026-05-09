package com.safracerta.modules.atividadeagricola.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AtividadeAgricolaRequestDto(
    @NotNull Long talhaoId,
    @NotBlank String tipoOperacao,
    @NotNull LocalDate dataAtividade,
    String descricao) {}
