# DevBank Inventory Management System

**DevOps Course-End Project – Implementing Microservices with CodePipeline and ECS**

---

## Project Objective

Demonstrate a complete DevOps pipeline for a containerised microservices application using:

- Java Spring Boot (backend REST API)
- Nginx (frontend static server)
- Docker & Docker Compose (local development and testing)
- AWS CodePipeline, CodeBuild, Amazon ECR, CodeDeploy, and ECS Fargate (CI/CD and production)

The application is a simple inventory management system called **DevBank Inventory Management System**.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        DevBank Microservices                     │
│                                                                   │
│  ┌──────────────────┐          ┌──────────────────────────────┐  │
│  │   Frontend        │  HTTP   │   Backend                     │  │
│  │   (Nginx)         │ ──────► │   (Spring Boot)               │  │
│  │   Port 3000       │         │   Port 8080                   │  │
│  │                   │         │                               │  │
│  │  index.html       │         │  GET /                        │  │
│  │  env-config.js    │         │  GET /health                  │  │
│  │                   │         │  GET /api/inventory           │  │
│  └──────────────────┘          └──────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Frontend

| Item | Detail |
|------|--------|
| Technology | Nginx 1.25 (Alpine), plain HTML/CSS/JavaScript |
| Container port | 3000 |
| Backend URL | Configured via `BACKEND_URL` environment variable |
| Default backend URL | `http://backend:8080` (Docker Compose service name) |

The frontend never hardcodes the backend hostname. At container startup, `docker-entrypoint.sh` reads the `BACKEND_URL` environment variable and writes it into `/usr/share/nginx/html/env-config.js` as `window.ENV.BACKEND_URL`. The browser-side JavaScript reads this value before making API calls.

### Backend

| Item | Detail |
|------|--------|
| Technology | Java 17, Spring Boot 3.2.5, Maven |
| Container port | 8080 |
| Endpoints | `GET /`, `GET /health`, `GET /api/inventory` |
| Data | 10 sample inventory items (in-memory) |

---

## Project Structure

```
devbank-microservices/
│
├── backend/
│   ├── Dockerfile                         # Multi-stage build (Maven → JRE Alpine)
│   ├── pom.xml                            # Maven build descriptor
│   └── src/
│       ├── main/
│       │   ├── java/com/devbank/inventory/
│       │   │   ├── InventoryApplication.java     # Spring Boot entry point
│       │   │   ├── controller/
│       │   │   │   └── InventoryController.java  # REST endpoints
│       │   │   ├── model/
│       │   │   │   └── InventoryItem.java        # Data model
│       │   │   └── service/
│       │   │       └── InventoryService.java     # Business / data layer
│       │   └── resources/
│       │       └── application.properties
│       └── test/
│           └── java/com/devbank/inventory/
│               └── InventoryApplicationTests.java
│
├── frontend/
│   ├── Dockerfile                         # Nginx Alpine image
│   ├── docker-entrypoint.sh               # Writes env-config.js at startup
│   ├── index.html                         # Single-page inventory UI
│   └── nginx.conf                         # Nginx site config (port 3000)
│
├── docker-compose.yml                     # Local multi-container setup
├── buildspec.yml                          # AWS CodeBuild specification
├── README.md                              # This file
└── .gitignore
```

---

## API Endpoints

### Backend (port 8080)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Service liveness – confirms the backend is running |
| GET | `/health` | Health check used by load balancers and ECS |
| GET | `/api/inventory` | Returns full inventory list as JSON |

#### Example responses

`GET /health`
```json
{
  "status": "healthy",
  "service": "DevBank Inventory Backend"
}
```

`GET /api/inventory`
```json
[
  { "id": 1, "name": "Laptop",           "category": "Computers",    "quantity": 45,  "price": 1299.99 },
  { "id": 2, "name": "Desktop Computer", "category": "Computers",    "quantity": 30,  "price":  899.99 },
  { "id": 3, "name": "Monitor",          "category": "Displays",     "quantity": 60,  "price":  349.99 },
  ...
]
```

---

## Prerequisites

| Tool | Minimum version |
|------|----------------|
| Docker | 24.x |
| Docker Compose | v2 (included with Docker Desktop) |
| Java | 17 (for local Maven builds only) |
| Maven | 3.9 (for local Maven builds only) |

---

## How to Build

### Option A – Docker Compose (recommended)

```bash
# Build both images
docker compose build

# Build with no cache (forces full rebuild)
docker compose build --no-cache
```

### Option B – Build images individually

```bash
# Backend
docker build -t devbank-backend:latest ./backend

# Frontend
docker build -t devbank-frontend:latest ./frontend
```

### Option C – Build and test the backend locally with Maven

```bash
cd backend
mvn clean package
```

---

## How to Run

```bash
# Start both services in detached mode
docker compose up -d

# Follow combined logs
docker compose logs -f

# Follow logs for a single service
docker compose logs -f backend
docker compose logs -f frontend
```

Once running:

| Service | URL |
|---------|-----|
| Frontend UI | http://localhost:3000 |
| Backend root | http://localhost:8080 |
| Backend health | http://localhost:8080/health |
| Backend inventory API | http://localhost:8080/api/inventory |

---

## How to Test

### Backend unit and integration tests

```bash
cd backend
mvn test
```

Tests cover:

- `GET /` returns HTTP 200 with `status: running`
- `GET /health` returns HTTP 200 with `status: healthy`
- `GET /api/inventory` returns HTTP 200 with a non-empty JSON array
- The inventory list contains exactly 10 items
- Each item has `id`, `name`, `category`, `quantity`, and `price` fields

### Manual API tests with curl

