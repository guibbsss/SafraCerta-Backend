package com.safracerta.modules.movimentacaoestoque;

import com.safracerta.modules.insumo.Insumo;
import com.safracerta.modules.safra.Safra;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "safra_id")
  private Safra safra;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_movimentacao", nullable = false, length = 20)
  private TipoMovimentacaoEstoque tipoMovimentacao;

  @Column(nullable = false, precision = 18, scale = 4)
  private BigDecimal quantidade;

  @Column(name = "valor_unitario", precision = 18, scale = 4)
  private BigDecimal valorUnitario;

  @Column(name = "data_movimentacao", nullable = false)
  private LocalDateTime dataMovimentacao;

  @Column(length = 500)
  private String observacao;

  @Column(length = 200)
  private String fornecedor;

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

  public Safra getSafra() {
    return safra;
  }

  public void setSafra(Safra safra) {
    this.safra = safra;
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

  public BigDecimal getValorUnitario() {
    return valorUnitario;
  }

  public void setValorUnitario(BigDecimal valorUnitario) {
    this.valorUnitario = valorUnitario;
  }

  public String getFornecedor() {
    return fornecedor;
  }

  public void setFornecedor(String fornecedor) {
    this.fornecedor = fornecedor;
  }
}
