package com.safracerta.modules.permissao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PerfilHasPermissaoRepository
    extends JpaRepository<PerfilHasPermissao, PerfilPermissaoId> {

  Optional<PerfilHasPermissao> findByIdPerfilIdAndIdPermissaoId(Long perfilId, Long permissaoId);

  @Query(
      "SELECT h FROM PerfilHasPermissao h JOIN FETCH h.permissao perm JOIN FETCH perm.categoria c "
          + "WHERE h.id.perfilId = :perfilId AND h.excluido = false AND h.ativo = true "
          + "AND perm.excluido = false AND perm.ativo = true AND c.excluido = false AND c.ativo = true "
          + "ORDER BY c.nome ASC, perm.nome ASC")
  List<PerfilHasPermissao> findConcedidasVisiveisPorPerfil(@Param("perfilId") Long perfilId);

  List<PerfilHasPermissao> findAllByIdPerfilIdAndExcluidoFalse(Long perfilId);
}
