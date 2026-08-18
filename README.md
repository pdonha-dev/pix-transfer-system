# PIX Transfer System

Plataforma backend para simular transferências PIX com práticas usadas em sistemas financeiros: consistência, idempotência, segurança, auditoria, mensageria e observabilidade.

> Projeto educacional. Não possui integração real com BACEN, DICT ou SPI.

## Objetivo

Construir uma aplicação que evolua de um domínio Java bem modelado para uma API distribuída próxima dos desafios encontrados em bancos e fintechs.

Ao final, o projeto deverá demonstrar:

- Java 21 e Spring Boot 4;
- arquitetura hexagonal;
- API REST documentada com OpenAPI;
- PostgreSQL para contas, saldos e lançamentos;
- Redis para cache e idempotência;
- mensageria para processamento assíncrono;
- autenticação OAuth2/JWT;
- testes unitários, de integração e de concorrência;
- logs, métricas e tracing;
- Docker, Kubernetes e CI/CD.

## Fluxo principal

```text
Cliente solicita transferência
        |
        v
API autentica e valida a requisição
        |
        v
Chave de idempotência já foi utilizada?
   | não                  | sim
   v                      v
Valida contas,       Retorna o resultado
chave, saldo e       previamente registrado
limites
        |
        v
Registra débito e crédito atomicamente
        |
        v
Publica evento pela Outbox
        |
        v
Notificação, auditoria e antifraude
```

## Arquitetura-alvo

```text
src/main/java/com/phenriq/pix
├── domain
│   ├── model
│   ├── service
│   └── exception
├── application
│   ├── port/in
│   ├── port/out
│   └── usecase
├── adapter
│   ├── in/web
│   └── out
│       ├── persistence
│       ├── cache
│       └── messaging
└── infrastructure
    └── configuration
```

O domínio não dependerá de Spring, banco de dados ou mensageria. Essas tecnologias serão conectadas por portas e adaptadores.

## Domínio inicial

| Componente | Responsabilidade |
|---|---|
| `Customer` | Representar o titular da conta |
| `Account` | Proteger saldo, estado e limites |
| `PixKey` | Associar uma chave válida a uma conta |
| `PixTransfer` | Representar o ciclo de vida da transferência |
| `LedgerEntry` | Registrar débito ou crédito de forma auditável |

Valores monetários usarão `BigDecimal`. Identificadores usarão UUID. Datas usarão `Instant` em UTC.

## Regras essenciais

- uma transferência deve possuir uma chave de idempotência;
- débito e crédito devem ocorrer na mesma transação;
- nenhuma conta pode ficar com saldo negativo;
- contas bloqueadas não podem movimentar valores;
- uma chave PIX ativa pertence a uma única conta;
- limites podem variar por cliente e período;
- o histórico financeiro é imutável;
- dados sensíveis não podem aparecer em logs;
- falhas assíncronas devem aceitar retry e DLQ.

## Trilha de conquistas

Cada conquista deve caber em uma sessão de 45 a 90 minutos e terminar com código funcionando. O projeto seguirá duas metas: publicar um MVP rapidamente e depois evoluí-lo sem atalhos técnicos.

### Nível 1 — Java bancário

**Recompensa:** domínio executável e testado, sem framework.

- [ ] **1. Projeto nasce:** Maven, Java 21, pacotes e primeiro teste
- [ ] **2. Dinheiro seguro:** criar `Money` ou padronizar `BigDecimal`
- [ ] **3. Cliente existe:** modelar `Customer` com invariantes
- [ ] **4. Conta protege saldo:** depósito, débito, bloqueio e limites
- [ ] **5. Chave encontra conta:** modelar tipos e ciclo de vida da `PixKey`
- [ ] **6. Primeiro PIX:** transferir entre duas contas em memória
- [ ] **7. Falhas explícitas:** criar exceções de domínio
- [ ] **8. Domínio confiável:** cobrir regras críticas com JUnit 5

### Nível 2 — MVP para portfólio

**Recompensa:** primeira versão publicável e utilizável por HTTP.

- [ ] **9. Spring entra:** criar aplicação sem contaminar o domínio
- [ ] **10. Casos de uso:** implementar portas de entrada e saída
- [ ] **11. API REST:** criar clientes, contas, chaves e transferências
- [ ] **12. Contrato profissional:** validação, Problem Details e OpenAPI
- [ ] **13. Dados persistentes:** PostgreSQL e migrations com Flyway
- [ ] **14. Ambiente reproduzível:** Docker Compose e instruções de execução
- [ ] **15. Qualidade automática:** testes no GitHub Actions

Ao concluir este nível, o projeto já poderá entrar no currículo e receber melhorias contínuas.

### Nível 3 — Confiabilidade financeira

**Recompensa:** solução capaz de explicar desafios reais de sistemas bancários.

