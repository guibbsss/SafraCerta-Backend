USE safracerta;

-- tipo_movimentacao: valores usados pelo Java (EnumType.STRING): ENTRADA | SAIDA
CREATE TABLE movimentacao_estoque (
  id BIGINT NOT NULL AUTO_INCREMENT,
  insumo_id BIGINT NOT NULL,
  tipo_movimentacao VARCHAR(20) NOT NULL,
  quantidade DECIMAL(18, 4) NOT NULL,
  data_movimentacao DATETIME(6) NOT NULL,
  observacao VARCHAR(500) NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_movimentacao_insumo FOREIGN KEY (insumo_id) REFERENCES insumo (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
