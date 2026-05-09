USE safracerta;

CREATE TABLE fazenda (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(200) NOT NULL,
  localizacao VARCHAR(500) NULL,
  area_total DECIMAL(14, 4) NULL,
  proprietario VARCHAR(200) NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
