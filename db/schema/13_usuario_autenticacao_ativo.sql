USE safracerta;

ALTER TABLE usuario
  ADD COLUMN autenticacao TEXT NULL AFTER senha,
  ADD COLUMN ativo TINYINT(1) NOT NULL DEFAULT 0 AFTER autenticacao;
