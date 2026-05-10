package com.safracerta.modules.permissao.dto;

import java.util.List;

public record ResumoCategoriaPermissoesDto(String nome, List<ResumoPermissaoItemDto> permissoes) {}
