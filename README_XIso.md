# CertiPath File Storage Service - XIso

ISO 27001 Evidence Management System with file storage capabilities using MinIO.

## Overview

XIso is a comprehensive evidence management system for ISO 27001 compliance. It allows RSSI (Information System Security Manager) to upload, manage, and track evidence documents related to ISO 27001 Annex A controls.

## Features

- **Evidence Management**: Create, update, delete, and track ISO 27001 compliance evidence
- **File Storage**: Secure file storage using MinIO object storage
- **Status Workflow**: Track evidence through multiple states (Draft, Pending, Validated, Rejected)
- **ISO 27001 Controls**: Map evidence to specific Annex A controls
- **REST API**: Complete RESTful API for evidence and file operations
- **Web Interface**: Modern, responsive web UI in French

## Technology Stack

### Backend

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Data JPA**
- **PostgreSQL 15** - Database
- **MinIO** - Object storage for files
- **Lombok** - Code generation
- **Maven** - Build tool

### Frontend

- **HTML5 / CSS3 / JavaScript**
- **Responsive Design**
- **Dark Theme UI**

## Prerequisites

- Docker & Docker Compose
- Java 21 (for local development)
- Maven 3.9+ (for local development)

## Quick Start with Docker

1. **Clone the repository**

   ```bash
   cd CertiPath-file-storage-service
   ```

2. **Start all services**

   ```bash
   docker-compose up -d
   ```

   This will start:

   - Backend API on `http://localhost:8083`
   - PostgreSQL database on `localhost:5432`
   - MinIO on `http://localhost:9000` (Console: `http://localhost:9001`)

3. **Open the web interface**

   Open `index.html` in your browser (you can use a simple HTTP server):

   ```bash
   # Using Python
   python -m http.server 8080

   # Using Node.js
   npx serve .
   ```

   Then navigate to `http://localhost:8080/index.html`

## Local Development

### Running Backend Locally

1. **Start PostgreSQL and MinIO**

   ```bash
   docker-compose up postgres minio -d
   ```

2. **Run the Spring Boot application**

   ```bash
   ./mvnw spring-boot:run
   ```

   Or on Windows:

   ```bash
   mvnw.cmd spring-boot:run
   ```

3. **API will be available at** `http://localhost:8083`

### Building the Application

```bash
./mvnw clean package
```

## API Endpoints

### Evidence Management

| Method | Endpoint                        | Description                          |
| ------ | ------------------------------- | ------------------------------------ |
| POST   | `/evidence/create`              | Create new evidence with file upload |
| GET    | `/evidence/list`                | List all evidences                   |
| GET    | `/evidence/{id}`                | Get evidence by ID                   |
| GET    | `/evidence/status/{status}`     | Get evidences by status              |
| GET    | `/evidence/control/{controlId}` | Get evidences by control ID          |
| PUT    | `/evidence/{id}/send`           | Send evidence to auditor             |
| PUT    | `/evidence/{id}/validate`       | Validate evidence                    |
| PUT    | `/evidence/{id}/reject`         | Reject evidence                      |
| GET    | `/evidence/{id}/download`       | Download evidence file               |
| DELETE | `/evidence/{id}`                | Delete evidence                      |
| GET    | `/evidence/stats`               | Get statistics                       |

### File Storage

| Method | Endpoint               | Description     |
| ------ | ---------------------- | --------------- |
| POST   | `/files/upload`        | Upload a file   |
| GET    | `/files`               | List all files  |
| GET    | `/files/download/{id}` | Download a file |
| DELETE | `/files/{id}`          | Delete a file   |

## Evidence Status Workflow

```
BROUILLON (Draft)
    ↓
EN_ATTENTE (Pending - Sent to Auditor)
    ↓
VALIDEE (Validated) or REFUSEE (Rejected)
```

## Supported ISO 27001 Controls

The application supports the following Annex A controls:

- **A.5.1** - Politiques de sécurité
- **A.5.2** - Rôles et responsabilités
- **A.6.1** - Analyse des risques
- **A.6.2** - Traitement des risques
- **A.7.1** - Screening du personnel
- **A.7.2** - Formation à la sécurité
- **A.8.1** - Dispositifs des utilisateurs
- **A.8.2** - Gestion des privilèges
- **A.8.23** - Filtrage Web

## Configuration

### Application Properties (`application.yml`)

```yaml
server:
  port: 8083

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/filedb
    username: postgres
    password: admin
  jpa:
    hibernate:
      ddl-auto: update

minio:
  url: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket: evidence
```

### Environment Variables (Docker)

- `SPRING_DATASOURCE_URL` - PostgreSQL connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `MINIO_URL` - MinIO server URL
- `MINIO_ACCESS_KEY` - MinIO access key
- `MINIO_SECRET_KEY` - MinIO secret key
- `MINIO_BUCKET` - MinIO bucket name

## MinIO Console

Access MinIO web console at `http://localhost:9001`

- **Username**: `minioadmin`
- **Password**: `minioadmin`

## Database

PostgreSQL database `filedb` will be automatically created with two tables:

- **evidences** - Evidence metadata
- **files** - File metadata

## File Upload Limits

- Maximum file size: **10MB**
- Supported formats: PDF, DOCX, PNG, JPG, JPEG, XLSX

## Troubleshooting

### MinIO Bucket Not Created

The application automatically creates the `evidence` bucket on startup. If you encounter issues:

1. Access MinIO console at `http://localhost:9001`
2. Manually create a bucket named `evidence`

### Database Connection Issues

Ensure PostgreSQL is running:

```bash
docker-compose ps postgres
```

Check logs:

```bash
docker-compose logs postgres
```

### Port Already in Use

If port 8083 is already in use, modify `application.yml` and `docker-compose.yml` to use a different port.

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── certifpath/
│   │           ├── file_storage_service/
│   │           │   └── FileStorageServiceApplication.java
│   │           └── storage/
│   │               ├── config/
│   │               │   ├── MinioConfig.java
│   │               │   ├── MinioInitializer.java
│   │               │   └── CorsConfig.java
│   │               ├── controller/
│   │               │   ├── StorageController.java
│   │               │   └── EvidenceController.java
│   │               ├── entity/
│   │               │   ├── FileMetadata.java
│   │               │   ├── Evidence.java
│   │               │   └── EvidenceStatus.java
│   │               ├── repository/
│   │               │   ├── FileMetadataRepository.java
│   │               │   └── EvidenceRepository.java
│   │               └── service/
│   │                   ├── StorageService.java
│   │                   └── EvidenceService.java
│   └── resources/
│       ├── application.properties
│       └── application.yml
└── test/
```

## License

Copyright © 2024 CertiPath

## Support

For issues and questions, please open an issue in the repository.
