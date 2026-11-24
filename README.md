# PayFlow — Cloud-Based Intelligent Payment Backend

An educational backend engineering portfolio project built with Java 21, Spring Boot, PostgreSQL, REST APIs, Docker, GitHub Actions, and an optional Amazon Bedrock support assistant.

> **Safety and scope:** This is a payment *simulator*. It does not connect to banks, card networks, payment gateways, or move money. Do not enter real card numbers, bank credentials, or sensitive personal data. The status-update endpoint is intentionally a demo operation, not a real settlement workflow.

## What this project demonstrates

- Java 21 and Spring Boot REST API development
- Layered backend structure: controllers, services, repositories, entities, DTOs
- PostgreSQL persistence using Spring Data JPA
- Request validation and consistent HTTP error responses
- Idempotency-key handling to make retried create requests return the same payment record
- Basic payment lifecycle simulation (`PENDING`, `COMPLETED`, `FAILED`, `REFUNDED`)
- Retrieval-augmented support flow: retrieve relevant local knowledge snippets, attach source names, and abstain when nothing relevant is found
- Optional Amazon Bedrock Runtime integration using the AWS SDK for Java 2.x Converse API
- Unit tests with JUnit 5 and Mockito
- Docker image and Docker Compose local environment
- GitHub Actions Maven test workflow
- Render Blueprint for a demo deployment

This starter is a **modular monolith**, not independently deployed microservices. The payment and support capabilities are separated into modules and API boundaries so they can later be extracted into services. Do not describe this implementation as a production microservices platform.

## Architecture

```text
Client / Postman / curl
        |
        v
Spring Boot REST API
   |                 |
   v                 v
Payment API       Support API
   |                 |
   v                 v
PaymentService   SupportAssistant
   |                 |
   v                 v
PaymentRepository KnowledgeRetriever --> local Markdown knowledge
   |                 |
   v                 v
PostgreSQL       AnswerProvider
                     |-- Local retrieval-only answer (default)
                     `-- Amazon Bedrock Converse (optional)

GitHub Actions: checkout -> Java 21 -> mvn clean verify
Docker: app container + PostgreSQL container
```

## Requirements

- Java 21
- Maven 3.9+
- Docker Desktop (recommended for the easiest local setup)
- Git
- Optional: AWS account with Amazon Bedrock model access and IAM permissions if enabling Bedrock

Spring Boot 3.5.x supports Java 17 and later. This project uses Java 21. See the [Spring Boot system requirements](https://docs.spring.io/spring-boot/3.5/system-requirements.html).

## Run locally with Docker Compose

From the project root:

```bash
docker compose up --build
```

Check health:

```bash
curl http://localhost:8080/api/health
```

Expected response:

```json
{"status":"UP","application":"PayFlow"}
```

Stop the application:

```bash
docker compose down
```

To remove the local database volume as well:

```bash
docker compose down -v
```

## Run locally without Docker

1. Start PostgreSQL and create a database named `payflow` with user `payflow` and password `payflow`.
2. Set environment variables (or update `src/main/resources/application.yml` for local-only development):

```bash
export DB_HOST='localhost'
export DB_PORT='5432'
export DB_NAME='payflow'
export DB_USERNAME='payflow'
export DB_PASSWORD='payflow'
export PORT=8080
export BEDROCK_ENABLED=false
```

3. Run:

```bash
mvn spring-boot:run
```

Run tests:

```bash
mvn clean verify
```

## API examples

### 1. Create a simulated payment

The `Idempotency-Key` header is required. Reuse the same key only when retrying the same logical request.

```bash
curl -X POST http://localhost:8080/api/payments \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: demo-payment-001' \
  -d '{"customerReference":"customer-123","amount":125.50,"currency":"INR"}'
```

### 2. List payments

```bash
curl http://localhost:8080/api/payments
```

### 3. Get one payment

Replace `<PAYMENT_ID>` with the UUID returned when creating a payment.

```bash
curl http://localhost:8080/api/payments/<PAYMENT_ID>
```

### 4. Simulate a status change

```bash
curl -X PATCH http://localhost:8080/api/payments/<PAYMENT_ID>/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"COMPLETED"}'
```

This endpoint exists for demonstration/testing only. A production payment system must not allow arbitrary client-side status changes; state transitions must be controlled by trusted payment-provider callbacks and server-side business rules.

### 5. Ask the support assistant

```bash
curl -X POST http://localhost:8080/api/support/ask \
  -H 'Content-Type: application/json' \
  -d '{"question":"How should I retry a payment if the request times out?"}'
