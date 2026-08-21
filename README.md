# PIX Transfer System

Backend Java para simulação de transferências PIX, construído com foco em consistência financeira, idempotência, concorrência, auditoria e arquitetura evolutiva.

O projeto reproduz desafios comuns em bancos e fintechs: impedir movimentações duplicadas, proteger saldo contra atualizações concorrentes, registrar lançamentos imutáveis e garantir persistência atômica de eventos.

> Projeto educacional e de portfólio. Não possui integração real com BACEN, DICT ou SPI.

## Tecnologias

- Java 21
- Spring Boot 3.3.2
- Spring Web e Bean Validation
- Spring Data JPA e Hibernate
- Resilience4j 2.2
- PostgreSQL 16
- Flyway
- OpenAPI 3 e Swagger UI
- JUnit 5, Mockito e ArchUnit
- JaCoCo
- Docker e Docker Compose
- GitHub Actions

## Funcionalidades implementadas

- criação de transferências entre contas identificadas por chaves PIX;
- validação de saldo, limite diário e estado da conta;
- `Money` como value object baseado em `BigDecimal`;
- idempotência persistente pelo header `Idempotency-Key`;
- fingerprint SHA-256 para impedir reutilização da chave com outro payload;
- optimistic locking com `@Version`;
- retry de conflitos otimistas em transações independentes;
- Circuit Breaker, Retry exponencial, TimeLimiter e ThreadPool Bulkhead;
- ledger imutável com lançamentos de débito e crédito;
- Transactional Outbox persistida junto da transferência;
- auditoria JPA com `created_by` e `last_modified_by`;
- migrations versionadas com Flyway;
- respostas de erro no padrão RFC 7807;
- documentação OpenAPI;
- testes unitários, arquiteturais, integração e concorrência real.

## Fluxo da transferência

```text
POST /api/v1/pix-transfers
            |
            v
Validação do Idempotency-Key e fingerprint
            |
            v
Reserva idempotente em transação independente
            |
            v
Validação das chaves PIX e contas
            |
            v
Débito e crédito com optimistic locking
            |
            v
Transferência + ledger + idempotência + outbox
            |
            v
Commit atômico no PostgreSQL
```

Saldo, transferência, dois lançamentos do ledger, resultado idempotente e evento da outbox compartilham a mesma transação. Falha em qualquer gravação causa rollback completo.

## Arquitetura

O projeto segue arquitetura hexagonal com fluxo de dependências direcionado ao domínio:

```text
HTTP Adapter
    |
    v
Application Services
    |
    v
Domain Models + Ports
    ^
    |
JPA Persistence Adapters
```

```text
src/main/java/com/pdonha/pix
├── domain
│   ├── model
│   ├── event
│   ├── port
│   └── exception
├── application
│   ├── dto
│   └── service
├── adapter
│   ├── in/http
│   └── out/persistence
└── PixTransferSystemApplication
```

Regras ArchUnit impedem dependências do domínio para Spring/JPA e dependências da aplicação para adapters.

## Consistência financeira

### Idempotência

Cada requisição reserva uma chave única no PostgreSQL. Repetição do mesmo payload retorna a transferência já registrada, sem movimentar saldo novamente. Mesma chave com payload diferente retorna `409 Conflict`.

### Concorrência

Contas e transferências possuem versão otimista. Atualizações baseadas em versão obsoleta falham e são repetidas em nova transação, evitando lost updates.

### Ledger

Cada transferência gera dois lançamentos imutáveis:

- `DEBIT` na conta pagadora;
- `CREDIT` na conta recebedora.

Cada lançamento registra valor e saldo posterior. Constraints impedem duplicação por transferência, conta e tipo.

### Transactional Outbox

Eventos são persistidos na tabela `event_store` durante a mesma transação da transferência. A estrutura contém status, tentativas e agendamento para futura publicação assíncrona.

O projeto não afirma usar Event Sourcing: estado dos agregados não é reconstruído por replay.

### Resiliência de integrações

Resilience4j protege a fronteira de autorização de transferências:

- Circuit Breaker interrompe chamadas durante falhas recorrentes;
- Retry repete somente falhas transitórias, com backoff exponencial;
- TimeLimiter limita o tempo da autorização;
- ThreadPool Bulkhead isola threads e limita fila/concorrência.

