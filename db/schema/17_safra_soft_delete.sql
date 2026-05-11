USE safracerta;


ALTER TABLE safra
  ADD COLUMN excluido TINYINT(1) NOT NULL DEFAULT 0,
  ADD COLUMN excluido_por_id BIGINT NULL,
  ADD COLUMN justificativa_exclusao VARCHAR(500) NULL,
  ADD COLUMN excluido_em DATETIME NULL,
  ADD CONSTRAINT fk_safra_excluido_por
    FOREIGN KEY (excluido_por_id) REFERENCES usuario (id);

CREATE INDEX idx_safra_excluido ON safra (excluido);
