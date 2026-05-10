USE safracerta;

ALTER TABLE movimentacao_estoque
  ADD COLUMN safra_id BIGINT NULL,
  ADD INDEX idx_movimentacao_estoque_safra (safra_id),
  ADD CONSTRAINT fk_movimentacao_safra FOREIGN KEY (safra_id) REFERENCES safra (id);
