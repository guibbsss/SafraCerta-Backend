package com.safracerta.modules.atividadeagricola;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AtividadeAgricolaRepository extends JpaRepository<AtividadeAgricola, Long> {

  @EntityGraph(attributePaths = {"talhao"})
  @Query(
      "SELECT a FROM AtividadeAgricola a WHERE a.id = :id AND a.talhao.fazenda.id IN :fazendaIds")
  Optional<AtividadeAgricola> findWithTalhaoByIdAndTalhao_Fazenda_IdIn(
      @Param("id") Long id, @Param("fazendaIds") Collection<Long> fazendaIds);

  @EntityGraph(attributePaths = {"talhao"})
  List<AtividadeAgricola> findByTalhao_Fazenda_IdInOrderByDataAtividadeDesc(
      Collection<Long> fazendaIds);

  @EntityGraph(attributePaths = {"talhao"})
  List<AtividadeAgricola> findByTalhao_IdOrderByDataAtividadeDesc(Long talhaoId);
}
