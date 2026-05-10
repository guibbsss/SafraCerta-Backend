package com.safracerta.modules.safra;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SafraRepository extends JpaRepository<Safra, Long> {

  List<Safra> findByExcluidoFalse();

  Optional<Safra> findByIdAndExcluidoFalse(Long id);
}
