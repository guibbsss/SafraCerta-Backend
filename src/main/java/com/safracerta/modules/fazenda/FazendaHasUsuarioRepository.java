package com.safracerta.modules.fazenda;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FazendaHasUsuarioRepository extends JpaRepository<FazendaHasUsuario, FazendaUsuarioId> {}
