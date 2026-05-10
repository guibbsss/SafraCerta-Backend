package com.safracerta.modules.atividadeagricola;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtividadeAgricolaRepository extends JpaRepository<AtividadeAgricola, Long> {

  List<AtividadeAgricola> findByTalhao_IdOrderByDataAtividadeDesc(Long talhaoId);
}
