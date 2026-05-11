-- ============================================================================
-- Safra Certa - Seed de demonstracao
-- ============================================================================
-- Objetivo: deixar o banco 'safracerta' pronto para apresentar ao cliente.
-- Popula TODAS as 13 tabelas com dados realistas distribuidos em 6 meses.
--
-- COMO APLICAR (siga na ordem):
--   1) Aplique todas as migracoes em db/schema/ (01..25) se ainda nao aplicou.
--   2) Rode este arquivo inteiro no MySQL Workbench (conectado ao banco safracerta).
--   3) Suba o backend:   mvn spring-boot:run   (pasta SafraCerta-Backend)
--   4) Crie o usuario admin via API (o backend gera o hash BCrypt correto).
--      O codigoAcesso obrigatorio (10-15 chars) deve ser um cod_fazenda existente.
--      Use 'FZ-001-DEMO' (Fazenda Sao Joao) ou 'FZ-002-DEMO' (Santa Maria):
--
--        curl.exe -X POST http://localhost:8080/auth/register ^
--          -H "Content-Type: application/json" ^
--          -d "{\"nome\":\"Administrador Demo\",\"email\":\"admin@safracerta.com\",\"senha\":\"Safra@123\",\"codigoAcesso\":\"FZ-001-DEMO\"}"
--
--      OU, mais simples, use a tela de cadastro do frontend preenchendo:
--          Nome:    Administrador Demo
--          Email:   admin@safracerta.com
--          Senha:   Safra@123
--          Codigo:  FZ-001-DEMO
--
--   5) Rode o bloco "POS-REGISTRO" no final deste arquivo para ativar
--      o admin, dar perfil Administrador, vincular tambem a outra fazenda
--      e atribuir autor dos soft-deletes.
--   6) Login no frontend:  admin@safracerta.com / Safra@123
--
-- ENUMs em uso (case sensitive):
--   safra.status              : PLANTADA | CRESCIMENTO | COLHEITA | FINALIZADA
--   movimentacao_estoque.tipo : ENTRADA | SAIDA
--   transacao_financeira.tipo : RECEITA | DESPESA
--   transacao_financeira.status: PENDENTE | PAGO | ATRASADO | CANCELADO
-- ============================================================================

USE safracerta;

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------------------------------------------------------
-- 1) LIMPEZA
-- ----------------------------------------------------------------------------
TRUNCATE TABLE transacao_financeira;
TRUNCATE TABLE movimentacao_estoque;
TRUNCATE TABLE insumo;
TRUNCATE TABLE atividade_agricola;
TRUNCATE TABLE safra;
TRUNCATE TABLE talhao;
TRUNCATE TABLE fazenda_has_usuario;
TRUNCATE TABLE perfil_has_permissao;
TRUNCATE TABLE permissao;
TRUNCATE TABLE permissao_categoria;
TRUNCATE TABLE usuario;
TRUNCATE TABLE fazenda;
TRUNCATE TABLE perfil;

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------------------------
-- 2) PERFIS
-- ----------------------------------------------------------------------------
INSERT INTO perfil (id, nome, ativo, excluido) VALUES
 (1, 'Administrador', 1, 0),
 (2, 'Gerente',       1, 0),
 (3, 'Operador',      1, 0);

ALTER TABLE perfil AUTO_INCREMENT = 100;

-- ----------------------------------------------------------------------------
-- 3) CATEGORIAS DE PERMISSAO
-- ----------------------------------------------------------------------------
INSERT INTO permissao_categoria (id, nome, descricao, ativo, excluido) VALUES
 (1, 'Acesso',    'Acesso as telas do sistema',          1, 0),
 (2, 'Acoes',     'Acoes transversais (criar, editar, excluir)', 1, 0),
 (3, 'Dashboard', 'Painel executivo',                    1, 0);

ALTER TABLE permissao_categoria AUTO_INCREMENT = 100;

