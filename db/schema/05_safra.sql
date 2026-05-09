USE safracerta;

CREATE TABLE safra (
  id BIGINT NOT NULL AUTO_INCREMENT,
  talhao_id BIGINT NOT NULL,
  cultura VARCHAR(120) NOT NULL,
  data_plantio DATE NOT NULL,
  data_colheita DATE NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_safra_talhao FOREIGN KEY (talhao_id) REFERENCES talhao (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
