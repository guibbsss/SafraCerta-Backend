package com.safracerta.modules.safra;

import com.safracerta.modules.talhao.Talhao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "safra")
public class Safra {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "talhao_id", nullable = false)
  private Talhao talhao;

  @Column(nullable = false, length = 120)
  private String cultura;

  @Column(name = "data_plantio", nullable = false)
  private LocalDate dataPlantio;

  @Column(name = "data_colheita")
  private LocalDate dataColheita;

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

  public String getCultura() {
    return cultura;
  }

  public void setCultura(String cultura) {
    this.cultura = cultura;
  }

  public LocalDate getDataPlantio() {
    return dataPlantio;
  }

  public void setDataPlantio(LocalDate dataPlantio) {
    this.dataPlantio = dataPlantio;
  }

  public LocalDate getDataColheita() {
    return dataColheita;
  }

  public void setDataColheita(LocalDate dataColheita) {
    this.dataColheita = dataColheita;
  }
}