```

The default mode uses simple keyword-overlap retrieval against Markdown files in `src/main/resources/support-knowledge`. It returns source filenames and abstains if no relevant source is found. This is a lightweight RAG demonstration, not embedding-based semantic retrieval.

## Optional Amazon Bedrock integration

The application defaults to local retrieval-only mode and does not require AWS credentials. To use the Bedrock provider:

1. Use an AWS account and Region where your chosen Bedrock model is available and enabled.
2. Configure credentials using an IAM role (recommended) or a local AWS profile. Never put access keys in source code, Git history, screenshots, or the repository.
3. Grant the runtime identity the required Bedrock model-invocation permission (for example, `bedrock:InvokeModel` as required by the selected model/API).
4. Set:

```bash
export BEDROCK_ENABLED=true
export AWS_REGION=us-east-1
export BEDROCK_MODEL_ID='your-enabled-model-id'
```

5. Restart the application and call `/api/support/ask` again.

The integration uses the AWS SDK for Java 2.x Bedrock Runtime Converse API. Model availability, access approval, pricing, and request limits depend on the AWS account and Region. Check the [AWS Java Bedrock examples](https://docs.aws.amazon.com/code-library/latest/ug/java_2_bedrock-runtime_code_examples.html) before enabling it. Bedrock usage may incur charges; keep it disabled for the no-cost local demo.

## Deploy to Render (demo deployment)

The repository includes `render.yaml` and a Dockerfile. Render supports Docker-based services, including JVM applications, and can build the image from the repository.

1. Push this repository to your GitHub account.
2. Sign in to Render and connect GitHub.
3. Choose **New → Blueprint** and select this repository, or create a Docker web service and a PostgreSQL database separately.
4. Review the resources and environment variables before applying the Blueprint.
5. After deployment, test `https://YOUR-SERVICE.onrender.com/api/health`.

See [Render Docker deployment](https://render.com/docs/docker), [Render deploys](https://render.com/docs/deploys), and [Render first deploy](https://render.com/docs/your-first-deploy). Free resource availability and limits can change; confirm current plan details in Render before deploying. Do not assume a free database is permanent or suitable for production.

For a real AWS deployment, a suitable next step is to containerize the service and deploy it to Amazon ECS/Fargate behind an Application Load Balancer, use Amazon RDS for PostgreSQL, store secrets in AWS Secrets Manager, and use IAM roles. S3 receipt storage and API Gateway/Lambda integration are extension tasks, not implemented features in this starter.

## GitHub setup

Create an empty repository on GitHub, for example `payflow-cloud-backend`, then from this folder run:

```bash
git init
git add .
git commit -m "Build PayFlow Spring Boot payment backend"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/payflow-cloud-backend.git
git push -u origin main
```

GitHub Actions runs `mvn clean verify` on pushes and pull requests to `main`.

## Suggested interview walkthrough

1. Explain the controller-service-repository separation and why DTOs are used.
2. Create a payment and explain validation, persistence, and the idempotency key.
3. Retry the same request with the same key and show that it does not create a second row.
4. Explain the support flow: retrieve relevant knowledge, pass grounded context to the answer provider, return sources, and abstain when retrieval finds nothing.
5. Show the optional Bedrock provider and explain how IAM credentials are supplied without hardcoding secrets.
6. Run `mvn clean verify`, show the GitHub Actions workflow, and explain the Docker deployment.
7. Be transparent that the code is a modular monolith and a simulator, and describe how you would split services and harden it for production.

## Important production gaps / future improvements

This is a learning portfolio project, not a production payment platform. Before any real-money use it would need, at minimum:

- Authentication and role-based authorization
- Provider-hosted payment tokenization; never store raw card data
- Signed and replay-protected provider webhooks
- Strict payment state-transition rules and transactional outbox/event handling
- Database migrations (Flyway or Liquibase) instead of `ddl-auto: update`
- Rate limiting, audit trails, structured logs, metrics, tracing, and alerting
- Secrets management, TLS, encryption, threat modelling, and security testing
- Stronger RAG retrieval (embeddings/vector database), evaluation, prompt-injection defenses, and access controls
- Integration tests with Testcontainers and broader coverage of error paths

## Resume wording — use only after you have run and understood the project

**PayFlow – Cloud-Based Intelligent Payment Backend** | Java 21, Spring Boot, REST APIs, PostgreSQL, Docker, GitHub Actions, AWS Bedrock (optional)

- Built a Spring Boot REST backend to create, retrieve, and simulate payment lifecycle updates with PostgreSQL persistence, request validation, and idempotency-key handling.
- Developed a retrieval-grounded support endpoint that searches local payment knowledge, returns source references, and abstains when relevant evidence is unavailable; added optional Amazon Bedrock integration through the AWS SDK for Java.
- Containerized the application with Docker Compose and configured GitHub Actions to run Maven tests on pushes and pull requests.

Only list AWS Bedrock as implemented experience if you have configured AWS access and successfully tested that optional mode. Otherwise write “optional Bedrock integration included; not yet deployed/tested”.
#   p a y f l o w - c l o u d - b a c k e n d  
 