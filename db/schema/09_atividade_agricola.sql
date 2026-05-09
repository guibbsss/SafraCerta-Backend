USE safracerta;

CREATE TABLE atividade_agricola (
  id BIGINT NOT NULL AUTO_INCREMENT,
  talhao_id BIGINT NOT NULL,
  tipo_operacao VARCHAR(120) NOT NULL,
  data_atividade DATE NOT NULL,
  descricao VARCHAR(500) NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_atividade_talhao FOREIGN KEY (talhao_id) REFERENCES talhao (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
