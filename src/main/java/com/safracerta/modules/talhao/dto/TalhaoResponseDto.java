package com.safracerta.modules.talhao.dto;

import java.math.BigDecimal;

public record TalhaoResponseDto(
    Long id,
    Long fazendaId,
    String fazendaNome,
    String nome,
    BigDecimal areaHectares,
    String tipoCultivo) {}