-- ----------------------------------------------------------------------------
-- 4) PERMISSOES (IDs FIXOS - precisam bater com frontend/src/app/constants/permissoes.ts)
--    P.VER_TALHAO=15, VER_SAFRA=16, VER_FAZENDA=17, VER_ATIVIDADES=18,
--    VER_ESTOQUE=19, VER_FINANCEIRO=20, VER_ADMINISTRACAO=21,
--    EDITAR=22, EXCLUIR=23, CRIAR=24, VER_DASHBOARD=25
-- ----------------------------------------------------------------------------
INSERT INTO permissao (id, permissao_categoria_id, nome, descricao, ativo, excluido) VALUES
 (15, 1, 'VER_TALHAO',        'Visualizar tela de Talhoes',    1, 0),
 (16, 1, 'VER_SAFRA',         'Visualizar tela de Safras',     1, 0),
 (17, 1, 'VER_FAZENDA',       'Visualizar tela de Fazendas',   1, 0),
 (18, 1, 'VER_ATIVIDADES',    'Visualizar tela de Atividades', 1, 0),
 (19, 1, 'VER_ESTOQUE',       'Visualizar tela de Estoque',    1, 0),
 (20, 1, 'VER_FINANCEIRO',    'Visualizar tela de Financeiro', 1, 0),
 (21, 1, 'VER_ADMINISTRACAO', 'Visualizar tela de Administracao', 1, 0),
 (22, 2, 'EDITAR',            'Editar registros',              1, 0),
 (23, 2, 'EXCLUIR',           'Excluir registros',             1, 0),
 (24, 2, 'CRIAR',             'Criar novos registros',         1, 0),
 (25, 3, 'VER_DASHBOARD',     'Visualizar Dashboard executivo',1, 0);

ALTER TABLE permissao AUTO_INCREMENT = 100;

-- ----------------------------------------------------------------------------
-- 5) PERFIL_HAS_PERMISSAO
--    Admin    : todas as 11 (15..25)
--    Gerente  : todas as VER_* (15..20) + CRIAR (24) + EDITAR (22) + VER_DASHBOARD (25)
--               -> SEM excluir (23) e SEM administracao (21)
--    Operador : somente VER_TALHAO, VER_SAFRA, VER_FAZENDA, VER_ATIVIDADES,
--               VER_ESTOQUE, VER_FINANCEIRO, VER_DASHBOARD
--               -> SEM criar/editar/excluir e SEM administracao
-- ----------------------------------------------------------------------------
INSERT INTO perfil_has_permissao (perfil_id, permissao_id, ativo, excluido)
SELECT 1, id, 1, 0 FROM permissao;

INSERT INTO perfil_has_permissao (perfil_id, permissao_id, ativo, excluido)
SELECT 2, id, 1, 0
  FROM permissao
 WHERE id IN (15, 16, 17, 18, 19, 20, 22, 24, 25);

INSERT INTO perfil_has_permissao (perfil_id, permissao_id, ativo, excluido)
SELECT 3, id, 1, 0
  FROM permissao
 WHERE id IN (15, 16, 17, 18, 19, 20, 25);

-- ----------------------------------------------------------------------------
-- 6) FAZENDAS
-- ----------------------------------------------------------------------------
-- IMPORTANTE: cod_fazenda e o "codigo de acesso" exigido no cadastro do usuario
-- pela tela de registro. A validacao do backend (RegistroUsuarioRequestDto)
-- exige entre 10 e 15 caracteres - por isso usamos 'FZ-001-DEMO' / 'FZ-002-DEMO'.
INSERT INTO fazenda (id, nome, localizacao, area_total, proprietario, cod_fazenda) VALUES
 (1, 'Fazenda Sao Joao',    'Uberaba - MG',   320.5000, NULL, 'FZ-001-DEMO'),
 (2, 'Fazenda Santa Maria', 'Rio Verde - GO', 480.0000, NULL, 'FZ-002-DEMO');

ALTER TABLE fazenda AUTO_INCREMENT = 100;

