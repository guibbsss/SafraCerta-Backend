package com.safracerta.modules.permissao.dto;

import java.util.List;

public record PerfilPermissaoResumoDto(
    Long perfilId, String perfilNome, List<ResumoCategoriaPermissoesDto> categorias) {}
