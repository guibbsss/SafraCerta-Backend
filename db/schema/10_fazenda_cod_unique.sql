-- Execute após criar a coluna cod_fazenda (03_fazenda.sql).
-- Garante unicidade do código de acesso no banco.

USE safracerta;

ALTER TABLE fazenda
  ADD UNIQUE INDEX uk_fazenda_cod_fazenda (cod_fazenda);