-- ----------------------------------------------------------------------------
-- 7) TALHOES
-- ----------------------------------------------------------------------------
INSERT INTO talhao (id, fazenda_id, nome, area_hectares, tipo_cultivo) VALUES
 (1, 1, 'Talhao A1',  80.0000, 'Soja'),
 (2, 1, 'Talhao A2',  75.0000, 'Milho'),
 (3, 1, 'Talhao A3',  60.0000, 'Soja'),
 (4, 1, 'Talhao A4',  50.0000, 'Sorgo'),
 (5, 2, 'Talhao B1', 120.0000, 'Soja'),
 (6, 2, 'Talhao B2', 100.0000, 'Milho'),
 (7, 2, 'Talhao B3',  90.0000, 'Algodao');

ALTER TABLE talhao AUTO_INCREMENT = 100;

-- ----------------------------------------------------------------------------
-- 8) SAFRAS (6 registros, 1 soft-deleted para demonstrar o recurso)
--    status: PLANTADA | CRESCIMENTO | COLHEITA | FINALIZADA
-- ----------------------------------------------------------------------------
INSERT INTO safra (id, talhao_id, nome, cultura, status,
                   data_plantio, data_colheita_prevista, data_colheita_real,
                   producao_estimada, producao_real,
                   excluido, excluido_por_id, justificativa_exclusao, excluido_em) VALUES
 (1, 1, 'Safra Soja 2025/26 - A1',         'Soja',    'COLHEITA',    '2025-10-15', '2026-03-20', NULL,        4800.0000, NULL,      0, NULL, NULL, NULL),
 (2, 2, 'Safra Milho 2025/26 - A2',        'Milho',   'CRESCIMENTO', '2026-02-05', '2026-07-10', NULL,        6750.0000, NULL,      0, NULL, NULL, NULL),
 (3, 3, 'Safra Soja 2024/25 - A3',         'Soja',    'FINALIZADA',  '2024-10-20', '2025-03-15', '2025-03-18',3600.0000, 3720.0000, 0, NULL, NULL, NULL),
 (4, 5, 'Safra Soja 2025/26 - B1',         'Soja',    'CRESCIMENTO', '2025-11-05', '2026-04-01', NULL,        7200.0000, NULL,      0, NULL, NULL, NULL),
 (5, 6, 'Safra Milho Safrinha 2026 - B2',  'Milho',   'PLANTADA',    '2026-03-10', '2026-08-20', NULL,        9000.0000, NULL,      0, NULL, NULL, NULL),
 (6, 7, 'Safra Algodao 2024/25 - B3',      'Algodao', 'FINALIZADA',  '2024-12-01', '2025-06-10', '2025-06-15',4500.0000, 4350.0000, 1, NULL, 'Lancada em duplicidade - registro substituido pelo sistema integrado da cooperativa', '2025-07-01 14:30:00');

ALTER TABLE safra AUTO_INCREMENT = 100;

-- ----------------------------------------------------------------------------
-- 9) ATIVIDADES AGRICOLAS (15 registros)
-- ----------------------------------------------------------------------------
INSERT INTO atividade_agricola (id, talhao_id, tipo_operacao, data_atividade, descricao) VALUES
 ( 1, 1, 'Plantio',         '2025-10-15', 'Plantio direto de soja Intacta RR2 PRO com semeadora pneumatica'),
 ( 2, 1, 'Adubacao',        '2025-10-20', 'Aplicacao de fertilizante NPK 04-30-10 no sulco de plantio'),
 ( 3, 1, 'Pulverizacao',    '2025-11-10', 'Pulverizacao preventiva de fungicidas (Opera 0.6L/ha)'),
 ( 4, 1, 'Irrigacao',       '2025-12-05', 'Irrigacao complementar por aspersao - lamina de 25mm'),
 ( 5, 2, 'Preparo de solo', '2026-01-28', 'Gradagem e nivelamento do talhao A2 para o milho safrinha'),
 ( 6, 2, 'Plantio',         '2026-02-05', 'Plantio de milho hibrido AG 8088 a 65.000 sementes/ha'),
 ( 7, 2, 'Adubacao',        '2026-02-15', 'Cobertura nitrogenada com ureia (180 kg/ha)'),
 ( 8, 3, 'Colheita',        '2025-03-18', 'Colheita mecanizada da soja - produtividade 62 sc/ha'),
 ( 9, 5, 'Plantio',         '2025-11-05', 'Plantio de soja Intacta RR2 PRO no talhao B1'),
 (10, 5, 'Adubacao',        '2025-11-12', 'Aplicacao de MAP (fosfato monoamonico) em cobertura'),
 (11, 5, 'Pulverizacao',    '2026-01-15', 'Pulverizacao de inseticida para controle de lagartas'),
 (12, 6, 'Plantio',         '2026-03-10', 'Plantio do milho safrinha no talhao B2'),
 (13, 7, 'Capina',          '2025-02-15', 'Controle de plantas daninhas no algodao (capina mecanica)'),
 (14, 4, 'Calagem',         '2025-09-12', 'Aplicacao de calcario dolomitico - 3 t/ha'),
 (15, 4, 'Plantio',         '2025-10-01', 'Plantio de sorgo granifero no talhao A4');

