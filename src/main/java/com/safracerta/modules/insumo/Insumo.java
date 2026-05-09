package com.safracerta.modules.insumo;

import com.safracerta.modules.fazenda.Fazenda;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "insumo")
public class Insumo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fazenda_id", nullable = false)
  private Fazenda fazenda;

  @Column(nullable = false, length = 200)
  private String nome;

  @Column(length = 120)
  private String categoria;

  @Column(name = "quantidade_atual", nullable = false, precision = 18, scale = 4)
  private BigDecimal quantidadeAtual;

  @Column(name = "unidade_medida", nullable = false, length = 50)
  private String unidadeMedida;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Fazenda getFazenda() {
    return fazenda;
  }

  public void setFazenda(Fazenda fazenda) {
    this.fazenda = fazenda;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getCategoria() {
    return categoria;
  }

  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }

  public BigDecimal getQuantidadeAtual() {
    return quantidadeAtual;
  }

  public void setQuantidadeAtual(BigDecimal quantidadeAtual) {
    this.quantidadeAtual = quantidadeAtual;
  }

  public String getUnidadeMedida() {
    return unidadeMedida;
  }

  public void setUnidadeMedida(String unidadeMedida) {
    this.unidadeMedida = unidadeMedida;
  }
}
