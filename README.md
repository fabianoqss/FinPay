# FinPay + FinBot 💳🤖

> A payments platform built with microservices and an AI-powered financial assistant — developed with Java 21, Spring Boot 4 and Azure Cloud.

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.4-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Azure](https://img.shields.io/badge/Azure-Cloud-blue?style=flat-square&logo=microsoftazure)](https://azure.microsoft.com/)
[![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)](LICENSE)

---

## 📋 Table of Contents

- [About the Project](#-about-the-project)
- [What the System Does](#-what-the-system-does)
- [Architecture](#-architecture)
- [Microservices](#-microservices)
- [Technologies](#-technologies)
- [Azure Services](#-azure-services)
- [Prerequisites](#-prerequisites)
- [Running Locally](#-running-locally)
- [Environment Variables](#-environment-variables)
- [Endpoints](#-endpoints)
- [Roadmap](#-roadmap)
- [Author](#-author)

---

## 📖 About the Project

**FinPay** is a simulated payments platform built with a microservices architecture, asynchronous messaging and conversational artificial intelligence. The project was developed as a hands-on study for the **Microsoft AI-200 (Azure AI Cloud Developer Associate)** certification and demonstrates architecture patterns used by fintechs and digital banks in the Brazilian market.

**FinBot** is the AI-powered financial assistant integrated into the platform — a chatbot that queries real user transaction data using **Azure OpenAI GPT-4o** with **function calling** and **RAG (Retrieval-Augmented Generation)**.

### Technical Highlights

- Event-driven architecture with **Azure Service Bus** and **Event Grid**
- Payment idempotency guaranteed via **Azure Cache for Redis**
- Automatic failure reprocessing with **dead-letter queue**
- Chatbot with **function calling** — GPT autonomously decides when to query the database
- **RAG pipeline** with vector embeddings and semantic search on **Azure AI Search**
- Production-grade security with **Key Vault** and **Managed Identity** — zero credentials in code
- Full observability with distributed tracing via **Application Insights**

---

## 💡 What the System Does

An authenticated user can:

- Create a digital account and check their balance
- Make transfers between accounts with real-time validation
- View transaction history filtered by period and category
- Receive confirmation or failure notifications for each operation
- Chat with **FinBot**: *"How much did I spend on restaurants this month?"*
- Get automatic analysis: monthly comparisons, top spending categories, trends
- Receive personalized financial tips based on spending profile

---

## 🏗️ Architecture

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
    │  (port 8081)   │  │  (port 8082)   │  │  (port 8085)   │
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
    │  (NoSQL)       │ Service           │ (port 8084) │  │
    └────────────────┘ (port 8083)       └────────────┘  │
                                                          │
                              ┌───────────────────────────┘
                              │
                    ┌─────────▼──────────┐
                    │   Azure OpenAI     │
                    │  (GPT-4o + RAG)    │
                    └────────────────────┘
```

---

## 🔧 Microservices

| Service | Port | Responsibility | Status |
|---|---|---|---|
| **account-service** | 8081 | Users, accounts and transaction history | 🚧 In development |
| **payment-service** | 8082 | Transfer processing and idempotency | 📋 Planned |
| **notification-service** | 8083 | Confirmation and failure notifications | 📋 Planned |
| **audit-service** | 8084 | Immutable operation records for compliance | 📋 Planned |
| **chat-service** | 8085 | FinBot — financial chatbot with Azure OpenAI | 📋 Planned |

---

## 🛠️ Technologies

| Technology | Version | Usage |
|---|---|---|
| Java | 21 (LTS) | Main language |
| Spring Boot | 4.0.4 | Base framework |
| Spring Web | Included | REST APIs |
| Spring Security | Included | Authentication and authorization |
| Spring Data Cosmos | Included | Cosmos DB access |
| Spring AI | 1.0.0 | Azure OpenAI and RAG integration |
| Spring WebFlux | Included | Chatbot response streaming |
| Lombok | Included | Boilerplate reduction |
| Maven | 3.9+ | Build and dependency management |

---

## ☁️ Azure Services

| Service | Usage in the Project |
|---|---|
| **Azure App Service** | Microservices hosting |
| **Azure Functions** | Asynchronous job processing |
| **Azure Cosmos DB** | NoSQL database — accounts, transactions and chat history |
| **Azure Service Bus** | Messaging between microservices |
| **Azure Event Grid** | Notification event publishing |
| **Azure Cache for Redis** | Payment idempotency and chatbot session |
| **Azure API Management** | Gateway with JWT authentication and rate limiting |
| **Azure Key Vault** | Secrets and certificate management |
| **Azure OpenAI** | GPT-4o for FinBot |
| **Azure AI Search** | Vector index for RAG pipeline |
| **Application Insights** | Observability and distributed tracing |

---

## ✅ Prerequisites

Before running the project, make sure you have installed:

- [Java 21](https://www.oracle.com/java/technologies/downloads/)
- [Maven 3.9+](https://maven.apache.org/download.cgi)
- [Azure CLI](https://docs.microsoft.com/cli/azure/install-azure-cli)
- [Docker](https://www.docker.com/products/docker-desktop/) — for the Cosmos DB emulator
- Active Azure account with credits

---

## 🚀 Running Locally

### 1. Clone the repository

```bash
git clone https://github.com/fabianoqss/FinPay.git
cd FinPay
```

### 2. Start the Cosmos DB emulator

```bash
docker-compose up -d
```

### 3. Authenticate with Azure

```bash
az login
```

### 4. Configure environment variables

Create an `application.yml` file in the service root (see [Environment Variables](#-environment-variables)).

### 5. Run the Account Service

```bash
cd account-service
mvn spring-boot:run
```

### 6. Check the health endpoint

```bash
curl http://localhost:8081/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

---

## 🔐 Environment Variables

Configure the following variables in your `application.yml`:

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

> ⚠️ **Never** commit real credentials to the repository. In production, all secrets are managed by **Azure Key Vault** with **Managed Identity**.

### Local development (Cosmos DB emulator)

```yaml
spring:
  cloud:
    azure:
      cosmos:
        endpoint: https://localhost:8081
        key: C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMcZcLU/zBJ8S4=
        database: finpay
        connection-mode: gateway
        disable-ssl-verification: true
```

---

## 📡 Endpoints

### Account Service — `http://localhost:8081`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/users` | Register a new user |
| `GET` | `/users/{id}` | Get user data |
| `POST` | `/users/{id}/accounts` | Create a financial account for the user |
| `GET` | `/users/{id}/accounts/{accountId}` | Get account details |
| `GET` | `/users/{id}/accounts/{accountId}/balance` | Check balance |
| `GET` | `/users/{id}/accounts/{accountId}/transactions` | Transaction history |

### Chat Service — `http://localhost:8085` *(coming soon)*

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/chat` | Send a message to FinBot (SSE streaming) |
| `GET` | `/chat/history` | Conversation history |

---

## 🗺️ Roadmap

- [x] Project base structure
- [x] Account Service — entities and configuration
- [ ] Account Service — complete endpoints
- [ ] Payment Service — transfers with idempotency
- [ ] Notification Service — events and notifications
- [ ] Audit Service — immutable records
- [ ] Chat Service — FinBot with function calling
- [ ] RAG pipeline with Azure AI Search
- [ ] CI/CD with GitHub Actions
- [ ] Full deployment to Azure App Service

---

## 👤 Author

**Fabiano Quirino da Silva**

[![GitHub](https://img.shields.io/badge/GitHub-fabianoqss-black?style=flat-square&logo=github)](https://github.com/fabianoqss/FinPay)

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

> Project developed as a hands-on study for the **Microsoft AI-200 — Azure AI Cloud Developer Associate** certification 🚀