ALTER TABLE atividade_agricola AUTO_INCREMENT = 100;

-- ----------------------------------------------------------------------------
-- 10) INSUMOS (8 itens cobrindo sementes, fertilizantes, defensivos, etc.)
-- ----------------------------------------------------------------------------
INSERT INTO insumo (id, fazenda_id, nome, categoria, quantidade_atual, unidade_medida, valor_unitario_referencia) VALUES
 (1, 1, 'Semente Soja Intacta RR2 PRO', 'Sementes',     1200.0000, 'kg',   28.5000),
 (2, 1, 'Ureia 45%',                    'Fertilizantes',4500.0000, 'kg',    4.2000),
 (3, 1, 'Fosfato Monoamonico (MAP)',    'Fertilizantes',3200.0000, 'kg',    6.8000),
 (4, 1, 'Glifosato 480 g/L',            'Defensivos',    350.0000, 'L',    28.0000),
 (5, 2, 'Semente Milho AG 8088',        'Sementes',      900.0000, 'kg',  850.0000),
 (6, 2, 'Fungicida Opera',              'Defensivos',    180.0000, 'L',   175.0000),
 (7, 2, 'Calcario Dolomitico',          'Corretivos',  12000.0000, 'kg',    0.4500),
 (8, 1, 'Diesel S-10',                  'Combustivel',  2800.0000, 'L',     6.1000);

ALTER TABLE insumo AUTO_INCREMENT = 100;

