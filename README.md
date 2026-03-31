# Restaurant Management API

API REST para gestão de restaurantes e usuários com Java 21 + Spring Boot 4.

> **Objetivo deste README:** servir como guia completo do que precisa ser implementado para este projeto ficar **pronto para produção**.

## 1) Estado atual do projeto (diagnóstico)

### O que já existe
- Estrutura base Spring Boot funcionando.
- Endpoints básicos:
  - `GET/POST /users`
  - `GET/POST/PUT/DELETE /api/restaurants`
- Tratamento global de exceções para:
  - `RestaurantNotFoundException` (404)
  - `IllegalArgumentException` (400)
- Segurança com Spring Security configurada (mas liberando tudo).
- Dependências de JPA, Security, Validation e OpenAPI no `pom.xml`.

### Lacunas identificadas
- Persistência está **em memória** (`InMemory*Repository`) apesar de existir configuração de PostgreSQL.
- Entidades (`Restaurant`, `User`) **não estão mapeadas com JPA** (`@Entity`, `@Id`, etc.).
- Segurança está aberta (`permitAll`) e sem autenticação/autorização real.
- Senha de usuário é salva sem hash.
- Validação é manual no service (não usa Bean Validation com anotações).
- Testes praticamente inexistentes (apenas `contextLoads`).
- Sem versionamento de banco (Flyway/Liquibase).
- Sem paginação, filtro, ordenação, auditoria, observabilidade, CI/CD e documentação operacional.

---

## 2) Definição de “projeto totalmente completo”

Para considerar este projeto completo, ele deve atender:
- **Funcionalidade:** CRUD completo de restaurantes e usuários, com regras de negócio claras.
- **Segurança:** autenticação robusta, autorização por perfil e proteção de dados sensíveis.
- **Persistência:** banco relacional com migrations versionadas.
- **Qualidade:** cobertura de testes (unitário, integração e controller) e pipeline CI.
- **Operação:** logs, métricas, health checks, documentação e deploy reproduzível.

---

## 3) Roadmap de implementação (prioridade)

## Fase 1 — Base sólida (MVP técnico)

### 1.1 Persistência real com Firebase Firestore
- [x] Configurar Firebase Admin SDK e conectar no Firestore.
- [x] Substituir os repositórios usados pela API por implementações Firestore.
- [x] Manter implementação em memória apenas no perfil `test`.
- [ ] Criar índices e regras no Firestore para `users.email` e `restaurants.cnpj`.

### 1.2 Versionamento de banco
- [ ] Adicionar Flyway.
- [ ] Criar migrations iniciais (`V1__create_users.sql`, `V2__create_restaurants.sql`, etc.).
- [ ] Parar de depender de `ddl-auto=update` em produção.

### 1.3 DTOs e separação de camadas
- [ ] Parar de expor entidades diretamente nos controllers.
- [ ] Criar DTOs de entrada/saída:
  - [ ] `CreateUserRequest`, `UserResponse`
  - [ ] `CreateRestaurantRequest`, `UpdateRestaurantRequest`, `RestaurantResponse`
- [ ] Criar mapeadores (manual ou MapStruct).

### 1.4 Validação com Bean Validation
- [ ] Aplicar `@NotBlank`, `@Email`, `@Size`, etc. nos DTOs.
- [ ] Validar CNPJ e telefone (anotação customizada ou regra de negócio no service).
- [ ] Melhorar `GlobalExceptionHandler` para retornar erros de validação por campo.

### 1.5 Endpoints mínimos faltantes
- [ ] `GET /api/restaurants/{id}`
- [ ] `PATCH /api/restaurants/{id}/activate`
- [ ] `PATCH /api/restaurants/{id}/deactivate`
- [ ] `GET /users/{id}`
- [ ] `PUT /users/{id}`
- [ ] `DELETE /users/{id}`

---

## Fase 2 — Segurança e domínio

### 2.1 Autenticação e autorização
- [ ] Implementar autenticação (JWT recomendado).
- [ ] Criar endpoint de login (`POST /auth/login`).
- [ ] Definir perfis (`ADMIN`, `MANAGER`, `CUSTOMER`).
- [ ] Restringir endpoints por perfil (`@PreAuthorize` ou config por rota).
- [ ] Manter Swagger e health check acessíveis conforme necessidade.

### 2.2 Segurança de dados
- [ ] Salvar senha com hash (`BCryptPasswordEncoder`).
- [ ] Nunca retornar senha em respostas.
- [ ] Adicionar política mínima de senha.
- [ ] Externalizar segredos (variáveis de ambiente).

### 2.3 Regras de negócio de domínio
- [ ] Impedir duplicidade de CNPJ com erro de domínio claro.
- [ ] Definir regras para atualização parcial.
- [ ] Definir comportamento de exclusão (hard delete vs soft delete).
- [ ] Se optar por soft delete:
  - [ ] adicionar `deletedAt` e filtros de consulta.

