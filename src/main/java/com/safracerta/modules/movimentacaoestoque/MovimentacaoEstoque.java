package com.safracerta.modules.movimentacaoestoque;

import com.safracerta.modules.insumo.Insumo;
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
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao_estoque")
public class MovimentacaoEstoque {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "insumo_id", nullable = false)
  private Insumo insumo;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_movimentacao", nullable = false, length = 20)
  private TipoMovimentacaoEstoque tipoMovimentacao;

  @Column(nullable = false, precision = 18, scale = 4)
  private BigDecimal quantidade;

  @Column(name = "data_movimentacao", nullable = false)
  private LocalDateTime dataMovimentacao;

  @Column(length = 500)
  private String observacao;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Insumo getInsumo() {
    return insumo;
  }

  public void setInsumo(Insumo insumo) {
    this.insumo = insumo;
  }

  public TipoMovimentacaoEstoque getTipoMovimentacao() {
    return tipoMovimentacao;
  }

  public void setTipoMovimentacao(TipoMovimentacaoEstoque tipoMovimentacao) {
    this.tipoMovimentacao = tipoMovimentacao;
  }

  public BigDecimal getQuantidade() {
    return quantidade;
  }

  public void setQuantidade(BigDecimal quantidade) {
    this.quantidade = quantidade;
  }

  public LocalDateTime getDataMovimentacao() {
    return dataMovimentacao;
  }

  public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
    this.dataMovimentacao = dataMovimentacao;
  }

  public String getObservacao() {
    return observacao;
  }

  public void setObservacao(String observacao) {
    this.observacao = observacao;
  }
}
