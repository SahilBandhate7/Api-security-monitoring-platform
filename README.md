# APIScan — API Security Testing & Monitoring Platform

A production-grade **SaaS platform** for automated API security testing, vulnerability detection, and compliance monitoring. Built with **Java 21 + Spring Boot 3** (backend) and **Next.js 14** (frontend).

---

## ✨ Features

- **Automated Security Scanning** — 8 built-in checks: SQL Injection, XSS, SSRF, CORS misconfiguration, Header Security, Rate Limiting, Auth Bypass, Sensitive Data Exposure
- **Multi-Tenant Organizations** — Team workspaces with RBAC (Owner / Admin / Tester)
- **Real-Time Scan Status** — Async job processing via RabbitMQ with live status polling
- **Vulnerability Reports** — Detailed findings with severity ratings, evidence, and remediation
- **PDF Export** — Downloadable security reports (Pro+ plans)
- **Stripe Billing** — Subscription management with quota enforcement
- **Audit Logging** — Track all sensitive actions per organization
- **Dashboard Analytics** — Vulnerability trends, scan history, usage metrics

---

## 🏗️ Tech Stack

### Backend

| Layer | Technology |
|---|---|
| Runtime | Java 21 LTS (virtual threads) |
| Framework | Spring Boot 3.2 |
| ORM | Spring Data JPA + Hibernate 6 |
| Database | PostgreSQL 15 |
| Cache | Redis 7 (Lettuce) |
| Queue | RabbitMQ 3.13 |
| Security | Spring Security 6 + JWT (jjwt) |
| Migrations | Flyway |
| API Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Metrics | Actuator + Prometheus + Micrometer |
| Build | Maven |

### Frontend

| Layer | Technology |
|---|---|
| Framework | Next.js 14 (App Router) |
| Styling | Tailwind CSS + shadcn/ui |
| State | Zustand |
| Data Fetching | TanStack Query v5 |
| Forms | React Hook Form + Zod |
| Charts | Recharts |
| HTTP Client | Axios |

### Infrastructure

| Service | Technology |
|---|---|
| Containers | Docker + Docker Compose |
| Backend Hosting | AWS ECS Fargate |
| Frontend Hosting | Vercel |
| Database | AWS RDS PostgreSQL |
| Cache | AWS ElastiCache (Redis) |
| Queue | Amazon MQ (RabbitMQ) |
| CI/CD | GitHub Actions |
| Monitoring | Prometheus + Grafana |
| Error Tracking | Sentry |
| Payments | Stripe |

---

## 📋 Prerequisites

