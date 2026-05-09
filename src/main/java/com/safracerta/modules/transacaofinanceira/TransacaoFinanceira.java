package com.safracerta.modules.transacaofinanceira;

import com.safracerta.modules.fazenda.Fazenda;
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
@Table(name = "transacao_financeira")
public class TransacaoFinanceira {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fazenda_id", nullable = false)
  private Fazenda fazenda;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TipoTransacaoFinanceira tipo;

  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal valor;

  @Column(name = "data_transacao", nullable = false)
  private LocalDate dataTransacao;

  @Column(length = 120)
  private String categoria;

  @Column(length = 200)
  private String origem;

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

  public TipoTransacaoFinanceira getTipo() {
    return tipo;
  }

  public void setTipo(TipoTransacaoFinanceira tipo) {
    this.tipo = tipo;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }

  public LocalDate getDataTransacao() {
    return dataTransacao;
  }

  public void setDataTransacao(LocalDate dataTransacao) {
    this.dataTransacao = dataTransacao;
  }

  public String getCategoria() {
    return categoria;
  }

  public void setCategoria(String categoria) {
    this.categoria = categoria;
  }

  public String getOrigem() {
    return origem;
  }

  public void setOrigem(String origem) {
    this.origem = origem;
  }
}
