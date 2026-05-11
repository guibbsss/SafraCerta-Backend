USE safracerta;



INSERT INTO fazenda_has_usuario (fazenda_id, usuario_id)
SELECT f.id, f.proprietario
FROM fazenda f
WHERE f.proprietario IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM fazenda_has_usuario h
    WHERE h.fazenda_id = f.id AND h.usuario_id = f.proprietario
  );
