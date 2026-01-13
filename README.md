# Media Service

Spring Boot service that handles file uploads, storage (S3-compatible), and downloading from chat.

## Prerequisites
- Java 21
- Maven 3.9+
- (Optional) Docker + Docker Compose

## Environment (.env)
Create `media-service/.env` environment variables, as shown in .env.example.


## Run locally (Maven)
From `media-service/`:

```
./load-env.ps1
```

This script builds the project, loads `.env`, and starts the API module.

The service starts on `http://localhost:8083`.

## Run with Docker
From `media-service/`:

```
docker network create chat-net
```

(Only needed once; `docker-compose.yml` expects this external network.) Then:

```
docker compose up --build
```

This starts:
- Media Service (port 8083)
- MongoDB container (port 27018)
- MinIO container (compatible S3 storage, ports 9000/9001)

When running with Docker, it auto-configures to use the local MinIO instance instead of cloud S3.

## Useful endpoints
- Health check: `http://localhost:8083/actuator/health`
- OpenAPI: `http://localhost:8083/v3/api-docs`
- Swagger UI: `http://localhost:8083/swagger-ui`
- REST:
  - `POST /media/upload`
  - `GET /media/{id}`
  - `GET /media/{id}/download`
