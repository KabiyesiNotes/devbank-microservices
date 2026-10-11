# DevBank Inventory Management System

**DevOps Course-End Project**
*Implementing Microservices with CodePipeline and ECS*

---

## Project Objective

This project demonstrates a production-style microservices architecture deployed on AWS ECS Fargate with a fully automated CI/CD pipeline. It consists of two independently containerized services — a Spring Boot backend REST API and a Node.js frontend — orchestrated locally with Docker Compose and deployed to AWS using CodePipeline, CodeBuild, ECR, and CodeDeploy.

---

## Architecture Overview

```
                          ┌─────────────────────────┐
                          │   GitHub Repository      │
                          └────────────┬────────────┘
                                       │ push / PR
                          ┌────────────▼────────────┐
                          │   AWS CodePipeline       │
                          └────────────┬────────────┘
                                       │
                          ┌────────────▼────────────┐
                          │   AWS CodeBuild          │
                          │  • Run backend tests     │
                          │  • Build Docker images   │
                          │  • Push to ECR           │
                          └──────────┬───────────────┘
                          ┌──────────▼──────────┐
                          │  Amazon ECR          │
                          │  devbank-backend     │
                          │  devbank-frontend    │
                          └──────────┬──────────┘
                                     │
               ┌─────────────────────┴──────────────────────┐
               │                                            │
   ┌───────────▼───────────┐               ┌───────────────▼──────────┐
   │  CodeDeploy (Blue/Green│               │  ECS Rolling Update       │
   │  ECS Deploy)           │               │                          │
   │  frontend ECS service  │               │  backend ECS service     │
   └───────────┬───────────┘               └──────────────┬───────────┘
               │                                          │
   ┌───────────▼────────────┐              ┌──────────────▼───────────┐
   │  Application Load      │              │  ECS Fargate Task         │
   │  Balancer (public)     │              │  (internal)               │
   └───────────┬────────────┘              └──────────────────────────┘
               │
         (browser traffic)
```

---

## Services

### Backend

| Property | Value |
|----------|-------|
| Language | Java 17 / Spring Boot 3.2 |
| Build tool | Maven |
| Container port | 8080 |
| Directory | `backend/` |

**REST Endpoints**

| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | Confirms the backend is running |
| GET | `/health` | Health check — returns `{"status":"healthy"}` |
| GET | `/api/inventory` | Returns all inventory items as JSON |

**Sample response — `GET /api/inventory`**

```json
[
  { "id": 1, "name": "Laptop",           "category": "Computing",   "quantity": 45, "status": "Available" },
  { "id": 2, "name": "Desktop Computer", "category": "Computing",   "quantity": 30, "status": "Available" },
  { "id": 3, "name": "Monitor",          "category": "Display",     "quantity": 60, "status": "Available" },
  { "id": 4, "name": "Keyboard",         "category": "Peripherals", "quantity": 80, "status": "Available" },
  { "id": 5, "name": "Mouse",            "category": "Peripherals", "quantity": 75, "status": "Available" }
]
```

### Frontend

| Property | Value |
|----------|-------|
| Language | Node.js 18 / Express |
| Container port | 3000 |
| Directory | `frontend/` |

The frontend is a single-page application served by an Express server. It reads `BACKEND_URL` from the environment and exposes it to the browser via `GET /config`. The browser then calls the backend API directly.

**Frontend endpoints**

| Path | Description |
|------|-------------|
| `GET /` | Serves the inventory management UI |
| `GET /config` | Returns `{"backendUrl": "..."}` from env var |
| `GET /health` | Frontend health check |

---

## Project Structure

```
devbank-microservices/
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/devbank/inventory/
│       │   │   ├── InventoryApplication.java
│       │   │   ├── controller/InventoryController.java
│       │   │   ├── model/InventoryItem.java
│       │   │   └── service/InventoryService.java
│       │   └── resources/
│       │       └── application.properties
│       └── test/
│           └── java/com/devbank/inventory/
│               └── controller/InventoryControllerTest.java
├── frontend/
│   ├── Dockerfile
│   ├── package.json
│   ├── server.js
│   └── public/
│       └── index.html
├── buildspec.yml
├── docker-compose.yml
├── .gitignore
└── README.md
```

---

## Prerequisites

| Tool | Minimum version |
|------|----------------|
| Docker | 24.x |
| Docker Compose | 2.x (`docker compose` plugin) |
| Java (local dev only) | 17 |
| Maven (local dev only) | 3.9 |

---

## Running Locally

### 1 — Build and start with Docker Compose

```bash
# Build both images
docker compose build

# Start both services (detached)
docker compose up -d

# Follow logs
docker compose logs -f

# Stop and remove containers
docker compose down
```

After startup (allow ~30 s for the backend to initialise):

- Frontend UI:   http://localhost:3000
- Backend API:   http://localhost:8080
- Health check:  http://localhost:8080/health
- Inventory API: http://localhost:8080/api/inventory

### 2 — Rebuild from scratch (no cache)

```bash
docker compose build --no-cache
docker compose up -d
```

### 3 — Backend-only local run (without Docker)

```bash
cd backend
mvn spring-boot:run
```

---

## Testing

### Backend unit tests

```bash
cd backend
mvn test
```

The test suite covers:

- `GET /`             → HTTP 200
- `GET /health`       → HTTP 200, `status=healthy`
- `GET /api/inventory`→ HTTP 200, array of 8 items with correct fields

### Manual API tests (curl)

