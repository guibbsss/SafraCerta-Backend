package com.safracerta.modules.insumo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {

  @EntityGraph(attributePaths = {"fazenda"})
  List<Insumo> findByFazenda_IdInOrderByNomeAsc(Collection<Long> fazendaIds);

  @EntityGraph(attributePaths = {"fazenda"})
  Optional<Insumo> findByIdAndFazenda_IdIn(Long id, Collection<Long> fazendaIds);

  @EntityGraph(attributePaths = {"fazenda"})
  List<Insumo> findByFazenda_IdOrderByNomeAsc(Long fazendaId);
}
