
USE safracerta;

ALTER TABLE fazenda
  ADD UNIQUE INDEX uk_fazenda_cod_fazenda (cod_fazenda);
