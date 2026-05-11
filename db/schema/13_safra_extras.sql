USE safracerta;



ALTER TABLE safra
  ADD COLUMN nome VARCHAR(160) NOT NULL DEFAULT '' AFTER talhao_id,
  ADD COLUMN status ENUM('PLANTADA','CRESCIMENTO','COLHEITA','FINALIZADA')
    NOT NULL DEFAULT 'PLANTADA' AFTER cultura,
  ADD COLUMN producao_estimada DECIMAL(14, 4) NULL,
  ADD COLUMN producao_real DECIMAL(14, 4) NULL;

-- Renomeia data_colheita -> data_colheita_prevista e cria data_colheita_real
-- para diferenciar a previsão da colheita efetivamente realizada.
ALTER TABLE safra
  CHANGE data_colheita data_colheita_prevista DATE NULL,
  ADD COLUMN data_colheita_real DATE NULL AFTER data_colheita_prevista;