- [ ] **16. PIX sem duplicidade:** `Idempotency-Key` com Redis
- [ ] **17. Concorrência segura:** locking e testes de débitos simultâneos
- [ ] **18. Ledger auditável:** lançamentos imutáveis de débito e crédito
- [ ] **19. Eventos confiáveis:** Transactional Outbox e mensageria
- [ ] **20. Falhas controladas:** retry, backoff, DLQ e consumidor idempotente

### Nível 4 — Pronto para entrevista

**Recompensa:** portfólio alinhado a bancos e fintechs de grande porte.

- [ ] **21. API protegida:** OAuth2 Resource Server, JWT e autorização
- [ ] **22. Operação visível:** logs estruturados e correlation ID
- [ ] **23. Saúde mensurável:** Actuator, Micrometer e Prometheus
- [ ] **24. Fluxo rastreável:** OpenTelemetry e tracing distribuído
- [ ] **25. Entrega moderna:** Docker, Kubernetes e pipeline completo
- [ ] **26. Evidência técnica:** diagrama, decisões arquiteturais e demonstração

## Como vamos aprender e entregar rápido

Para cada conquista:

1. eu explico o conceito e comparo com C# quando isso ajudar;
2. você propõe a modelagem ou algoritmo;
3. você implementa o primeiro trecho;
4. eu reviso funcionalidade, Java idiomático, Clean Code e SOLID;
5. corrigimos juntos e registramos a conquista;
6. eu só forneço a solução completa se você ficar bloqueado.

Não criaremos abstrações antecipadamente. Primeiro faremos uma fatia vertical funcionar; depois adicionaremos robustez com uma necessidade concreta.

### Ritmo sustentável

- uma conquista por sessão;
- pausa após 60 minutos;
- no máximo duas conquistas no mesmo dia;
- commit pequeno ao concluir cada conquista;
- um dia semanal sem projeto;
- se uma etapa ficar grande, ela será dividida antes de continuar.

### Paralelo C# → Java

Durante as revisões, daremos atenção especial a:

- propriedades do C# versus encapsulamento e métodos em Java;
- `decimal` versus `BigDecimal`;
- LINQ versus Streams;
- records do C# versus records do Java;
- ASP.NET Core DI versus Spring DI;
- Entity Framework versus Spring Data JPA/Hibernate;
- `async/await` versus concorrência e programação assíncrona em Java.

## API planejada

```http
POST   /api/v1/customers
POST   /api/v1/accounts
POST   /api/v1/pix-keys
DELETE /api/v1/pix-keys/{id}
POST   /api/v1/pix-transfers
GET    /api/v1/pix-transfers/{id}
GET    /api/v1/accounts/{id}/statement
```

Exemplo:

```http
POST /api/v1/pix-transfers
Idempotency-Key: 816f0f4e-4ef7-4f3d-a9d6-8f617dcceb32
Authorization: Bearer <token>
```

```json
{
  "sourceAccountId": "5f84c720-8d64-42ab-b9dd-a0569e522fd1",
  "targetPixKey": "cliente@exemplo.com",
  "amount": 150.00,
  "description": "Pagamento"
}
```

## Estratégia de testes

| Tipo | O que comprova |
|---|---|
| Unitário | Regras do domínio e casos de uso |
| Integração | PostgreSQL, Redis e mensageria |
| Contrato | Compatibilidade da API |
| Concorrência | Proteção contra débito duplicado |
| Carga | Latência e capacidade |

Cobertura será usada como indicador, não como objetivo isolado.

## Decisões importantes

- **PostgreSQL no core financeiro:** transações e integridade são prioritárias.
- **Redis fora da fonte de verdade:** cache não determina o saldo oficial.
- **Outbox em vez de publicação direta:** evita salvar a transferência e perder o evento.
- **Arquitetura hexagonal:** mantém domínio testável e independente do framework.
- **Monólito modular primeiro:** microsserviços serão considerados apenas quando houver uma razão mensurável.

## Fora do escopo

- conexão real com a infraestrutura do PIX;
- armazenamento de credenciais bancárias reais;
- alegação de conformidade regulatória;
- simulação completa do ecossistema BACEN.

## Critério final de sucesso

O projeto estará pronto para portfólio quando:

- regras críticas estiverem testadas;
- concorrência e idempotência forem demonstradas;
- API e decisões arquiteturais estiverem documentadas;
- ambiente iniciar com um único comando;
- pipeline validar build, testes e segurança;
- dashboards permitirem investigar uma transferência;
- nenhuma etapa do README afirmar integração bancária inexistente.

## Status

**Fase atual:** planejamento e modelagem do domínio.

O primeiro incremento será uma transferência entre duas contas em memória, implementada em Java puro e coberta por testes.
