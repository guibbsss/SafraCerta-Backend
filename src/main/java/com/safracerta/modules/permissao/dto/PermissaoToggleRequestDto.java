package com.safracerta.modules.permissao.dto;

import jakarta.validation.constraints.NotNull;

public record PermissaoToggleRequestDto(@NotNull Boolean ativo) {}
