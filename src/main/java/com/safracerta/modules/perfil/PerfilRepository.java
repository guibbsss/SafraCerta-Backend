package com.safracerta.modules.perfil;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

  List<Perfil> findAllByExcluidoFalseOrderByNomeAsc();
}
