USE safracerta;

CREATE TABLE usuario (
  id BIGINT NOT NULL AUTO_INCREMENT,
  email VARCHAR(180) NOT NULL,
  senha VARCHAR(255) NOT NULL,
  nome VARCHAR(200) NOT NULL,
  perfil_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_usuario_email (email),
  CONSTRAINT fk_usuario_perfil FOREIGN KEY (perfil_id) REFERENCES perfil (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
