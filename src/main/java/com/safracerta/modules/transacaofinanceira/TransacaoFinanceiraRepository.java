package com.safracerta.modules.transacaofinanceira;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransacaoFinanceiraRepository
    extends JpaRepository<TransacaoFinanceira, Long> {

  Optional<TransacaoFinanceira> findByIdAndExcluidoFalseAndFazenda_IdIn(
      Long id, Collection<Long> fazendaIds);

  /**
   * Listagem com filtros opcionais, restrita às fazendas indicadas. {@code fazendaId} filtra uma
   * única fazenda dentro desse conjunto (útil na UI); quando null, inclui todas as fazendas em
   * {@code fazendaIds}.
   */
  @Query(
      "SELECT t FROM TransacaoFinanceira t "
          + "WHERE t.excluido = false "
          + "AND t.fazenda.id IN :fazendaIds "
          + "AND (:fazendaId IS NULL OR t.fazenda.id = :fazendaId) "
          + "AND (:tipo IS NULL OR t.tipo = :tipo) "
          + "AND (:status IS NULL OR t.status = :status) "
          + "AND (:dataInicio IS NULL OR t.dataTransacao >= :dataInicio) "
          + "AND (:dataFim IS NULL OR t.dataTransacao <= :dataFim) "
          + "ORDER BY t.dataTransacao DESC, t.id DESC")
  List<TransacaoFinanceira> filtrarPorFazendas(
      @Param("fazendaIds") Collection<Long> fazendaIds,
      @Param("fazendaId") Long fazendaId,
      @Param("tipo") TipoTransacaoFinanceira tipo,
      @Param("status") StatusTransacaoFinanceira status,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim);
}
