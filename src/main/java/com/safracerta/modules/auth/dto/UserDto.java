package com.safracerta.modules.auth.dto;

import java.util.List;

public record UserDto(
    Long id,
    String email,
    String nome,
    boolean ativo,
    Long perfilId,
    List<Long> permissaoIds) {}
