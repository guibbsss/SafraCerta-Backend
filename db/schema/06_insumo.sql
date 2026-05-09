USE safracerta;

CREATE TABLE insumo (
  id BIGINT NOT NULL AUTO_INCREMENT,
  fazenda_id BIGINT NOT NULL,
  nome VARCHAR(200) NOT NULL,
  categoria VARCHAR(120) NULL,
  quantidade_atual DECIMAL(18, 4) NOT NULL,
  unidade_medida VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_insumo_fazenda FOREIGN KEY (fazenda_id) REFERENCES fazenda (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
