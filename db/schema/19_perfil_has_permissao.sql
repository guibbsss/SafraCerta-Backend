USE safracerta;

CREATE TABLE perfil_has_permissao (
  perfil_id BIGINT NOT NULL,
  permissao_id BIGINT NOT NULL,
  ativo TINYINT(1) NOT NULL DEFAULT 1,
  excluido TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (perfil_id, permissao_id),
  KEY idx_php_permissao (permissao_id),
  CONSTRAINT fk_php_perfil FOREIGN KEY (perfil_id) REFERENCES perfil (id),
  CONSTRAINT fk_php_permissao FOREIGN KEY (permissao_id) REFERENCES permissao (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
