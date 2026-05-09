package com.safracerta.modules.permissao;

import com.safracerta.modules.perfil.Perfil;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "perfil_has_permissao")
public class PerfilHasPermissao {

  @EmbeddedId private PerfilPermissaoId id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("perfilId")
  @JoinColumn(name = "perfil_id", nullable = false)
  private Perfil perfil;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("permissaoId")
  @JoinColumn(name = "permissao_id", nullable = false)
  private Permissao permissao;

  @Column(nullable = false)
  private boolean ativo = true;

  @Column(nullable = false)
  private boolean excluido = false;

  public PerfilHasPermissao() {}

  public PerfilPermissaoId getId() {
    return id;
  }

  public void setId(PerfilPermissaoId id) {
    this.id = id;
  }

  public Perfil getPerfil() {
    return perfil;
  }

  public void setPerfil(Perfil perfil) {
    this.perfil = perfil;
  }

  public Permissao getPermissao() {
    return permissao;
  }

  public void setPermissao(Permissao permissao) {
    this.permissao = permissao;
  }

  public boolean isAtivo() {
    return ativo;
  }

  public void setAtivo(boolean ativo) {
    this.ativo = ativo;
  }

  public boolean isExcluido() {
    return excluido;
  }

  public void setExcluido(boolean excluido) {
    this.excluido = excluido;
  }
}