```bash
# Health check
curl -s http://localhost:8080/health | python3 -m json.tool

# Inventory list
curl -s http://localhost:8080/api/inventory | python3 -m json.tool

# Frontend health
curl -s http://localhost:3000/health

# Frontend config (shows resolved BACKEND_URL)
curl -s http://localhost:3000/config
```

---

## Environment Variables

| Variable | Service | Default | Description |
|----------|---------|---------|-------------|
| `BACKEND_URL` | frontend | `http://backend:8080` | Full URL of the backend service |
| `PORT` | frontend | `3000` | Port the frontend Express server listens on |

When deployed to AWS ECS, set `BACKEND_URL` in the frontend ECS task definition to the appropriate internal endpoint (ECS Service Connect name or internal ALB DNS).

---

## Docker Images

Both Dockerfiles use a multi-stage build to keep the final image small and secure:

**Backend**
- Build stage: `maven:3.9.6-eclipse-temurin-17` (compiles the fat JAR)
- Runtime stage: `eclipse-temurin:17-jre-alpine` (~85 MB)

**Frontend**
- Build stage: `node:18-alpine` (`npm ci --omit=dev` — installs prod deps only)
- Runtime stage: `node:18-alpine` (copies only `node_modules`, `server.js`, `public/`)

Both containers run as a non-root user for security.

---

## AWS Deployment Architecture

### Prerequisites (manual steps in AWS console/CLI)

Before running the CI/CD pipeline, create the following AWS resources:

1. **ECR repositories**
   - `devbank-backend`
   - `devbank-frontend`

2. **ECS Cluster** (Fargate)
   - Cluster name: `devbank-cluster`

3. **ECS Task Definitions**
   - `devbank-backend-task` — container `backend`, port 8080
   - `devbank-frontend-task` — container `frontend`, port 3000, env var `BACKEND_URL`

4. **ECS Services**
   - `devbank-backend-service` — rolling update deployment
   - `devbank-frontend-service` — CodeDeploy Blue/Green deployment

5. **Application Load Balancer** (for frontend)
   - Listener on port 80
   - Two target groups for Blue/Green (required by CodeDeploy)

6. **IAM roles**
   - CodeBuild service role with ECR push + ECS describe permissions
   - CodeDeploy role with ECS task definition registration + service update permissions

### CodePipeline stages

```
Source (GitHub)
  └─▶  Build (CodeBuild)
         • runs backend tests
         • builds & pushes both Docker images to ECR
         • outputs imagedefinitions-backend.json
         • outputs imagedefinitions-frontend.json
  └─▶  Deploy Backend (ECS rolling update)
         • uses imagedefinitions-backend.json
  └─▶  Deploy Frontend (CodeDeploy Blue/Green ECS)
         • uses imagedefinitions-frontend.json
```

### CodeBuild environment variables

Set these in the CodeBuild project (or CodePipeline action):

| Variable | Example value | Notes |
|----------|--------------|-------|
| `AWS_DEFAULT_REGION` | `us-east-1` | Set automatically by CodeBuild |
| `FRONTEND_REPOSITORY` | `devbank-frontend` | ECR repository name |
| `BACKEND_REPOSITORY` | `devbank-backend` | ECR repository name |

`AWS_ACCOUNT_ID` is resolved automatically via `aws sts get-caller-identity` — do not hardcode it.

### Frontend-to-backend communication on AWS

When deployed to ECS, set the `BACKEND_URL` environment variable in the frontend task definition to the backend's internal address. Options:

| Option | Value | Notes |
|--------|-------|-------|
| ECS Service Connect | `http://backend:8080` | Recommended — uses AWS Cloud Map |
| Internal ALB | `http://internal-alb-dns:8080` | Alternative |

---

## CI/CD Flow

```
Developer pushes to GitHub main branch
    │
    ▼
CodePipeline Source stage detects change
    │
    ▼
CodeBuild (buildspec.yml)
    ├── install Java 17
    ├── mvn test  (fails pipeline if tests fail)
    ├── docker build backend  → tag with short commit SHA
    ├── docker build frontend → tag with short commit SHA
    ├── docker push both to ECR (versioned + :latest)
    ├── write imagedefinitions-backend.json
    └── write imagedefinitions-frontend.json
    │
    ▼
Deploy Backend
    └── ECS rolling update using imagedefinitions-backend.json
    │
    ▼
Deploy Frontend
    └── CodeDeploy Blue/Green ECS using imagedefinitions-frontend.json
         ├── New task set started (Green)
         ├── ALB shifts traffic
         └── Old task set terminated (Blue)
```

---

## Useful Commands

```bash
# View running containers
docker compose ps

# View real-time logs
docker compose logs -f

# Restart a single service
docker compose restart backend

# Rebuild a single service
docker compose up -d --build frontend

# Remove everything including volumes
docker compose down -v

# Run backend tests standalone
cd backend && mvn test

# Check backend health directly
curl http://localhost:8080/health

# Check frontend config endpoint
curl http://localhost:3000/config
```

---

## Notes

- No AWS account IDs, resource ARNs, subnet IDs, or security group IDs are hardcoded anywhere in this project. All AWS-specific values must be provided as environment variables or created in the AWS console during lab setup.
- The frontend communicates with the backend using the `BACKEND_URL` environment variable so the same Docker image can be used in both Docker Compose (internal hostname `backend`) and AWS ECS (service discovery endpoint) without rebuilding.
- The `buildspec.yml` file is at the project root. CodeBuild is configured to look for it there.
