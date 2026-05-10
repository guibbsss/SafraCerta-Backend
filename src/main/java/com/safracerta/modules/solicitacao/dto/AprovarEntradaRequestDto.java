package com.safracerta.modules.solicitacao.dto;

import jakarta.validation.constraints.NotNull;

public record AprovarEntradaRequestDto(
    @NotNull Long proprietarioUsuarioId,
    @NotNull Long usuarioId,
    @NotNull Long fazendaId,
    @NotNull Long perfilId) {}
