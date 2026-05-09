package com.safracerta.modules.fazenda;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FazendaRepository extends JpaRepository<Fazenda, Long> {

  boolean existsByCodFazenda(String codFazenda);

  Optional<Fazenda> findByCodFazenda(String codFazenda);

  @Query(
      "SELECT f FROM Fazenda f WHERE EXISTS (SELECT 1 FROM FazendaHasUsuario h WHERE"
          + " h.id.fazendaId = f.id AND h.id.usuarioId = :usuarioId)")
  List<Fazenda> findAllByUsuarioVinculado(@Param("usuarioId") Long usuarioId);
}
