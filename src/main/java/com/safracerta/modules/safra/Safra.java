package com.safracerta.modules.safra;

import com.safracerta.modules.talhao.Talhao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "safra")
public class Safra {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "talhao_id")
  private Talhao talhao;

  @Column(nullable = false, length = 160)
  private String nome;

  @Column(nullable = false, length = 120)
  private String cultura;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private SafraStatus status;

  @Column(name = "data_plantio", nullable = false)
  private LocalDate dataPlantio;

  @Column(name = "data_colheita_prevista")
  private LocalDate dataColheitaPrevista;

  @Column(name = "data_colheita_real")
  private LocalDate dataColheitaReal;

  @Column(name = "producao_estimada", precision = 14, scale = 4)
  private BigDecimal producaoEstimada;

  @Column(name = "producao_real", precision = 14, scale = 4)
  private BigDecimal producaoReal;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Talhao getTalhao() {
    return talhao;
  }

  public void setTalhao(Talhao talhao) {
    this.talhao = talhao;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getCultura() {
    return cultura;
  }

  public void setCultura(String cultura) {
    this.cultura = cultura;
  }

  public SafraStatus getStatus() {
    return status;
  }

  public void setStatus(SafraStatus status) {
    this.status = status;
  }

  public LocalDate getDataPlantio() {
    return dataPlantio;
  }

  public void setDataPlantio(LocalDate dataPlantio) {
    this.dataPlantio = dataPlantio;
  }

  public LocalDate getDataColheitaPrevista() {
    return dataColheitaPrevista;
  }

  public void setDataColheitaPrevista(LocalDate dataColheitaPrevista) {
    this.dataColheitaPrevista = dataColheitaPrevista;
  }

  public LocalDate getDataColheitaReal() {
    return dataColheitaReal;
  }

  public void setDataColheitaReal(LocalDate dataColheitaReal) {
    this.dataColheitaReal = dataColheitaReal;
  }

  public BigDecimal getProducaoEstimada() {
    return producaoEstimada;
  }

  public void setProducaoEstimada(BigDecimal producaoEstimada) {
    this.producaoEstimada = producaoEstimada;
  }

  public BigDecimal getProducaoReal() {
    return producaoReal;
  }

  public void setProducaoReal(BigDecimal producaoReal) {
    this.producaoReal = producaoReal;
  }
}
