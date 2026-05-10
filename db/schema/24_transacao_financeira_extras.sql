USE safracerta;

-- Expansão da tabela transacao_financeira para suportar o módulo Financeiro completo:
--   * descrição livre, forma de pagamento, status, datas de vencimento/pagamento, observações
--   * vínculo opcional com safra (rastrear venda de produção / custos por safra)
--   * soft delete + auditoria de exclusão (mesmo padrão de safra)

ALTER TABLE transacao_financeira
  ADD COLUMN descricao VARCHAR(500) NULL AFTER valor,
  ADD COLUMN forma_pagamento VARCHAR(40) NULL AFTER descricao,
  ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PAGO' AFTER forma_pagamento,
  ADD COLUMN data_vencimento DATE NULL AFTER data_transacao,
  ADD COLUMN data_pagamento DATE NULL AFTER data_vencimento,
  ADD COLUMN observacoes VARCHAR(1000) NULL AFTER origem,
  ADD COLUMN safra_id BIGINT NULL AFTER fazenda_id,
  ADD COLUMN excluido TINYINT(1) NOT NULL DEFAULT 0,
  ADD COLUMN excluido_por_id BIGINT NULL,
  ADD COLUMN justificativa_exclusao VARCHAR(500) NULL,
  ADD COLUMN excluido_em DATETIME NULL,
  ADD CONSTRAINT fk_transacao_safra
    FOREIGN KEY (safra_id) REFERENCES safra (id),
  ADD CONSTRAINT fk_transacao_excluido_por
    FOREIGN KEY (excluido_por_id) REFERENCES usuario (id);

CREATE INDEX idx_transacao_fazenda_data ON transacao_financeira (fazenda_id, data_transacao);
CREATE INDEX idx_transacao_tipo ON transacao_financeira (tipo);
CREATE INDEX idx_transacao_status ON transacao_financeira (status);
CREATE INDEX idx_transacao_excluido ON transacao_financeira (excluido);
