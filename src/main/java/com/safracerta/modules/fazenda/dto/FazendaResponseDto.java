package com.safracerta.modules.fazenda.dto;

import java.math.BigDecimal;

public record FazendaResponseDto(
    Long id, String nome, String localizacao, BigDecimal areaTotal, String proprietario) {}
