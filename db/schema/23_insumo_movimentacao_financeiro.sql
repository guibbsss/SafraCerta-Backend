USE safracerta;

ALTER TABLE movimentacao_estoque
  ADD COLUMN valor_unitario DECIMAL(18, 4) NULL AFTER quantidade,
  ADD COLUMN fornecedor VARCHAR(200) NULL AFTER observacao;

ALTER TABLE insumo
  ADD COLUMN valor_unitario_referencia DECIMAL(18, 4) NULL AFTER unidade_medida;
