USE safracerta;

CREATE TABLE talhao (
  id BIGINT NOT NULL AUTO_INCREMENT,
  fazenda_id BIGINT NOT NULL,
  nome VARCHAR(200) NOT NULL,
  area_hectares DECIMAL(14, 4) NULL,
  tipo_cultivo VARCHAR(120) NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_talhao_fazenda FOREIGN KEY (fazenda_id) REFERENCES fazenda (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
