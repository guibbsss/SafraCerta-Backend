USE safracerta;

-- Vínculo N:N entre fazenda e usuário (vários usuários podem estar associados à mesma fazenda).
CREATE TABLE fazenda_has_usuario (
  fazenda_id BIGINT NOT NULL,
  usuario_id BIGINT NOT NULL,
  PRIMARY KEY (fazenda_id, usuario_id),
  CONSTRAINT fk_fhu_fazenda
    FOREIGN KEY (fazenda_id)
    REFERENCES fazenda (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_fhu_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES usuario (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  INDEX idx_fhu_usuario (usuario_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
