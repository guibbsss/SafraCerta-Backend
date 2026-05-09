package com.safracerta.modules.talhao;

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
@Table(name = "talhao")
public class Talhao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fazenda_id", nullable = false)
  private Fazenda fazenda;

  @Column(nullable = false, length = 200)
  private String nome;

  @Column(name = "area_hectares", precision = 14, scale = 4)
  private BigDecimal areaHectares;

  @Column(name = "tipo_cultivo", length = 120)
  private String tipoCultivo;

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

  public BigDecimal getAreaHectares() {
    return areaHectares;
  }

  public void setAreaHectares(BigDecimal areaHectares) {
    this.areaHectares = areaHectares;
  }

  public String getTipoCultivo() {
    return tipoCultivo;
  }

  public void setTipoCultivo(String tipoCultivo) {
    this.tipoCultivo = tipoCultivo;
  }
}
