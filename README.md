# MediLabo Solutions

Microservices-based application to manage patients, medical notes, and diabetes risk assessment.
Services are exposed through a Spring Cloud Gateway and the UI is provided by a Thymeleaf front.

## Architecture

**Microservices**
- **patient-service** (Spring Boot + H2) — port **8081**
- **notes-service** (Spring Boot + MongoDB) — port **8083**
- **assessment-service** (Spring Boot) — port **8084**
- **gateway-service** (Spring Cloud Gateway) — port **8080**
- **front-service** (Spring Boot + Thymeleaf) — port **8082**
- **mongo** (MongoDB) — port **27017**


## Prerequisites

- Docker Desktop (Windows: WSL2 enabled)
- Docker Compose (included with Docker Desktop)

## Run with Docker

From the repository root:

```bash
docker compose up --build
```

### Access

- Front UI: `http://localhost:8082/patients`
    
- Gateway (API entrypoint):
    
    - Patients: `http://localhost:8080/patients`
        
    - Notes (example): `http://localhost:8080/notes/patient/2`
        
    - Assessment (example): `http://localhost:8080/assessments/patient/4`
        

### Authentication

Basic authentication is enabled (Spring Security).

- Username: `admin`
    
- Password: `admin`
    

## Stop

`docker compose down`

## Clean volumes (Mongo data)

`docker compose down -v`