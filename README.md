<div align="center">

# 🚀 Elevate

### A Distributed Microservices LinkedIn Clone

*Not just another LinkedIn clone — a production-style, cloud-deployed microservices platform built to demonstrate real-world backend engineering.*

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-GKE-326CE5?style=for-the-badge&logo=kubernetes)](https://cloud.google.com/kubernetes-engine)
[![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-Event%20Driven-231F20?style=for-the-badge&logo=apachekafka)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)
[![Neo4j](https://img.shields.io/badge/Neo4j-Graph%20DB-018bff?style=for-the-badge&logo=neo4j)](https://neo4j.com/)

<img src="https://raw.githubusercontent.com/microsoft/vscode-icons/main/icons/dark/architecture.svg" width="0" height="0" alt=""/>

<!-- 🎬 Replace this with an actual demo GIF of the app in action -->
<img src="./docs/assets/demo.gif" alt="Elevate demo" width="800"/>

</div>

---

## 📖 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Services](#-services)
- [Screenshots](#-screenshots)
- [Getting Started](#-getting-started)
- [Deployment](#-deployment)
- [API Reference](#-api-reference)
- [Roadmap](#-roadmap)
- [Author](#-author)

---

## 🌟 Overview

**Elevate** is a distributed, event-driven microservices platform inspired by LinkedIn — built from the ground up to explore real backend engineering challenges: service discovery, polyglot persistence, async messaging, container orchestration, and cloud deployment.

Instead of a monolith, every core capability lives in its own independently deployable Spring Boot service, all wired together through **Eureka**, routed via an **API Gateway**, and deployed on **Google Kubernetes Engine (GKE)**.

> 💡 Built to showcase distributed systems design, not just CRUD — service discovery, event-driven notifications, polyglot databases, and container orchestration are first-class citizens here.

---

## 🏗️ Architecture

```mermaid
graph TD
    Client([Client / Postman]) --> Ingress[GKE Ingress<br/>External LB]
    Ingress --> Gateway[API Gateway<br/>Spring Cloud Gateway]

    Gateway --> Eureka[(Discovery Server<br/>Netflix Eureka)]
    UserSvc -.register.-> Eureka
    PostSvc -.register.-> Eureka
    ConnSvc -.register.-> Eureka
    NotifSvc -.register.-> Eureka
    UploadSvc -.register.-> Eureka

    Gateway --> UserSvc[User Service<br/>:1920]
    Gateway --> PostSvc[Post Service<br/>:9010]
    Gateway --> ConnSvc[Connection Service<br/>:9030]
    Gateway --> NotifSvc[Notification Service<br/>:9040]
    Gateway --> UploadSvc[Uploader Service<br/>:9050]

    UserSvc --> UserDB[(PostgreSQL<br/>userDB)]
    PostSvc --> PostDB[(PostgreSQL<br/>postDB)]
    NotifSvc --> NotifDB[(PostgreSQL<br/>notificationDB)]
    ConnSvc --> Neo4j[(Neo4j<br/>Graph DB)]

    PostSvc -- publishes --> Kafka{{Apache Kafka}}
    Kafka -- consumes --> NotifSvc

    UploadSvc --> Cloudinary[(Cloudinary)]
    UploadSvc --> GCS[(Google Cloud Storage)]

    style Gateway fill:#6DB33F,color:#fff
    style Eureka fill:#3C873A,color:#fff
    style Kafka fill:#231F20,color:#fff
    style UserDB fill:#4169E1,color:#fff
    style PostDB fill:#4169E1,color:#fff
    style NotifDB fill:#4169E1,color:#fff
    style Neo4j fill:#018bff,color:#fff
```

---

## 🧰 Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 4.1.0, Spring Cloud 2025.1.2 |
| **Service Discovery** | Netflix Eureka |
| **API Gateway** | Spring Cloud Gateway (WebFlux) |
| **Messaging** | Apache Kafka |
| **Relational DB** | PostgreSQL |
| **Graph DB** | Neo4j (social connections) |
| **Auth** | JWT (JJWT) |
| **File Storage** | Cloudinary, Google Cloud Storage |
| **Containerization** | Docker, Jib (Maven) |
| **Orchestration** | Kubernetes (Google Kubernetes Engine) |
| **API Testing** | Postman |

---

## 🧩 Services

| Service | Responsibility | Port | Database |
|---|---|:---:|---|
| 🌐 **API Gateway** | Single entry point, routing, load balancing | `8080` | – |
| 🔎 **Discovery Server** | Service registry (Eureka) | `8761` | – |
| 👤 **User Service** | Auth (JWT), signup/login, profiles | `1920` | PostgreSQL |
| 📝 **Post Service** | Create/like/comment on posts | `9010` | PostgreSQL |
| 🤝 **Connection Service** | Social graph — connections, first-degree network | `9030` | Neo4j |
| 🔔 **Notification Service** | Kafka-consumer, real-time notifications | `9040` | PostgreSQL |
| 📁 **Uploader Service** | Media upload (images) via Cloudinary/GCS | `9050` | – |

---
## ⚡ Getting Started

### Prerequisites
- Java 21
- Maven
- Docker
- A running Kafka cluster (or use `k8s/kafka.yml`)
- PostgreSQL & Neo4j instances

### Run locally

```bash
# Clone the repo
git clone https://github.com/Bhavnish15/Elevate.git
cd Elevate

# Start each service (repeat per service directory)
cd userService
./mvnw spring-boot:run
```

Each service reads its local config from `application.properties`, pointing to `localhost` for databases and Eureka.

---

## ☁️ Deployment

Elevate is fully containerized and deployed on **Google Kubernetes Engine**:

```bash
# Build & push images (via Jib)
./mvnw clean package

# Apply Kubernetes manifests
kubectl apply -f k8s/
```

Each service ships with:
- A dedicated `Deployment` + `Service`
- Resource-tuned CPU/memory limits
- `application-k8s.properties` profile for cluster-specific config (service DNS names, env-injected secrets)
- Secrets management via Kubernetes `Secret` objects (never hardcoded)

An **Ingress** exposes the API Gateway externally, fronting the entire platform behind a single load balancer.

<sub>🎬 *Add a terminal recording GIF of `kubectl apply -f k8s/` → `kubectl get pods` going green*</sub>

---

## 📡 API Reference

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/users/auth/signup` | Register a new user |
| `POST` | `/api/v1/users/auth/login` | Authenticate & receive JWT |
| `POST` | `/posts` | Create a new post |
| `POST` | `/posts/{id}/like` | Like a post |
| `GET` | `/connections/first-degree` | Get first-degree connections |
| `POST` | `/uploads` | Upload media (image) |

<sub>Full collection available via the project's Postman workspace.</sub>

---

## 🗺️ Roadmap

- [ ] Comments on posts
- [ ] WebSocket-based real-time notifications
- [ ] Search service (Elasticsearch)
- [ ] CI/CD pipeline (GitHub Actions → GKE)
- [ ] Horizontal Pod Autoscaling

---

## 👤 Author

**Bhavnish Bhardwaj**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0077B5?style=flat-square&logo=linkedin)](https://linkedin.com/in/bhavnishbharadwaj)
[![Portfolio](https://img.shields.io/badge/Portfolio-Visit-black?style=flat-square&logo=vercel)](https://portfolio-bhavnish15.vercel.app)

<div align="center">
<sub>⭐️ If you found this project interesting, consider giving it a star!</sub>
</div>
