package com.safracerta.modules.solicitacao.projection;

/** Projeção para {@code nativeQuery} — aliases alinhados aos getters. */
public interface SolicitacaoEntradaListagemProj {

  Long getUsuarioId();

  String getNomeUsuario();

  Long getFazendaId();

  String getNomeFazenda();

  Long getPerfilId();

  String getPerfilNome();

  Boolean getAtivo();
}