-- ----------------------------------------------------------------------------
-- 11) MOVIMENTACOES DE ESTOQUE (18 registros)
--    tipo_movimentacao: ENTRADA | SAIDA
-- ----------------------------------------------------------------------------
INSERT INTO movimentacao_estoque (id, insumo_id, safra_id, tipo_movimentacao, quantidade, valor_unitario, data_movimentacao, observacao, fornecedor) VALUES
 ( 1, 1, NULL, 'ENTRADA', 2000.0000, 28.5000, '2025-09-20 10:00:00', 'Compra de sementes para safra 2025/26',            'Agrosem Distribuidora'),
 ( 2, 1, 1,    'SAIDA',    800.0000, NULL,    '2025-10-15 07:30:00', 'Plantio talhao A1 - 80 ha x 10 kg/ha',             NULL),
 ( 3, 2, NULL, 'ENTRADA', 6000.0000,  4.2000, '2025-09-25 14:00:00', 'Reposicao de estoque de ureia',                    'Heringer Fertilizantes'),
 ( 4, 2, 1,    'SAIDA',   1500.0000, NULL,    '2025-10-20 08:00:00', 'Adubacao de plantio soja A1',                      NULL),
 ( 5, 3, NULL, 'ENTRADA', 4000.0000,  6.8000, '2025-10-05 09:00:00', 'Compra de MAP para safra principal',               'Yara Brasil'),
 ( 6, 3, 1,    'SAIDA',    800.0000, NULL,    '2025-10-20 08:30:00', 'Adubacao fosfatada talhao A1',                     NULL),
 ( 7, 4, NULL, 'ENTRADA',  500.0000, 28.0000, '2025-11-01 11:00:00', 'Estoque de defensivos para temporada',             'Syngenta'),
 ( 8, 4, 1,    'SAIDA',    150.0000, NULL,    '2025-11-10 06:00:00', 'Pulverizacao preventiva A1',                       NULL),
 ( 9, 5, NULL, 'ENTRADA', 1000.0000, 850.0000,'2026-01-15 15:00:00', 'Aquisicao de sementes para safrinha',              'Agroceres'),
 (10, 5, 2,    'SAIDA',    100.0000, NULL,    '2026-02-05 07:00:00', 'Plantio milho talhao A2',                          NULL),
 (11, 6, NULL, 'ENTRADA',  200.0000, 175.0000,'2025-12-10 13:00:00', 'Estoque preventivo de fungicida',                  'BASF'),
 (12, 6, 4,    'SAIDA',     20.0000, NULL,    '2026-01-15 06:30:00', 'Pulverizacao preventiva B1',                       NULL),
 (13, 7, NULL, 'ENTRADA',15000.0000,  0.4500, '2025-08-20 10:00:00', 'Corretivo de solo - calagem programada',           'Carbomil'),
 (14, 7, NULL, 'SAIDA',  3000.0000, NULL,    '2025-09-12 09:00:00', 'Calagem talhao A4',                                NULL),
 (15, 8, NULL, 'ENTRADA', 3000.0000,  6.1000, '2025-09-15 17:00:00', 'Abastecimento mensal de diesel',                   'Petrobras'),
 (16, 8, 1,    'SAIDA',    200.0000, NULL,    '2025-10-15 06:00:00', 'Operacao de plantio talhao A1',                    NULL),
 (17, 8, 2,    'SAIDA',    180.0000, NULL,    '2026-02-05 06:00:00', 'Operacao de plantio talhao A2',                    NULL),
 (18, 2, 4,    'SAIDA',   1200.0000, NULL,    '2025-11-12 08:30:00', 'Cobertura nitrogenada talhao B1',                  NULL);

ALTER TABLE movimentacao_estoque AUTO_INCREMENT = 100;

