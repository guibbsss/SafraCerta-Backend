package com.safracerta.modules.solicitacao.dto;

public record SolicitacaoEntradaResponseDto(
    Long usuarioId,
    String nomeUsuario,
    Long fazendaId,
    String nomeFazenda,
    PerfilDoUsuarioDto perfil,
    String status) {}