---

## Fase 3 — Qualidade, observabilidade e docs

### 3.1 Testes
- [ ] Unitários de services (cenários felizes e de erro).
- [ ] Testes de controller com `MockMvc`.
- [ ] Testes de integração com banco (Testcontainers PostgreSQL).
- [ ] Meta mínima de cobertura inicial: **70%** (ajustável).

### 3.2 Documentação da API
- [ ] Completar OpenAPI com:
  - [ ] descrições de endpoints
  - [ ] exemplos de request/response
  - [ ] códigos de erro padronizados
- [ ] Atualizar `requests.http` com fluxo completo (auth + CRUD completo).

### 3.3 Observabilidade e operação
- [ ] Adicionar Spring Boot Actuator.
- [ ] Expor `/actuator/health`, `/actuator/info`, métricas básicas.
- [ ] Padronizar logs (JSON opcional) com correlação de requisição.
- [ ] Tratar exceções inesperadas com resposta padrão (`500`).

---

## Fase 4 — Produção e escalabilidade

### 4.1 Ambientes e configuração
- [ ] Criar perfis `dev`, `test`, `prod`.
- [ ] Separar `application-dev.properties`, `application-test.properties`, `application-prod.properties`.
- [ ] Remover credenciais fixas do repositório.

### 4.2 CI/CD
- [ ] Pipeline (GitHub Actions):
  - [ ] build
  - [ ] test
  - [ ] análise estática (ex: SpotBugs/Checkstyle/Sonar)
- [ ] Bloquear merge sem pipeline verde.

### 4.3 Containerização e deploy
- [ ] Criar `Dockerfile`.
- [ ] Criar `docker-compose.yml` para API + PostgreSQL.
- [ ] Definir estratégia de deploy (Render, Railway, Fly.io, AWS, Azure, etc.).

---

## 4) Backlog sugerido por épicos

## Épico A — Persistência e modelo de dados
- [ ] JPA entities
- [ ] Repositórios Spring Data
- [ ] Flyway
- [ ] Constraints e índices

## Épico B — API e contratos
- [ ] DTOs + mapeadores
- [ ] CRUD completo User/Restaurant
- [ ] Paginação (`page`, `size`, `sort`)
- [ ] Filtros (nome, ativo, CNPJ)

## Épico C — Segurança
- [ ] JWT
- [ ] Roles
- [ ] BCrypt
- [ ] Hardening de endpoints

## Épico D — Qualidade
- [ ] Testes unitários/integrados
- [ ] Cobertura mínima
- [ ] Padronização de erros

## Épico E — Operação
- [ ] Actuator
- [ ] Logs estruturados
- [ ] Docker + CI/CD
- [ ] Runbook de deploy

---

## 5) Critérios de pronto (Definition of Done)

Uma feature só é considerada pronta quando:
- [ ] Código implementado com testes passando.
- [ ] Cobertura de testes da feature adequada.
- [ ] Validação e tratamento de erro implementados.
- [ ] Endpoint documentado no OpenAPI.
- [ ] Sem credenciais hardcoded.
- [ ] Revisão de código concluída.

Para considerar o projeto pronto para produção:
- [ ] Todos os itens da Fase 1 e Fase 2 concluídos.
- [ ] Pelo menos 80% da Fase 3 concluída.
- [ ] Pipeline CI/CD ativo.
- [ ] Deploy em ambiente real validado.

---

## 6) Comandos úteis

```bash
# rodar aplicação
./mvnw spring-boot:run

# rodar testes
./mvnw test

# gerar build
./mvnw clean package
```

---

## 7) Próximos passos imediatos (ordem recomendada)

1. Consolidar Firestore + índices + DTOs.
2. Fechar CRUD completo (incluindo buscar por ID e ativar/desativar).
3. Implementar autorização JWT por perfil.
4. Cobrir com testes de service e controller.
5. Configurar Actuator + Docker + pipeline CI.

## 8) Firebase / Firestore

1. No Firebase Console, gere uma service account em `Project Settings > Service accounts`.
2. Salve o JSON fora do repositório.
3. Copie `.env.example` para `.env` e preencha `FIREBASE_SERVICE_ACCOUNT_PATH`.
4. Rode a API com o perfil desejado:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Observações:
- o trecho `firebaseConfig` da SDK web nao autentica este backend Spring Boot;
- para backend, a API usa `firebase-admin` com service account;
- o perfil `test` nao acessa Firebase e usa repositórios em memoria.

---

## 8) Observação importante

Atualmente, o projeto mistura estruturas de arquitetura (pastas `controller/service/repository` e também `application/domain/infrastructure`).

Para evitar dívida técnica, escolha **um padrão principal** e aplique de forma consistente:
- opção 1: arquitetura em camadas tradicional;
- opção 2: arquitetura hexagonal/clean (ports/adapters).

Padronizar isso cedo reduz retrabalho nas fases de segurança, testes e deploy.
