package com.safracerta.modules.auth.dto;

public record RegistroUsuarioResponseDto(
    Long id, String nome, String email, boolean ativo, String mensagem) {}
