USE safracerta;



ALTER TABLE safra DROP FOREIGN KEY fk_safra_talhao;
ALTER TABLE safra MODIFY COLUMN talhao_id BIGINT NULL;
ALTER TABLE safra
  ADD CONSTRAINT fk_safra_talhao
  FOREIGN KEY (talhao_id) REFERENCES talhao (id);
