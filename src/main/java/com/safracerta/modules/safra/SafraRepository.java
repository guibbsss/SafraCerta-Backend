package com.safracerta.modules.safra;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SafraRepository extends JpaRepository<Safra, Long> {

  List<Safra> findByExcluidoFalse();

  Optional<Safra> findByIdAndExcluidoFalse(Long id);

  List<Safra> findByExcluidoFalseAndTalhao_Fazenda_IdInOrderByDataPlantioDesc(
      Collection<Long> fazendaIds);

  Optional<Safra> findByIdAndExcluidoFalseAndTalhao_Fazenda_IdIn(
      Long id, Collection<Long> fazendaIds);
}
