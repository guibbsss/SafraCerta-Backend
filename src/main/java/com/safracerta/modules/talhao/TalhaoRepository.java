package com.safracerta.modules.talhao;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TalhaoRepository extends JpaRepository<Talhao, Long> {

  List<Talhao> findByFazenda_IdInOrderByNomeAsc(Collection<Long> fazendaIds);

  List<Talhao> findByFazenda_IdOrderByNomeAsc(Long fazendaId);
}
