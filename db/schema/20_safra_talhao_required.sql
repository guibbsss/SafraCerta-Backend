USE safracerta;

-- Reverte 14_safra_talhao_nullable: agora o módulo de Talhões já está pronto,
-- então toda safra precisa estar vinculada a um talhão.
-- Antes de aplicar, garanta que não existem safras com talhao_id NULL.
-- Se houver, vincule manualmente ou exclua antes desta migration.

ALTER TABLE safra DROP FOREIGN KEY fk_safra_talhao;
ALTER TABLE safra MODIFY COLUMN talhao_id BIGINT NOT NULL;
ALTER TABLE safra
  ADD CONSTRAINT fk_safra_talhao
  FOREIGN KEY (talhao_id) REFERENCES talhao (id);
