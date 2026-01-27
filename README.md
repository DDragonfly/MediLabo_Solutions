# MediLabo_Solutions
P9 OpenClassrooms Microservices app

# MediLabo Solutions

This project is a microservices-based application developed as part of the OpenClassrooms Java Developer path.

## Architecture (Sprint 1)

The application is composed of three independent microservices:

- **Patient service** (Spring Boot, SQL, H2)
- **Gateway** (Spring Cloud Gateway)
- **Front** (Spring Boot, Thymeleaf)

## Microservices

### Patient service
- Manages patient records
- Exposes REST endpoints for CRUD operations
- Uses an SQL database (H2)
- Secured with HTTP Basic authentication

**Port:** 8081

### Gateway
- Entry point for all client requests
- Routes requests to backend microservices

**Port:** 8080

### Front
- Simple user interface to display patient data
- Communicates only with the Gateway

**Port:** 8082

## Authentication (development)
For development and testing purposes, HTTP Basic authentication is used.

Username: admin  
Password: admin


## Test data
The SQL database is automatically populated at startup with the four patient test cases provided in the project requirements.

## How to run (Sprint 1)

Start the services in the following order:

1. Patient service
2. Gateway
3. Front

Then open:

- Patient list: http://localhost:8082/patients
- Patient detail: http://localhost:8082/patients/{id}