-- ----------------------------------------------------------------------------
-- 12) TRANSACOES FINANCEIRAS (28 registros: receitas + despesas)
--    tipo  : RECEITA | DESPESA
--    status: PAGO | PENDENTE | ATRASADO | CANCELADO
-- ----------------------------------------------------------------------------
INSERT INTO transacao_financeira (id, fazenda_id, safra_id, tipo, valor, descricao,
                                  forma_pagamento, status,
                                  data_transacao, data_vencimento, data_pagamento,
                                  categoria, origem, observacoes,
                                  excluido, excluido_por_id, justificativa_exclusao, excluido_em) VALUES
 ( 1, 1, 3,    'RECEITA', 245000.00, 'Venda safra soja 2024/25',          'TRANSFERENCIA', 'PAGO',     '2025-04-10', NULL,         '2025-04-10', 'Vendas de Graos',          'Cooperativa Coopadap',         'Comercializacao final de safra', 0, NULL, NULL, NULL),
 ( 2, 2, 6,    'RECEITA', 185000.00, 'Venda algodao 2024/25',             'TRANSFERENCIA', 'PAGO',     '2025-07-05', NULL,         '2025-07-05', 'Vendas de Graos',          'Algodoeira Brasil',            NULL,                              0, NULL, NULL, NULL),
 ( 3, 1, NULL, 'RECEITA',  12500.00, 'Venda de milho excedente',          'PIX',           'PAGO',     '2025-09-12', NULL,         '2025-09-12', 'Vendas de Graos',          'Mercado Local',                NULL,                              0, NULL, NULL, NULL),
 ( 4, 1, NULL, 'RECEITA',   8000.00, 'Aluguel de maquinario',             'BOLETO',        'PAGO',     '2025-10-20', NULL,         '2025-10-20', 'Servicos',                 'Fazenda Vizinha',              NULL,                              0, NULL, NULL, NULL),
 ( 5, 2, NULL, 'RECEITA',   4500.00, 'Venda de bezerros',                 'DINHEIRO',      'PAGO',     '2025-11-15', NULL,         '2025-11-15', 'Pecuaria',                 'Frigorifico Goias',            NULL,                              0, NULL, NULL, NULL),
 ( 6, 1, 3,    'RECEITA',  60000.00, 'Pagamento parcial safra soja',      'TRANSFERENCIA', 'PAGO',     '2025-12-10', NULL,         '2025-12-10', 'Vendas de Graos',          'Cargill',                      'Parcela 1 de 3',                  0, NULL, NULL, NULL),
 ( 7, 1, NULL, 'DESPESA',  57000.00, 'Compra de sementes de soja',        'BOLETO',        'PAGO',     '2025-09-20', '2025-10-05', '2025-09-25', 'Insumos - Sementes',       'Agrosem Distribuidora',        'NF 12345',                        0, NULL, NULL, NULL),
 ( 8, 1, NULL, 'DESPESA',  25200.00, 'Compra de ureia',                   'BOLETO',        'PAGO',     '2025-09-25', '2025-10-10', '2025-09-30', 'Insumos - Fertilizantes',  'Heringer Fertilizantes',       NULL,                              0, NULL, NULL, NULL),
 ( 9, 1, NULL, 'DESPESA',  27200.00, 'Compra de MAP',                     'BOLETO',        'PAGO',     '2025-10-05', '2025-10-20', '2025-10-08', 'Insumos - Fertilizantes',  'Yara Brasil',                  NULL,                              0, NULL, NULL, NULL),
 (10, 1, NULL, 'DESPESA',  14000.00, 'Compra de glifosato',               'PIX',           'PAGO',     '2025-11-01', NULL,         '2025-11-01', 'Insumos - Defensivos',     'Syngenta',                     NULL,                              0, NULL, NULL, NULL),
 (11, 1, 1,    'DESPESA',   9500.00, 'Manutencao trator John Deere',      'BOLETO',        'PAGO',     '2025-10-25', '2025-11-10', '2025-11-08', 'Manutencao de Maquinario', 'Concessionaria JD Uberaba',    NULL,                              0, NULL, NULL, NULL),
 (12, 1, NULL, 'DESPESA',  18300.00, 'Diesel S-10 - abastecimento',       'CARTAO',        'PAGO',     '2025-09-15', '2025-09-30', '2025-09-25', 'Combustivel',              'Petrobras',                    NULL,                              0, NULL, NULL, NULL),
 (13, 1, 1,    'DESPESA',   6800.00, 'Mao de obra plantio A1',            'TRANSFERENCIA', 'PAGO',     '2025-10-30', '2025-10-30', '2025-10-30', 'Mao de Obra',              'Folha de pagamento',           NULL,                              0, NULL, NULL, NULL),
 (14, 1, 1,    'DESPESA',   4200.00, 'Manutencao do pulverizador',        'BOLETO',        'PENDENTE', '2025-11-20', '2025-12-05', NULL,         'Manutencao de Maquinario', 'Oficina Mecanica Central',     'Aguardando pagamento',            0, NULL, NULL, NULL),
 (15, 2, NULL, 'DESPESA',  85000.00, 'Compra de sementes de milho',       'BOLETO',        'PAGO',     '2026-01-15', '2026-02-05', '2026-01-25', 'Insumos - Sementes',       'Agroceres',                    NULL,                              0, NULL, NULL, NULL),
 (16, 2, NULL, 'DESPESA',  35000.00, 'Compra de fungicida Opera',         'BOLETO',        'PAGO',     '2025-12-10', '2025-12-25', '2025-12-18', 'Insumos - Defensivos',     'BASF',                         NULL,                              0, NULL, NULL, NULL),
 (17, 2, NULL, 'DESPESA',   6750.00, 'Compra de calcario dolomitico',     'BOLETO',        'PAGO',     '2025-08-20', '2025-09-05', '2025-08-28', 'Insumos - Corretivos',     'Carbomil',                     NULL,                              0, NULL, NULL, NULL),
 (18, 2, 4,    'DESPESA',   9200.00, 'Mao de obra plantio B1',            'TRANSFERENCIA', 'PAGO',     '2025-11-15', '2025-11-15', '2025-11-15', 'Mao de Obra',              'Folha de pagamento',           NULL,                              0, NULL, NULL, NULL),
 (19, 2, NULL, 'DESPESA',  12500.00, 'Conserto colheitadeira',            'BOLETO',        'PAGO',     '2025-07-20', '2025-08-05', '2025-08-02', 'Manutencao de Maquinario', 'Concessionaria Rio Verde',     NULL,                              0, NULL, NULL, NULL),
 (20, 1, NULL, 'DESPESA',   3200.00, 'Energia eletrica - irrigacao',      'BOLETO',        'PAGO',     '2025-12-10', '2025-12-20', '2025-12-15', 'Utilidades',               'CEMIG',                        NULL,                              0, NULL, NULL, NULL),
 (21, 1, NULL, 'DESPESA',   2800.00, 'Energia eletrica',                  'BOLETO',        'PAGO',     '2026-01-10', '2026-01-20', '2026-01-18', 'Utilidades',               'CEMIG',                        NULL,                              0, NULL, NULL, NULL),
 (22, 2, NULL, 'DESPESA',   4500.00, 'Energia eletrica',                  'BOLETO',        'ATRASADO', '2026-02-10', '2026-02-20', NULL,         'Utilidades',               'Enel Goias',                   'Em negociacao com a concessionaria', 0, NULL, NULL, NULL),
 (23, 1, NULL, 'DESPESA',   7800.00, 'ITR - Imposto Territorial Rural',   'BOLETO',        'PAGO',     '2025-09-30', '2025-09-30', '2025-09-30', 'Impostos',                 'Receita Federal',              NULL,                              0, NULL, NULL, NULL),
 (24, 2, NULL, 'DESPESA',  12400.00, 'ITR - Imposto Territorial Rural',   'BOLETO',        'PAGO',     '2025-09-30', '2025-09-30', '2025-09-30', 'Impostos',                 'Receita Federal',              NULL,                              0, NULL, NULL, NULL),
 (25, 1, 2,    'DESPESA',   5400.00, 'Mao de obra plantio milho A2',      'TRANSFERENCIA', 'PAGO',     '2026-02-10', '2026-02-10', '2026-02-10', 'Mao de Obra',              'Folha de pagamento',           NULL,                              0, NULL, NULL, NULL),
 (26, 2, NULL, 'RECEITA',  95000.00, 'Adiantamento venda milho safrinha', 'TRANSFERENCIA', 'PENDENTE', '2026-04-15', '2026-04-15', NULL,         'Vendas de Graos',          'ADM',                          'Aguardando entrega',              0, NULL, NULL, NULL),
 (27, 1, 1,    'RECEITA', 320000.00, 'Pre-venda soja 2025/26',            'TRANSFERENCIA', 'PENDENTE', '2026-04-01', '2026-04-30', NULL,         'Vendas de Graos',          'Bunge',                        'Contrato fechado - aguardando entrega', 0, NULL, NULL, NULL),
 (28, 1, NULL, 'RECEITA',   5500.00, 'Venda lancada por engano',          'PIX',           'CANCELADO','2026-03-15', NULL,         NULL,         'Vendas de Graos',          'Erro de digitacao',            'Estornado',                       1, NULL, 'Registro lancado em duplicidade na transcricao do livro caixa', '2026-03-16 09:15:00');

