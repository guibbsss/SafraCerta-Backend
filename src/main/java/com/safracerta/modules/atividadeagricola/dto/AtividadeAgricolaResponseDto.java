package com.safracerta.modules.atividadeagricola.dto;

import java.time.LocalDate;

public record AtividadeAgricolaResponseDto(
    Long id, Long talhaoId, String tipoOperacao, LocalDate dataAtividade, String descricao) {}
