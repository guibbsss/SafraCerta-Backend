package com.safracerta.modules.transacaofinanceira;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransacaoFinanceiraRepository
    extends JpaRepository<TransacaoFinanceira, Long> {

  Optional<TransacaoFinanceira> findByIdAndExcluidoFalse(Long id);

  /**
   * Listagem com filtros opcionais. Cada parâmetro é tratado como "ignorar quando null", o que
   * permite reaproveitar o mesmo método para todas as combinações da tela.
   */
  @Query(
      "SELECT t FROM TransacaoFinanceira t "
          + "WHERE t.excluido = false "
          + "AND (:fazendaId IS NULL OR t.fazenda.id = :fazendaId) "
          + "AND (:tipo IS NULL OR t.tipo = :tipo) "
          + "AND (:status IS NULL OR t.status = :status) "
          + "AND (:dataInicio IS NULL OR t.dataTransacao >= :dataInicio) "
          + "AND (:dataFim IS NULL OR t.dataTransacao <= :dataFim) "
          + "ORDER BY t.dataTransacao DESC, t.id DESC")
  List<TransacaoFinanceira> filtrar(
      @Param("fazendaId") Long fazendaId,
      @Param("tipo") TipoTransacaoFinanceira tipo,
      @Param("status") StatusTransacaoFinanceira status,
      @Param("dataInicio") LocalDate dataInicio,
      @Param("dataFim") LocalDate dataFim);

}
