USE safracerta;

-- Permite cadastrar safras antes de ter talhões cadastrados.
-- Solta a FK existente, torna a coluna nullable e recria a FK aceitando NULL.

ALTER TABLE safra DROP FOREIGN KEY fk_safra_talhao;
ALTER TABLE safra MODIFY COLUMN talhao_id BIGINT NULL;
ALTER TABLE safra
  ADD CONSTRAINT fk_safra_talhao
  FOREIGN KEY (talhao_id) REFERENCES talhao (id);
