USE safracerta;

-- Soft delete + auditoria de exclusão de safra.
-- A safra deixa de ser removida fisicamente; passamos a marcar `excluido = 1`
-- guardando QUEM excluiu, QUANDO e o motivo (justificativa obrigatória no front).

ALTER TABLE safra
  ADD COLUMN excluido TINYINT(1) NOT NULL DEFAULT 0,
  ADD COLUMN excluido_por_id BIGINT NULL,
  ADD COLUMN justificativa_exclusao VARCHAR(500) NULL,
  ADD COLUMN excluido_em DATETIME NULL,
  ADD CONSTRAINT fk_safra_excluido_por
    FOREIGN KEY (excluido_por_id) REFERENCES usuario (id);

CREATE INDEX idx_safra_excluido ON safra (excluido);
