package com.safracerta.modules.permissao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "permissao")
public class Permissao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "permissao_categoria_id", nullable = false)
  private PermissaoCategoria categoria;

  @Column(nullable = false, length = 200)
  private String nome;

  @Column(length = 1000)
  private String descricao;

  @Column(nullable = false)
  private boolean ativo = true;

  @Column(nullable = false)
  private boolean excluido = false;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public PermissaoCategoria getCategoria() {
    return categoria;
  }

  public void setCategoria(PermissaoCategoria categoria) {
    this.categoria = categoria;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
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
