USE safracerta;

CREATE TABLE transacao_financeira (
  id BIGINT NOT NULL AUTO_INCREMENT,
  fazenda_id BIGINT NOT NULL,
  tipo VARCHAR(20) NOT NULL,
  valor DECIMAL(14, 2) NOT NULL,
  data_transacao DATE NOT NULL,
  categoria VARCHAR(120) NULL,
  origem VARCHAR(200) NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_transacao_fazenda FOREIGN KEY (fazenda_id) REFERENCES fazenda (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
