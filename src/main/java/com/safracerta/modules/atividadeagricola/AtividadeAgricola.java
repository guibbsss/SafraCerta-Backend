package com.safracerta.modules.atividadeagricola;

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
@Table(name = "atividade_agricola")
public class AtividadeAgricola {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "talhao_id", nullable = false)
  private Talhao talhao;

  @Column(name = "tipo_operacao", nullable = false, length = 120)
  private String tipoOperacao;

  @Column(name = "data_atividade", nullable = false)
  private LocalDate dataAtividade;

  @Column(length = 500)
  private String descricao;

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

  public String getTipoOperacao() {
    return tipoOperacao;
  }

  public void setTipoOperacao(String tipoOperacao) {
    this.tipoOperacao = tipoOperacao;
  }

  public LocalDate getDataAtividade() {
    return dataAtividade;
  }

  public void setDataAtividade(LocalDate dataAtividade) {
    this.dataAtividade = dataAtividade;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }
}