- **Java 21** — [Download Temurin JDK](https://adoptium.net/)
- **Maven 3.9+** — [Download](https://maven.apache.org/download.cgi)
- **Node.js 18+** — [Download](https://nodejs.org/)
- **Docker Desktop** — [Download](https://www.docker.com/products/docker-desktop)
- **Git** — [Download](https://git-scm.com/)

---

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/apiscan.git
cd apiscan
```

### 2. Start Infrastructure Services

```bash
# Start PostgreSQL, Redis, RabbitMQ
docker compose -f docker-compose.dev.yml up -d
```

### 3. Run the Backend

```bash
cd backend

# Copy and configure environment variables
cp .env.example .env
# Edit .env with your local settings

# Run with Maven
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The backend starts at **<http://localhost:8080>**  
Swagger UI available at **<http://localhost:8080/swagger-ui.html>**

### 4. Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend starts at **<http://localhost:3000>**

### 5. Full Stack (Docker Compose)

```bash
# Build and run everything
docker compose up --build
```

---

## 📁 Project Structure

```
SAAS/
├── frontend/                    # Next.js 14 Application
│   ├── src/
│   │   ├── app/                 # App Router pages
│   │   ├── components/          # React components
│   │   ├── hooks/               # Custom hooks
│   │   ├── lib/                 # API client, utils
│   │   ├── store/               # Zustand stores
│   │   └── types/               # TypeScript types
│   └── package.json
│
├── backend/                     # Spring Boot 3 Application
│   ├── src/main/java/com/apiscan/
│   │   ├── config/              # Spring configuration
│   │   ├── auth/                # Authentication module
│   │   ├── organization/        # Org & team management
│   │   ├── project/             # Project management
│   │   ├── scan/                # Scan orchestration
│   │   ├── scanner/             # Security scanning engine
│   │   │   └── checks/          # Individual security checks
│   │   ├── report/              # Report management
│   │   ├── billing/             # Stripe integration
│   │   ├── domain/              # JPA Entities
│   │   ├── repository/          # Spring Data Repositories
│   │   ├── security/            # JWT + RBAC components
│   │   ├── middleware/          # Interceptors (quota, rate-limit)
│   │   └── common/              # Shared utils, DTOs, enums
│   ├── src/main/resources/
│   │   ├── db/migration/        # Flyway SQL migrations
│   │   ├── application.yml      # Base config
│   │   ├── application-dev.yml  # Dev overrides
│   │   └── application-prod.yml # Production overrides
│   ├── pom.xml
│   └── Dockerfile
│
├── .github/workflows/           # CI/CD pipelines
├── docker-compose.yml           # Full stack
├── docker-compose.dev.yml       # Infrastructure only
└── README.md
```

---

## 🔌 API Reference

### Auth — `POST /api/auth/*`

| Endpoint | Description |
|---|---|
| `/signup` | Register new user |
| `/login` | Login → access + refresh tokens |
| `/logout` | Revoke refresh token |
| `/refresh` | Issue new access token |
| `/forgot-password` | Send reset email |
| `/reset-password` | Set new password |

### Organizations — `/api/orgs`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/` | Create organization |
| GET | `/{orgId}` | Get org details |
| PATCH | `/{orgId}` | Update org settings |
| DELETE | `/{orgId}` | Delete org (Owner only) |
| POST | `/{orgId}/invite` | Invite team member |
| GET | `/{orgId}/members` | List members |

### Projects — `/api/orgs/{orgId}/projects`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/` | List projects (paginated) |
| POST | `/` | Create project |
| GET | `/{projectId}` | Get project + scan history |
| PATCH | `/{projectId}` | Update project |
| DELETE | `/{projectId}` | Delete project |

### Scans — `/api/orgs/{orgId}/scans`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/` | Trigger new scan |
| GET | `/` | List scans (paginated) |
| GET | `/{scanId}` | Full scan + vulnerabilities |
| GET | `/{scanId}/status` | Poll scan status |
| GET | `/{scanId}/report` | Vulnerability report |
| GET | `/{scanId}/export` | PDF export (Pro+) |

### Billing — `/api/billing`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/plans` | List available plans |
| POST | `/checkout` | Stripe checkout session |
| POST | `/portal` | Customer portal session |
| GET | `/usage` | Current quota usage |

---

## 🔐 RBAC Permission Matrix

| Action | Owner | Admin | Tester |
|---|---|---|---|
| Delete Organization | ✅ | ❌ | ❌ |
| Invite / Remove Members | ✅ | ✅ | ❌ |
| Change Member Role | ✅ | ❌ | ❌ |
| Manage Billing | ✅ | ❌ | ❌ |
| Create / Delete Projects | ✅ | ✅ | ❌ |
| Trigger Scans | ✅ | ✅ | ✅ |
| View Reports | ✅ | ✅ | ✅ |
| Export PDF Reports | ✅ | ✅ | ❌ |
| View Audit Logs | ✅ | ✅ | ❌ |

---

## 💰 Subscription Plans

| Feature | Free | Pro ($29/mo) | Enterprise ($99/mo) |
|---|---|---|---|
| Scans / Month | 50 | 1,000 | Unlimited |
| Team Members | 1 | 10 | Unlimited |
| Projects | 2 | 20 | Unlimited |
| Security Checks | 3 basic | All 8 | All + custom |
| PDF Export | ❌ | ✅ | ✅ |
| Audit Logs | ❌ | 30 days | 1 year |
| API Access | ❌ | ✅ | ✅ |
| Priority Support | ❌ | ❌ | ✅ |

---

## 🌍 Environment Variables

See [`backend/.env.example`](backend/.env.example) for the full list. Key variables:

| Variable | Description |
|---|---|
| `DATABASE_URL` | PostgreSQL JDBC connection string |
| `REDIS_URL` | Redis connection URL |
| `RABBITMQ_HOST` | RabbitMQ hostname |
| `JWT_ACCESS_SECRET` | JWT signing secret (≥64 chars) |
| `JWT_REFRESH_SECRET` | JWT refresh secret (≥64 chars) |
| `STRIPE_SECRET_KEY` | Stripe API secret key |
| `STRIPE_WEBHOOK_SECRET` | Stripe webhook signing secret |
| `SENTRY_DSN` | Sentry error tracking DSN |
| `FRONTEND_URL` | Frontend app URL (for CORS) |

---

## 📦 Deployment

### Backend → AWS ECS Fargate

1. Push Docker image to ECR
2. ECS service auto-deploys on image update
3. Uses AWS Secrets Manager for env vars
4. RDS PostgreSQL + ElastiCache Redis + Amazon MQ

### Frontend → Vercel

1. Connect GitHub repo to Vercel
2. Auto-deploys on push to `main`
3. Environment variables configured in Vercel dashboard

### CI/CD → GitHub Actions

- **Backend**: Test → Build Docker → Push ECR → Deploy ECS
- **Frontend**: Lint → Build → Deploy Vercel

---

## 📄 License

This project is proprietary software. All rights reserved.
