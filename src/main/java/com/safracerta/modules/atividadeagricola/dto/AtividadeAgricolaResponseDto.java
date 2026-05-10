package com.safracerta.modules.atividadeagricola.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public record AtividadeAgricolaResponseDto(
    Long id,
    @JsonProperty("talhaoId") Long talhaoId,
    @JsonProperty("talhaoNome") String talhaoNome,
    @JsonProperty("tipoOperacao") String tipoOperacao,
    @JsonProperty("dataAtividade") LocalDate dataAtividade,
    @JsonProperty("descricao") String descricao) {}
