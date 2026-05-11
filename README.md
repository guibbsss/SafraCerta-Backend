# Safra Certa — Backend

API REST do sistema de gestão rural **Safra Certa**, escrita em Java 17 com Spring Boot 3.5.

A API expõe endpoints para autenticação JWT, cadastro de fazendas, talhões, safras,
atividades agrícolas, estoque de insumos, transações financeiras e administração de
perfis/permissões. A documentação interativa fica em **Swagger UI** após o boot.

Repositório irmão: **[SafraCerta-docs](https://github.com/guibbsss/SafraCerta-docs)** — pacote de entrega (manual, testes, apresentação, doc técnica). Release sugerida: tag **`v1.0-mvp`**.

---

## 1. Stack

| Camada       | Tecnologia                                   |
| ------------ | -------------------------------------------- |
| Linguagem    | Java 17                                      |
| Framework    | Spring Boot 3.5.14                           |
| Segurança    | Spring Security + JJWT 0.12.6 (JWT stateless)|
| Persistência | Spring Data JPA + Hibernate (MySQLDialect)   |
| Banco        | MySQL 8                                      |
| Doc API      | springdoc-openapi 2.8.5 (Swagger UI)         |
| Build        | Maven (wrapper `mvnw`)                       |
| Testes       | JUnit 5 + Mockito (serviços críticos)        |

---

## 2. Pré-requisitos

- **JDK 17** (recomendado: Temurin/OpenJDK 17).
- **MySQL 8** em execução (porta padrão `3306`).
- **Maven**: não é necessário instalar; use o wrapper `./mvnw` (Linux/macOS) ou
  `.\mvnw.cmd` (Windows) já incluído no repositório.

---

## 3. Variáveis de ambiente

Todas têm valores **default** em `src/main/resources/application.yml`, mas em produção
devem ser sobrescritas via ambiente.

| Variável                       | Default                                                                | Descrição                                       |
| ------------------------------ | ---------------------------------------------------------------------- | ----------------------------------------------- |
| `SPRING_DATASOURCE_URL`        | `jdbc:mysql://localhost:3306/safracerta?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo` | URL JDBC do MySQL.                              |
| `SPRING_DATASOURCE_USERNAME`   | `root`                                                                 | Usuário do banco.                               |
| `SPRING_DATASOURCE_PASSWORD`   | *(definido no `application.yml`)*                                      | Senha do banco. **Trocar em produção.**         |
| `JWT_SECRET`                   | `SafraCertaJwtSecretChangeMe_Min32Chars_DevOnly_UseEnvInProd`          | Segredo HMAC do JWT (mínimo 32 caracteres).     |
| `JWT_EXPIRATION_MS`            | `86400000` (24 h)                                                      | Tempo de expiração do token.                    |
| `REGISTRO_PERFIL_ID`           | `2`                                                                    | Perfil atribuído por padrão em novos cadastros. |

Exemplo (PowerShell):

```powershell
$env:SPRING_DATASOURCE_PASSWORD = "minhaSenhaForte"
$env:JWT_SECRET = "outroSegredoDeNoMinimo32CaracteresEmProducao!"
.\mvnw.cmd spring-boot:run
```

---

## 4. Como rodar localmente

### 4.1. Criar e popular o banco

1. Crie o database e o usuário no MySQL.
2. Execute, **em ordem**, os scripts SQL em [`db/schema/`](db/schema/) — eles são
   versionados e numerados de `00_create_database.sql` a `25_movimentacao_estoque_safra.sql`.

   No Workbench, basta abrir cada arquivo e executar; via linha de comando:

   ```powershell
   Get-ChildItem db\schema\*.sql | Sort-Object Name | ForEach-Object {
     mysql -u root -p safracerta < $_.FullName
   }
   ```

3. Opcional: scripts SQL de seed (quando existirem no repositório, por exemplo em `db/seed/`) para perfis padrão ou dados de demonstração.

### 4.2. Subir a API

```powershell
# Windows
.\mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080` com context path `/api`:

- Base da API: `http://localhost:8080/api`
- Health (sem JWT): `GET http://localhost:8080/api/health` → `{"status":"UP"}`
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/v3/api-docs`

### 4.3. Rodar os testes

```powershell
.\mvnw.cmd test
```

São executados **15 testes unitários** em `AuthApplicationService`, `SafraService` e `TransacaoFinanceiraService` (via Mockito). Relatórios em `target/surefire-reports/`.

### 4.4. Empacotar como JAR

```powershell
.\mvnw.cmd clean package
java -jar target\safracerta-backend-0.0.1-SNAPSHOT.jar
```

---

## 5. Estrutura de pacotes

```
src/main/java/com/safracerta/
├── SafraCertaApplication.java
├── config/
│   └── security/        # SecurityConfig, JwtAuthenticationFilter, handlers
├── modules/
│   ├── health/          # GET /health (disponibilidade)
│   ├── auth/            # Login/Registro, AuthApplicationService, GET /auth/me
│   ├── user/            # Usuário (controller, service, repository)
│   ├── perfil/          # Perfis e permissões
│   ├── permissao/
│   ├── fazenda/         # Fazenda e vínculo com usuários
│   ├── talhao/
│   ├── safra/           # Inclui soft-delete com justificativa
│   ├── atividadeagricola/
│   ├── insumo/
│   ├── movimentacaoestoque/
│   ├── transacaofinanceira/   # CRUD + /resumo agregado
│   └── solicitacao/     # Solicitações de entrada na fazenda
└── shared/              # DTOs comuns, exceptions, utilitários

src/test/java/com/safracerta/modules/   # Testes unitários (auth, safra, transacaofinanceira)
```

---

## 6. Endpoints principais

Rotas públicas (sem JWT): `POST /auth/login`, `POST /auth/register`, `GET /health`, `/error`, documentação OpenAPI/Swagger e `OPTIONS /**`. Demais rotas exigem `Authorization: Bearer <token>`. Prefixo `/api`:

| Recurso              | Endpoints                                                                 |
| -------------------- | ------------------------------------------------------------------------- |
| Disponibilidade      | `GET /health`                                                             |
| Autenticação (público)| `POST /auth/login`, `POST /auth/register`                               |
| Autenticação (JWT)   | `GET /auth/me` — dados e permissões do usuário autenticado                 |
| Fazendas             | `GET/POST/PUT/DELETE /fazendas`                                           |
| Talhões              | `GET/POST/PUT/DELETE /talhoes`                                            |
| Safras               | `GET/POST/PUT /safras` + `DELETE /safras/{id}` (soft-delete c/ justificativa) |
| Atividades agrícolas | `GET/POST/PUT/DELETE /atividades-agricolas`                               |
| Insumos              | `GET/POST/PUT/DELETE /insumos`                                            |
| Movimentações estoque| `GET/POST /movimentacoes-estoque`                                         |
| Financeiro           | `GET/POST/PUT /transacoes-financeiras`, `GET /transacoes-financeiras/resumo`, `DELETE /transacoes-financeiras/{id}` (soft-delete) |
| Administração        | `/perfis`, `/permissoes`, `/usuarios`, `/solicitacoes-entrada`            |

Filtros disponíveis em `/transacoes-financeiras` e `/resumo`:
`fazendaId`, `tipo` (`RECEITA`/`DESPESA`), `status` (`PENDENTE`/`PAGO`/`ATRASADO`/`CANCELADO`),
`dataInicio`, `dataFim`.

---

## 7. Migrações SQL versionadas

As migrações ficam em [`db/schema/`](db/schema/) e devem ser executadas **em ordem
numérica**. O sistema **não** usa Flyway/Liquibase: a ordem é manual.

| Nº  | Arquivo                                          | Conteúdo resumido                                                              |
| --- | ------------------------------------------------ | ------------------------------------------------------------------------------ |
| 00  | `00_create_database.sql`                         | Cria o schema `safracerta`.                                                    |
| 01  | `01_perfil.sql`                                  | Tabela `perfil`.                                                               |
| 02  | `02_usuario.sql`                                 | Tabela `usuario`.                                                              |
| 03  | `03_fazenda.sql`                                 | Tabela `fazenda`.                                                              |
| 04  | `04_talhao.sql`                                  | Tabela `talhao`.                                                               |
| 05  | `05_safra.sql`                                   | Tabela `safra`.                                                                |
| 06  | `06_insumo.sql`                                  | Tabela `insumo`.                                                               |
| 07  | `07_movimentacao_estoque.sql`                    | Tabela `movimentacao_estoque`.                                                 |
| 08  | `08_transacao_financeira.sql`                    | Tabela `transacao_financeira`.                                                 |
| 09  | `09_atividade_agricola.sql`                      | Tabela `atividade_agricola`.                                                   |
| 10  | `10_fazenda_cod_unique.sql`                      | Unique constraint em `fazenda.codigo`.                                         |
| 11  | `11_fazenda_has_usuario.sql`                     | Tabela associativa fazenda ↔ usuário.                                          |
| 12  | `12_backfill_fazenda_has_usuario.sql`            | Backfill da associativa.                                                       |
| 13  | `13_safra_extras.sql`                            | Campos extras de safra (nome, status, produção, datas split).                  |
| 14  | `14_usuario_autenticacao_ativo.sql`              | Campos `ativo`/auth no usuário.                                                |
| 15  | `15_safra_talhao_nullable.sql`                   | (Histórico) `safra.talhao_id` permitiu NULL.                                   |
| 16  | `16_permissao_categoria.sql`                     | Coluna `categoria` em `permissao`.                                             |
| 17  | `17_safra_soft_delete.sql`                       | Primeira versão do soft-delete em `safra`.                                     |
| 18  | `18_permissao.sql`                               | Catálogo de permissões.                                                        |
| 19  | `19_perfil_has_permissao.sql`                    | Associativa perfil ↔ permissão.                                                |
| 20  | `20_safra_talhao_required.sql`                   | Reverte 15: `safra.talhao_id` volta a ser NOT NULL.                            |
| 21  | `21_perfil_ativo_excluido.sql`                   | Soft-delete em `perfil`.                                                       |
| 22  | `22_safra_soft_delete.sql`                       | Refino do soft-delete em `safra` (`excluido_por_id`, `justificativa_exclusao`).|
| 23  | `23_insumo_movimentacao_financeiro.sql`          | Ajustes em insumos e movimentações.                                            |
| 24  | `24_transacao_financeira_extras.sql`             | Expande `transacao_financeira` (descrição, status, forma pgto, soft-delete).   |
| 25  | `25_movimentacao_estoque_safra.sql`              | Vínculo opcional movimentação ↔ safra.                                         |

---

## 8. Segurança em uma frase

O `JwtAuthenticationFilter` valida o token enviado no header `Authorization: Bearer ...`,
extrai o `userId` e popula o `SecurityContextHolder`. Estão liberadas (entre outras):
`POST /auth/login`, `POST /auth/register`, `GET /health`, `/error`, Swagger/OpenAPI e
`OPTIONS /**`; o restante exige token válido.

---

## 9. Boas práticas adotadas no código

- DTOs como **`record`** (imutáveis, sem boilerplate).
- Validação com **Bean Validation** (`@NotNull`, `@Size`, `@DecimalMin`).
- **Soft-delete com justificativa** nos recursos sensíveis (Safra, Transação Financeira).
- Resumo financeiro calculado **em memória** sobre o `filtrar(...)` — evita problemas
  conhecidos do Hibernate com `SUM(CASE WHEN)` retornando `BigDecimal` zerado.
- `@Transactional(readOnly = true)` em consultas.
- Senha do usuário com **BCrypt**.

---

## 10. Licença

Projeto acadêmico. Uso livre para fins de estudo. Veja a documentação em
[`SafraCerta-docs`](https://github.com/guibbsss/SafraCerta-docs) para o pacote completo
de entrega do MVP (plano de testes, manual do usuário, apresentação e documentação técnica).
