package com.safracerta.modules.permissao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PermissaoRepository extends JpaRepository<Permissao, Long> {

  @Query(
      "SELECT p FROM Permissao p JOIN FETCH p.categoria c WHERE p.excluido = false AND p.ativo = true "
          + "AND c.excluido = false AND c.ativo = true ORDER BY c.nome ASC, p.nome ASC")
  List<Permissao> findAllVisiveisOrderByCategoriaENome();
}
