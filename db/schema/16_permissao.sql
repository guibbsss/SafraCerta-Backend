USE safracerta;

CREATE TABLE permissao (
  id BIGINT NOT NULL AUTO_INCREMENT,
  permissao_categoria_id BIGINT NOT NULL,
  nome VARCHAR(200) NOT NULL,
  descricao VARCHAR(1000) NULL,
  ativo TINYINT(1) NOT NULL DEFAULT 1,
  excluido TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_permissao_categoria (permissao_categoria_id),
  CONSTRAINT fk_permissao_categoria
    FOREIGN KEY (permissao_categoria_id) REFERENCES permissao_categoria (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
