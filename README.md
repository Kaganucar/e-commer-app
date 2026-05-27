# E-Commerce Microservices Application

A full-stack e-commerce backend built with **Spring Boot** and a microservices architecture. Services communicate synchronously via REST/Feign and asynchronously via Apache Kafka. Authentication is handled by Keycloak through the API Gateway.

---

## Architecture

```
Client
  └── API Gateway (port 8222)  ← JWT auth via Keycloak
        ├── Customer Service
        ├── Product Service
        ├── Order Service
        │     ├── → Customer Service (Feign)
        │     ├── → Product Service  (Feign)
        │     ├── → Payment Service  (Feign)
        │     └── → Kafka: order-topic
        └── Payment Service
              └── → Kafka: payment-topic

Kafka
  └── Notification Service
        ├── consumes: order-topic   → Order confirmation email
        └── consumes: payment-topic → Payment confirmation email
```

---

## Services

| Service              | Port  | Database   | Responsibility                          |
|----------------------|-------|------------|-----------------------------------------|
| Config Server        | 8888  | —          | Centralized configuration               |
| Discovery (Eureka)   | 8761  | —          | Service registry & discovery            |
| API Gateway          | 8222  | —          | Routing, JWT authentication             |
| Customer Service     | 8090  | MongoDB    | Customer CRUD                           |
| Product Service      | 8050  | PostgreSQL | Product management & stock              |
| Order Service        | 8070  | PostgreSQL | Order creation & management             |
| Payment Service      | 8060  | PostgreSQL | Payment processing                      |
| Notification Service | —     | MongoDB    | Email notifications via Kafka events    |

---

## Tech Stack

- **Java 17** + **Spring Boot 3**
- **Spring Cloud** (Config, Eureka, Gateway, OpenFeign)
- **Apache Kafka** — async event-driven communication
- **Keycloak 24** — OAuth2 / JWT authentication
- **PostgreSQL** — Order, Payment, Product data
- **MongoDB** — Customer and Notification data
- **Zipkin** — Distributed tracing
- **Thymeleaf** — HTML email templates
- **Docker Compose** — Full infrastructure setup

---

## Getting Started

### Prerequisites

- Docker & Docker Compose
- Java 17+
- Maven

### 1. Start Infrastructure

```bash
docker-compose up -d
```

This starts: PostgreSQL, MongoDB, Kafka, Zookeeper, Zipkin, Keycloak, MailDev, pgAdmin, Mongo Express.

### 2. Configure Keycloak

1. Open Keycloak admin at `http://localhost:9098` (admin / admin)
2. Create a new realm: `micro-service`
3. Create a client for the gateway with **Client Authentication** enabled

### 3. Start Services (in order)

```bash
# 1. Config Server
cd services/config-server && ./mvnw spring-boot:run

# 2. Discovery
cd services/discovery && ./mvnw spring-boot:run

# 3. All other services (any order)
cd services/customer    && ./mvnw spring-boot:run
cd services/product     && ./mvnw spring-boot:run
cd services/order       && ./mvnw spring-boot:run
cd services/payment     && ./mvnw spring-boot:run
cd services/notification && ./mvnw spring-boot:run

# 4. Gateway
cd services/gateway && ./mvnw spring-boot:run
```

---

## Infrastructure URLs

| Service       | URL                          |
|---------------|------------------------------|
| API Gateway   | http://localhost:8222         |
| Eureka        | http://localhost:8761         |
| Keycloak      | http://localhost:9098         |
| Zipkin        | http://localhost:9411         |
| pgAdmin       | http://localhost:5050         |
| Mongo Express | http://localhost:8081         |
| MailDev       | http://localhost:1080         |

---

## API Endpoints

All requests go through the API Gateway (`http://localhost:8222`) and require a valid JWT token.

### Customer
| Method | Endpoint                        | Description        |
|--------|---------------------------------|--------------------|
| POST   | `/api/v1/customers`             | Create customer    |
| PUT    | `/api/v1/customers`             | Update customer    |
| GET    | `/api/v1/customers`             | List all customers |
| GET    | `/api/v1/customers/{id}`        | Get by ID          |

### Product
| Method | Endpoint                        | Description        |
|--------|---------------------------------|--------------------|
| POST   | `/api/v1/products`              | Create product     |
| POST   | `/api/v1/products/purchase`     | Purchase products  |
| GET    | `/api/v1/products`              | List all products  |
| GET    | `/api/v1/products/{id}`         | Get by ID          |

### Order
| Method | Endpoint                        | Description        |
|--------|---------------------------------|--------------------|
| POST   | `/api/v1/orders`                | Create order       |
| GET    | `/api/v1/orders`                | List all orders    |
| GET    | `/api/v1/orders/{id}`           | Get by ID          |
| GET    | `/api/v1/order-lines/{orderId}` | Get order lines    |

### Payment
| Method | Endpoint                        | Description        |
|--------|---------------------------------|--------------------|
| POST   | `/api/v1/payments`              | Process payment    |

---

## Order Flow

1. Client sends `POST /api/v1/orders` with JWT token
2. Gateway validates token via Keycloak
3. Order Service validates customer (→ Customer Service via Feign)
4. Order Service purchases products (→ Product Service via Feign)
5. Order Service requests payment (→ Payment Service via Feign)
6. Payment Service publishes event to **payment-topic**
7. Order Service publishes event to **order-topic**
8. Notification Service consumes both events and sends confirmation emails

---

## Author

**Kagan Ucar** — [GitHub](https://github.com/Kaganucar)