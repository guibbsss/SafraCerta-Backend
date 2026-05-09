package com.safracerta.modules.permissao.dto;

import java.util.List;

public record MatrizCategoriaDto(
    Long categoriaId, String categoriaNome, List<MatrizPermissaoItemDto> permissoes) {}
