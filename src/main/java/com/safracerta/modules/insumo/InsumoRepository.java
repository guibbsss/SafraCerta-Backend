package com.safracerta.modules.insumo;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {

  @Query("SELECT DISTINCT i FROM Insumo i JOIN FETCH i.fazenda ORDER BY i.nome ASC")
  List<Insumo> findAllWithFazenda();

  @EntityGraph(attributePaths = {"fazenda"})
  List<Insumo> findByFazenda_IdOrderByNomeAsc(Long fazendaId);
}