ALTER TABLE transacao_financeira AUTO_INCREMENT = 100;

-- ----------------------------------------------------------------------------
-- 13) VERIFICACAO - contagem por tabela (rode este SELECT depois do seed)
-- ----------------------------------------------------------------------------
SELECT 'perfil'                AS tabela, COUNT(*) AS total FROM perfil
UNION ALL SELECT 'permissao_categoria',   COUNT(*) FROM permissao_categoria
UNION ALL SELECT 'permissao',             COUNT(*) FROM permissao
UNION ALL SELECT 'perfil_has_permissao',  COUNT(*) FROM perfil_has_permissao
UNION ALL SELECT 'usuario',               COUNT(*) FROM usuario
UNION ALL SELECT 'fazenda',               COUNT(*) FROM fazenda
UNION ALL SELECT 'fazenda_has_usuario',   COUNT(*) FROM fazenda_has_usuario
UNION ALL SELECT 'talhao',                COUNT(*) FROM talhao
UNION ALL SELECT 'safra',                 COUNT(*) FROM safra
UNION ALL SELECT 'atividade_agricola',    COUNT(*) FROM atividade_agricola
UNION ALL SELECT 'insumo',                COUNT(*) FROM insumo
UNION ALL SELECT 'movimentacao_estoque',  COUNT(*) FROM movimentacao_estoque
UNION ALL SELECT 'transacao_financeira',  COUNT(*) FROM transacao_financeira;

