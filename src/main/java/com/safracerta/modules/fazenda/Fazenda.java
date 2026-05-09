package com.safracerta.modules.fazenda;

import com.safracerta.modules.user.Usuario;
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
@Table(name = "fazenda")
public class Fazenda {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String nome;

  @Column(length = 500)
  private String localizacao;

  @Column(name = "area_total", precision = 14, scale = 4)
  private BigDecimal areaTotal;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "proprietario", nullable = false)
  private Usuario proprietario;

  @Column(name = "cod_fazenda", nullable = false, unique = true, length = 15)
  private String codFazenda;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getLocalizacao() {
    return localizacao;
  }

  public void setLocalizacao(String localizacao) {
    this.localizacao = localizacao;
  }

  public BigDecimal getAreaTotal() {
    return areaTotal;
  }

  public void setAreaTotal(BigDecimal areaTotal) {
    this.areaTotal = areaTotal;
  }

  public Usuario getProprietario() {
    return proprietario;
  }

  public void setProprietario(Usuario proprietario) {
    this.proprietario = proprietario;
  }

  public String getCodFazenda() {
    return codFazenda;
  }

  public void setCodFazenda(String codFazenda) {
    this.codFazenda = codFazenda;
  }
}
