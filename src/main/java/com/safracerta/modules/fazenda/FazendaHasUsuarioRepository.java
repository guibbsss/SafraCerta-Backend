package com.safracerta.modules.fazenda;

import com.safracerta.modules.solicitacao.projection.SolicitacaoEntradaListagemProj;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FazendaHasUsuarioRepository extends JpaRepository<FazendaHasUsuario, FazendaUsuarioId> {

  @Query(
      value =
          "SELECT u.id AS usuarioId, u.nome AS nomeUsuario, f.id AS fazendaId, f.nome AS nomeFazenda,"
              + " p.id AS perfilId, p.nome AS perfilNome, u.ativo AS ativo "
              + "FROM fazenda_has_usuario h "
              + "INNER JOIN usuario u ON u.id = h.usuario_id "
              + "INNER JOIN fazenda f ON f.id = h.fazenda_id "
              + "INNER JOIN perfil p ON p.id = u.perfil_id "
              + "WHERE f.proprietario = :proprietarioId "
              + "ORDER BY u.ativo ASC, u.nome ASC",
      nativeQuery = true)
  List<SolicitacaoEntradaListagemProj> findSolicitacoesPorProprietario(
      @Param("proprietarioId") Long proprietarioUsuarioId);
}