-- Esperado:
--   perfil                  3
--   permissao_categoria     3
--   permissao              11   (IDs 15..25 alinhados com o frontend)
--   perfil_has_permissao   27   (11 admin + 9 gerente + 7 operador)
--   usuario                 0   (admin sera criado via API - veja bloco abaixo)
--   fazenda                 2
--   fazenda_has_usuario     0   (preenchido apos criar admin)
--   talhao                  7
--   safra                   6
--   atividade_agricola     15
--   insumo                  8
--   movimentacao_estoque   18
--   transacao_financeira   28


-- ============================================================================
-- POS-REGISTRO (rodar SOMENTE depois do POST /auth/register do admin)
-- ============================================================================
-- 1) Cria o admin via API (codigoAcesso = cod_fazenda de uma das fazendas):
--
--      POST http://localhost:8080/auth/register
--      {
--        "nome": "Administrador Demo",
--        "email": "admin@safracerta.com",
--        "senha": "Safra@123",
--        "codigoAcesso": "FZ-001-DEMO"
--      }
--
--    Observacao: o /auth/register ja cria o vinculo do usuario com a fazenda
--    do codigoAcesso informado (fazenda_has_usuario). Os updates abaixo apenas
--    ativam o usuario, promovem para Administrador, vinculam a 2a fazenda
--    e atribuem o autor dos soft-deletes.
--
-- 2) Rode os comandos abaixo no DBeaver / Workbench:

USE safracerta;

-- Ativa o admin e promove para Administrador (perfil_id = 1)
UPDATE usuario
   SET ativo     = 1,
       perfil_id = 1
 WHERE email = 'admin@safracerta.com';

-- Vincula o admin a SEGUNDA fazenda (a primeira ja foi vinculada no register)
INSERT IGNORE INTO fazenda_has_usuario (fazenda_id, usuario_id)
SELECT f.id, u.id
  FROM fazenda f, usuario u
 WHERE u.email = 'admin@safracerta.com';

-- Define o admin como autor dos soft-deletes de demonstracao
UPDATE safra
   SET excluido_por_id = (SELECT id FROM usuario WHERE email = 'admin@safracerta.com')
 WHERE id = 6;

UPDATE transacao_financeira
   SET excluido_por_id = (SELECT id FROM usuario WHERE email = 'admin@safracerta.com')
 WHERE id = 28;

-- (Opcional) Marca o admin como proprietario nominal das fazendas
UPDATE fazenda
   SET proprietario = (SELECT id FROM usuario WHERE email = 'admin@safracerta.com')
 WHERE proprietario IS NULL;

-- 3) Login no frontend:
--      Email: admin@safracerta.com
--      Senha: Safra@123
-- ============================================================================
