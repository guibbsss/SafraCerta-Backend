USE safracerta;

-- Adiciona campos extras para alinhar com o formulário de Nova Safra do frontend.
-- O DEFAULT '' em `nome` existe apenas para permitir aplicar a alteração em
-- bases já populadas; a entidade JPA exige valor não-nulo nos novos cadastros.

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