Essas políticas não envolvem a transação financeira. Cancelar uma chamada JDBC
não prova que o commit foi interrompido e poderia incentivar repetição de uma
transferência já efetivada. A autorização é somente leitura, idempotente e ocorre
antes da gravação de saldos, ledger e outbox.

O adapter local aprova autorizações para permitir execução independente. A
fronteira está pronta para substituição por cliente HTTP de antifraude ou
autorizador externo.

Falhas transitórias deixam a chave idempotente como `RETRYABLE`, permitindo nova
tentativa segura com o mesmo transfer ID. Recusas definitivas ficam registradas
como `FAILED`.

## API

### Criar transferência

```http
POST /api/v1/pix-transfers
Content-Type: application/json
Idempotency-Key: 816f0f4e-4ef7-4f3d-a9d6-8f617dcceb32
```

```json
{
  "originPixKey": "12345678900",
  "destinationPixKey": "cliente@exemplo.com",
  "amount": 150.00
}
```

Resposta:

```json
{
  "transfer_id": "968d5db4-f37a-42f4-9758-f405a19f9d19",
  "status": "PENDING",
  "amount": 150.00,
  "created_at": "2026-08-21T10:30:00"
}
```

### Erros

Erros seguem RFC 7807:

```json
{
  "type": "/problems/insufficient-balance",
  "title": "Transfer cannot be processed",
  "status": 422,
  "detail": "Insufficient balance for withdrawal",
  "error_code": "INSUFFICIENT_BALANCE"
}
```

### Documentação

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- Health check: `http://localhost:8080/actuator/health`
- Métricas: `http://localhost:8080/actuator/metrics`
- Circuit Breakers: `http://localhost:8080/actuator/circuitbreakers`

## Executando com Docker

Pré-requisito: Docker com Compose.

```bash
git clone https://github.com/pdonha-dev/pix-transfer-system.git
cd pix-transfer-system
docker compose up -d --build
```

Serviços iniciados:

- aplicação: `localhost:8080`;
- PostgreSQL: `localhost:5432`.

Verificar estado:

```bash
docker compose ps
docker compose logs app
```

Encerrar:

```bash
docker compose down
```

Volumes do PostgreSQL são preservados. Use `docker compose down -v` somente quando quiser remover os dados locais.

## Executando testes

Testes unitários:

```bash
mvn clean test
```

Pipeline completo:

```bash
mvn clean verify
```

O pipeline atual executa:

- 98 testes unitários e arquiteturais;
- 11 testes de integração;
- 109 testes no total;
- cobertura JaCoCo de 76,51% das linhas;
- validação das dependências arquiteturais;
- testes de concorrência com PostgreSQL real.

Relatório de cobertura:

```text
target/site/jacoco/index.html
```

## Banco de dados

Flyway controla todo o schema. Hibernate usa `ddl-auto: validate`, sem alterar tabelas automaticamente.

As migrations atuais cobrem:

- contas, chaves PIX e transferências;
- idempotência;
- optimistic locking;
- auditoria;
- event store/outbox;
- audit logs;
- fingerprint de requisições;
- ledger financeiro imutável.

## Decisões técnicas

- **PostgreSQL como fonte de verdade:** integridade e transações prevalecem sobre cache.
- **Sem Redis sem necessidade concreta:** cache exige TTL, invalidação, fallback e testes próprios.
- **Monólito modular primeiro:** microsserviços somente quando houver motivação mensurável.
- **Outbox em vez de publicação direta:** evita transferência persistida sem evento correspondente.
- **Ledger append-only:** histórico financeiro não é atualizado nem removido.
- **Adapters isolados:** domínio permanece independente de frameworks.

## Próximas evoluções

- OAuth2 Resource Server com JWT;
- logs estruturados e correlation ID;
- métricas Prometheus com Micrometer;
- tracing com OpenTelemetry;
- publicação assíncrona da outbox e DLQ;
- Kubernetes e automação de entrega.

## Limitações

- não processa transferências no sistema PIX real;
- não consulta DICT ou SPI;
- não implementa autenticação nesta etapa;
- não representa conformidade regulatória ou certificação bancária;
- não deve armazenar dados financeiros reais.
