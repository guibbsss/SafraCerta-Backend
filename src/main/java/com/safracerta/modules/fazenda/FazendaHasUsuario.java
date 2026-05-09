package com.safracerta.modules.fazenda;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "fazenda_has_usuario")
public class FazendaHasUsuario {

  @EmbeddedId private FazendaUsuarioId id;

  public FazendaHasUsuario() {}

  public FazendaHasUsuario(FazendaUsuarioId id) {
    this.id = id;
  }

  public FazendaUsuarioId getId() {
    return id;
  }

  public void setId(FazendaUsuarioId id) {
    this.id = id;
  }
}
