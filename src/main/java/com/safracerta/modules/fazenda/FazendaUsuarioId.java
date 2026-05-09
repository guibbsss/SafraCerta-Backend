package com.safracerta.modules.fazenda;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FazendaUsuarioId implements Serializable {

  @Column(name = "fazenda_id", nullable = false)
  private Long fazendaId;

  @Column(name = "usuario_id", nullable = false)
  private Long usuarioId;

  public FazendaUsuarioId() {}

  public FazendaUsuarioId(Long fazendaId, Long usuarioId) {
    this.fazendaId = fazendaId;
    this.usuarioId = usuarioId;
  }

  public Long getFazendaId() {
    return fazendaId;
  }

  public void setFazendaId(Long fazendaId) {
    this.fazendaId = fazendaId;
  }

  public Long getUsuarioId() {
    return usuarioId;
  }

  public void setUsuarioId(Long usuarioId) {
    this.usuarioId = usuarioId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FazendaUsuarioId that = (FazendaUsuarioId) o;
    return Objects.equals(fazendaId, that.fazendaId) && Objects.equals(usuarioId, that.usuarioId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fazendaId, usuarioId);
  }
}
