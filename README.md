# FinPay + FinBot 💳🤖

> Plataforma de pagamentos com microsserviços e assistente financeiro com Inteligência Artificial — construída com Java 21, Spring Boot 4 e Azure Cloud.

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.4-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Azure](https://img.shields.io/badge/Azure-Cloud-blue?style=flat-square&logo=microsoftazure)](https://azure.microsoft.com/)
[![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)](LICENSE)

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [O que o sistema faz](#-o-que-o-sistema-faz)
- [Arquitetura](#-arquitetura)
- [Microsserviços](#-microsserviços)
- [Tecnologias](#-tecnologias)
- [Serviços Azure](#-serviços-azure)
- [Pré-requisitos](#-pré-requisitos)
- [Como rodar localmente](#-como-rodar-localmente)
- [Variáveis de ambiente](#-variáveis-de-ambiente)
- [Endpoints](#-endpoints)
- [Roadmap](#-roadmap)
- [Autor](#-autor)

---

## 📖 Sobre o Projeto

O **FinPay** é uma plataforma de pagamentos simulada construída com arquitetura de microsserviços, comunicação assíncrona via mensageria e inteligência artificial conversacional. O projeto foi desenvolvido como estudo prático para a certificação **Microsoft AI-200 (Azure AI Cloud Developer Associate)** e demonstra padrões de arquitetura utilizados em fintechs e bancos digitais do mercado brasileiro.

O **FinBot** é o assistente financeiro com IA integrado à plataforma — um chatbot que consulta dados reais de transações do usuário usando **Azure OpenAI GPT-4o** com **function calling** e **RAG (Retrieval-Augmented Generation)**.

### Destaques técnicos

- Arquitetura orientada a eventos com **Azure Service Bus** e **Event Grid**
- Garantia de idempotência em pagamentos via **Azure Cache for Redis**
- Reprocessamento automático de falhas com **dead-letter queue**
- Chatbot com **function calling** — o GPT decide autonomamente quando consultar o banco
- Pipeline **RAG** com embeddings vetoriais e busca semântica no **Azure AI Search**
- Segurança production-grade com **Key Vault** e **Managed Identity** — zero credenciais em código
- Observabilidade completa com rastreamento distribuído via **Application Insights**

---

## 💡 O que o sistema faz

Um usuário autenticado pode:

- Criar uma conta digital e consultar seu saldo
- Realizar transferências entre contas com validação em tempo real
- Consultar histórico de transações com filtros por período e categoria
- Receber notificações de confirmação ou falha de cada operação
- Conversar com o **FinBot**: *"Quanto gastei em restaurantes esse mês?"*
- Obter análises automáticas: comparativos mensais, categorias com maior gasto
- Receber dicas financeiras personalizadas com base no perfil de gastos

---

## 🏗️ Arquitetura

```
                          ┌─────────────────┐
                          │  Azure API Mgmt  │
                          │    (Gateway)     │
                          └────────┬────────┘
                                   │ JWT Auth
              ┌────────────────────┼────────────────────┐
              │                    │                    │
    ┌─────────▼──────┐  ┌──────────▼─────┐  ┌──────────▼─────┐
    │ Account Service│  │Payment Service │  │  Chat Service  │
    │   (porta 8081) │  │  (porta 8082)  │  │  (porta 8085)  │
    └─────────┬──────┘  └──────────┬─────┘  └──────────┬─────┘
              │                    │                    │
              │           ┌────────▼────────┐           │
              │           │  Azure Service  │           │
              │           │      Bus        │           │
              │           └────────┬────────┘           │
              │                    │                    │
              │         ┌──────────┼──────────┐         │
              │         │                     │         │
    ┌─────────▼──────┐  ▼               ┌─────▼──────┐  │
    │  Cosmos DB     │ Notification      │Audit Service│  │
    │  (NoSQL)       │ Service           │ (porta 8084)│  │
    └────────────────┘ (porta 8083)      └────────────┘  │
                                                          │
                              ┌───────────────────────────┘
                              │
                    ┌─────────▼──────────┐
                    │   Azure OpenAI     │
                    │  (GPT-4o + RAG)    │
                    └────────────────────┘
```

---

## 🔧 Microsserviços

| Serviço | Porta | Responsabilidade | Status |
|---|---|---|---|
| **account-service** | 8081 | Usuários, contas e histórico de transações | 🚧 Em desenvolvimento |
| **payment-service** | 8082 | Processamento de transferências e idempotência | 📋 Planejado |
| **notification-service** | 8083 | Notificações de confirmação e falha | 📋 Planejado |
| **audit-service** | 8084 | Registro imutável de operações para compliance | 📋 Planejado |
| **chat-service** | 8085 | FinBot — chatbot financeiro com Azure OpenAI | 📋 Planejado |

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 (LTS) | Linguagem principal |
| Spring Boot | 4.0.4 | Framework base |
| Spring Web | Incluso | APIs REST |
| Spring Security | Incluso | Autenticação e autorização |
| Spring Data Cosmos | Incluso | Acesso ao Cosmos DB |
| Spring AI | 1.0.0 | Integração com Azure OpenAI e RAG |
| Spring WebFlux | Incluso | Streaming de respostas do chatbot |
| Lombok | Incluso | Redução de boilerplate |
| Maven | 3.9+ | Build e dependências |

---

## ☁️ Serviços Azure

| Serviço | Uso no Projeto |
|---|---|
| **Azure App Service** | Hospedagem dos microsserviços |
| **Azure Functions** | Processamento assíncrono de jobs |
| **Azure Cosmos DB** | Banco NoSQL — contas, transações e histórico de chat |
| **Azure Service Bus** | Mensageria entre microsserviços |
| **Azure Event Grid** | Publicação de eventos de notificação |
| **Azure Cache for Redis** | Idempotência de pagamentos e sessão do chatbot |
| **Azure API Management** | Gateway com autenticação JWT e rate limiting |
| **Azure Key Vault** | Gerenciamento de secrets e certificados |
| **Azure OpenAI** | GPT-4o para o FinBot |
| **Azure AI Search** | Índice vetorial para pipeline RAG |
| **Application Insights** | Observabilidade e rastreamento distribuído |

---

## ✅ Pré-requisitos

Antes de rodar o projeto, certifique-se de ter instalado:

- [Java 21](https://www.oracle.com/java/technologies/downloads/)
- [Maven 3.9+](https://maven.apache.org/download.cgi)
- [Azure CLI](https://docs.microsoft.com/cli/azure/install-azure-cli)
- [Docker](https://www.docker.com/products/docker-desktop/) (opcional — para Redis local)
- Conta Azure com crédito ativo

---

## 🚀 Como rodar localmente

### 1. Clone o repositório

```bash
git clone https://github.com/fabianoqss/FinPay.git
cd FinPay
```

### 2. Autentique no Azure

```bash
az login
```

### 3. Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do serviço (veja a seção [Variáveis de ambiente](#-variáveis-de-ambiente)).

### 4. Rode o Account Service

```bash
cd account-service
mvn spring-boot:run
```

### 5. Verifique o health check

```bash
curl http://localhost:8081/actuator/health
```

Resposta esperada:
```json
{
  "status": "UP"
}
```

---

## 🔐 Variáveis de ambiente

Crie um arquivo `application.yml` local ou configure as seguintes variáveis:

```yaml
spring:
  cloud:
    azure:
      cosmos:
        endpoint: ${COSMOS_ENDPOINT}
        key: ${COSMOS_KEY}
        database: finpay
      keyvault:
        secret:
          endpoint: ${KEY_VAULT_ENDPOINT}
      servicebus:
        connection-string: ${SERVICE_BUS_CONNECTION_STRING}

  data:
    redis:
      host: ${REDIS_HOST}
      port: 6380
      ssl:
        enabled: true
      password: ${REDIS_PASSWORD}
```

> ⚠️ **Nunca** commite credenciais reais no repositório. Em produção, todas as secrets são gerenciadas pelo **Azure Key Vault** com **Managed Identity**.

---

## 📡 Endpoints

### Account Service — `http://localhost:8081`

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/users` | Cadastra um novo usuário |
| `GET` | `/users/{id}` | Busca dados do usuário |
| `POST` | `/users/{id}/accounts` | Cria conta financeira para o usuário |
| `GET` | `/users/{id}/accounts/{accountId}` | Dados da conta |
| `GET` | `/users/{id}/accounts/{accountId}/balance` | Consulta saldo |
| `GET` | `/users/{id}/accounts/{accountId}/transactions` | Histórico de transações |

### Chat Service — `http://localhost:8085` *(em breve)*

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/chat` | Envia mensagem para o FinBot (streaming SSE) |
| `GET` | `/chat/history` | Histórico de conversas |

---

## 🗺️ Roadmap

- [x] Estrutura base do projeto
- [x] Account Service — entidades e configuração
- [ ] Account Service — endpoints completos
- [ ] Payment Service — transferências com idempotência
- [ ] Notification Service — eventos e notificações
- [ ] Audit Service — registro imutável
- [ ] Chat Service — FinBot com function calling
- [ ] Pipeline RAG com Azure AI Search
- [ ] CI/CD com GitHub Actions
- [ ] Deploy completo no Azure App Service

---

## 👤 Autor

**Fabiano Quirino da Silva**

[![GitHub](https://img.shields.io/badge/GitHub-fabianoqss-black?style=flat-square&logo=github)](https://github.com/fabianoqss/FinPay)

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

> Projeto desenvolvido como estudo prático para a certificação **Microsoft AI-200 — Azure AI Cloud Developer Associate** 🚀
