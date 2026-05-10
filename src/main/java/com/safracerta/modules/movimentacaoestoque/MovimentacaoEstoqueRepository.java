package com.safracerta.modules.movimentacaoestoque;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

  @Query(
      """
      SELECT DISTINCT m FROM MovimentacaoEstoque m
      JOIN FETCH m.insumo i
      JOIN FETCH i.fazenda
      ORDER BY m.dataMovimentacao DESC
      """)
  List<MovimentacaoEstoque> findAllWithDetails();

  @Query(
      """
      SELECT DISTINCT m FROM MovimentacaoEstoque m
      JOIN FETCH m.insumo i
      JOIN FETCH i.fazenda
      WHERE m.tipoMovimentacao = :tipo
      ORDER BY m.dataMovimentacao DESC
      """)
  List<MovimentacaoEstoque> findByTipoWithDetails(
      @Param("tipo") TipoMovimentacaoEstoque tipo);

  @Query(
      """
      SELECT m FROM MovimentacaoEstoque m
      JOIN FETCH m.insumo i
      JOIN FETCH i.fazenda
      WHERE m.id = :id
      """)
  Optional<MovimentacaoEstoque> findByIdWithDetails(@Param("id") Long id);

  @Query(
      """
      SELECT DISTINCT m FROM MovimentacaoEstoque m
      JOIN FETCH m.insumo i
      JOIN FETCH i.fazenda
      WHERE m.safra.id = :safraId
      ORDER BY m.id ASC
      """)
  List<MovimentacaoEstoque> findBySafra_IdOrderByIdAsc(@Param("safraId") Long safraId);
}
