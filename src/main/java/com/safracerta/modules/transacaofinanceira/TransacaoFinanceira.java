package com.safracerta.modules.transacaofinanceira;

import com.safracerta.modules.fazenda.Fazenda;
import com.safracerta.modules.safra.Safra;
import com.safracerta.modules.user.Usuario;
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
import java.time.LocalDateTime;

@Entity
@Table(name = "transacao_financeira")
public class TransacaoFinanceira {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fazenda_id", nullable = false)
  private Fazenda fazenda;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "safra_id")
  private Safra safra;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TipoTransacaoFinanceira tipo;

  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal valor;

  @Column(length = 500)
  private String descricao;

  @Column(name = "forma_pagamento", length = 40)
  private String formaPagamento;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private StatusTransacaoFinanceira status = StatusTransacaoFinanceira.PAGO;

  @Column(name = "data_transacao", nullable = false)
  private LocalDate dataTransacao;

  @Column(name = "data_vencimento")
  private LocalDate dataVencimento;

  @Column(name = "data_pagamento")
  private LocalDate dataPagamento;

  @Column(length = 120)
  private String categoria;

  @Column(length = 200)
  private String origem;

  @Column(length = 1000)
  private String observacoes;

  @Column(nullable = false)
  private boolean excluido = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "excluido_por_id")
  private Usuario excluidoPor;

  @Column(name = "justificativa_exclusao", length = 500)
  private String justificativaExclusao;

  @Column(name = "excluido_em")
  private LocalDateTime excluidoEm;

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

  public Safra getSafra() {
    return safra;
  }

  public void setSafra(Safra safra) {
    this.safra = safra;
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

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public String getFormaPagamento() {
    return formaPagamento;
  }

  public void setFormaPagamento(String formaPagamento) {
    this.formaPagamento = formaPagamento;
  }

  public StatusTransacaoFinanceira getStatus() {
    return status;
  }

  public void setStatus(StatusTransacaoFinanceira status) {
    this.status = status;
  }

  public LocalDate getDataTransacao() {
    return dataTransacao;
  }

  public void setDataTransacao(LocalDate dataTransacao) {
    this.dataTransacao = dataTransacao;
  }

  public LocalDate getDataVencimento() {
    return dataVencimento;
  }

  public void setDataVencimento(LocalDate dataVencimento) {
    this.dataVencimento = dataVencimento;
  }

  public LocalDate getDataPagamento() {
    return dataPagamento;
  }

  public void setDataPagamento(LocalDate dataPagamento) {
    this.dataPagamento = dataPagamento;
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

  public String getObservacoes() {
    return observacoes;
  }

  public void setObservacoes(String observacoes) {
    this.observacoes = observacoes;
  }

  public boolean isExcluido() {
    return excluido;
  }

  public void setExcluido(boolean excluido) {
    this.excluido = excluido;
  }

  public Usuario getExcluidoPor() {
    return excluidoPor;
  }

  public void setExcluidoPor(Usuario excluidoPor) {
    this.excluidoPor = excluidoPor;
  }

  public String getJustificativaExclusao() {
    return justificativaExclusao;
  }

  public void setJustificativaExclusao(String justificativaExclusao) {
    this.justificativaExclusao = justificativaExclusao;
  }

  public LocalDateTime getExcluidoEm() {
    return excluidoEm;
  }

  public void setExcluidoEm(LocalDateTime excluidoEm) {
    this.excluidoEm = excluidoEm;
  }
}