```bash
# Health check
curl -s http://localhost:8080/health | python3 -m json.tool

# Inventory
curl -s http://localhost:8080/api/inventory | python3 -m json.tool

# Root
curl -s http://localhost:8080/ | python3 -m json.tool
```

### Frontend smoke test

Open http://localhost:3000 in a browser, click **Load Inventory**, and verify:
- The inventory table populates with 10 items.
- The backend status indicator turns green.

---

## How to Stop

```bash
# Stop containers (data preserved)
docker compose down

# Stop containers and remove volumes
docker compose down -v

# Stop and remove images too
docker compose down --rmi all
```

---

## Frontend–Backend Communication

### Inside Docker Compose

The frontend container calls the backend using the Docker Compose **service name** `backend`:

```
BACKEND_URL=http://backend:8080
```

Docker Compose creates an internal network so containers can resolve each other by service name. `localhost` inside the frontend container refers to the frontend container itself, not the backend.

### Runtime configuration flow

```
docker-compose.yml
  └── BACKEND_URL=http://backend:8080
        └── docker-entrypoint.sh (runs at container start)
              └── writes /usr/share/nginx/html/env-config.js
                    └── window.ENV = { "BACKEND_URL": "http://backend:8080" }
                          └── index.html JavaScript reads window.ENV.BACKEND_URL
```

---

## AWS Deployment Architecture

```
Developer pushes code
        │
        ▼
   GitHub Repository
        │
        ▼
  AWS CodePipeline
  ┌─────────────────────────────────────────────────┐
  │  Stage 1 – Source                               │
  │    GitHub (main branch trigger)                 │
  │                                                 │
  │  Stage 2 – Build (AWS CodeBuild)                │
  │    - mvn test                                   │
  │    - docker build backend                       │
  │    - docker build frontend                      │
  │    - docker push → Amazon ECR                   │
  │    - output imagedefinitions.json               │
  │                                                 │
  │  Stage 3 – Deploy (AWS CodeDeploy / ECS)        │
  │    - Update ECS task definition                 │
  │    - Blue/Green deploy frontend ECS service     │
  │    - Rolling deploy backend ECS service         │
  └─────────────────────────────────────────────────┘
        │                     │
        ▼                     ▼
  Frontend ECS Service   Backend ECS Service
  (ECS Fargate)          (ECS Fargate)
  Behind ALB             Internal service
```

### AWS services used

| AWS Service | Purpose |
|-------------|---------|
| GitHub | Source repository and webhook trigger |
| AWS CodePipeline | Orchestrates the CI/CD pipeline |
| AWS CodeBuild | Builds and tests Docker images |
| Amazon ECR | Private Docker image registry |
| AWS CodeDeploy | Blue/Green deployment for frontend ECS service |
| Amazon ECS Fargate | Serverless container runtime |
| Application Load Balancer | Exposes the frontend to the internet |
| AWS VPC | Network isolation |

### ECS Service design

**Frontend ECS Service**
- Launch type: Fargate
- Deployment: Blue/Green via CodeDeploy
- Load balancer: Application Load Balancer (port 80 → container port 3000)
- Environment variable: `BACKEND_URL=http://<backend-service-discovery-endpoint>:8080`

**Backend ECS Service**
- Launch type: Fargate
- Deployment: Rolling update
- Not directly exposed to the internet
- Reachable from the frontend via ECS Service Discovery or internal ALB

---

## CI/CD Pipeline Setup (AWS)

### 1. Create ECR repositories

```bash
aws ecr create-repository --repository-name devbank-frontend --region us-east-1
aws ecr create-repository --repository-name devbank-backend  --region us-east-1
```

### 2. CodeBuild environment variables

Configure these in your CodeBuild project (do not hardcode in source):

| Variable | Example value |
|----------|--------------|
| `AWS_DEFAULT_REGION` | `us-east-1` |
| `FRONTEND_REPOSITORY` | `devbank-frontend` |
| `BACKEND_REPOSITORY` | `devbank-backend` |

`AWS_ACCOUNT_ID` is resolved automatically in `buildspec.yml` via:
```bash
aws sts get-caller-identity --query Account --output text
```

### 3. CodeBuild IAM role – required permissions

```json
{
  "Effect": "Allow",
  "Action": [
    "ecr:GetAuthorizationToken",
    "ecr:BatchCheckLayerAvailability",
    "ecr:GetDownloadUrlForLayer",
    "ecr:BatchGetImage",
    "ecr:PutImage",
    "ecr:InitiateLayerUpload",
    "ecr:UploadLayerPart",
    "ecr:CompleteLayerUpload"
  ],
  "Resource": "*"
}
```

### 4. CodePipeline stages

1. **Source** – Connect to GitHub repository, trigger on push to `main`
2. **Build** – CodeBuild project using `buildspec.yml`
3. **Deploy-Backend** – ECS rolling deploy using `imagedefinitions.json`
4. **Deploy-Frontend** – ECS Blue/Green deploy via CodeDeploy

---

## Notes for AWS Lab

- AWS resource IDs, ARNs, subnet IDs, security group IDs, and target group ARNs are **not included** in this project. These must be created in your AWS lab environment.
- The application is stateless – no database or persistent storage is required.
- For the frontend to reach the backend on ECS, set `BACKEND_URL` in the frontend ECS task definition to the backend's service discovery DNS name or internal load balancer URL.

---

## Quick Reference

```bash
# Build
docker compose build

# Run
docker compose up -d

# View logs
docker compose logs -f

# Run backend tests
cd backend && mvn test

# Stop
docker compose down
```

---

*DevBank Inventory Management System – DevOps Course-End Project*
