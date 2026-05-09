USE safracerta;

-- Popular vínculos para fazendas já existentes (usa coluna proprietario como usuário vinculado).
-- Execute depois de criar `fazenda_has_usuario` e apenas onde ainda não existir o par.

INSERT INTO fazenda_has_usuario (fazenda_id, usuario_id)
SELECT f.id, f.proprietario
FROM fazenda f
WHERE f.proprietario IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM fazenda_has_usuario h
    WHERE h.fazenda_id = f.id AND h.usuario_id = f.proprietario
  );
