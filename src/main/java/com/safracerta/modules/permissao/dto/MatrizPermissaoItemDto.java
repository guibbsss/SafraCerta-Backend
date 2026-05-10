package com.safracerta.modules.permissao.dto;

public record MatrizPermissaoItemDto(
    Long id, String nome, String descricao, boolean concedida) {}
