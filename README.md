# File Storage Microservice

A lightweight Spring Boot microservice that provides file upload, download, listing, and deletion backed by MinIO and PostgreSQL.

## ✅ Features

- Upload files via multipart upload
- Download files by metadata id
- List all stored files (metadata)
- Delete files (both metadata and object)
- Uses MinIO for object storage and PostgreSQL for file metadata
- Built on Spring Boot (3.x+), Spring Data JPA, MinIO Java SDK

---

## ⚙️ Requirements

- Java 21+ (project uses `java.version=21`)
- Maven 3.8+
- Docker (optional, for Postgres/MinIO local quick start)

---

## 🧩 Local Setup (quickstart)

The service uses the following default configuration (see `src/main/resources/application.yml`):

- Server port: `8082`
- Postgres: `jdbc:postgresql://localhost:5432/filedb` (user: `postgres`, password: `admin`)
- MinIO: `http://localhost:9000` (accessKey: `minioadmin`, secretKey: `minioadmin`)
- MinIO bucket: `evidence`

### Option A — Start dependencies with Docker

Run Postgres and MinIO locally with Docker:

```powershell
# Start PostgreSQL
docker run --name filedb -e POSTGRES_PASSWORD=admin -e POSTGRES_USER=postgres -e POSTGRES_DB=filedb -p 5432:5432 -d postgres:15

# Start MinIO with console port 9001 exposed
docker run -p 9000:9000 -p 9001:9001 --name minio -e MINIO_ROOT_USER=minioadmin -e MINIO_ROOT_PASSWORD=minioadmin -v "${PWD}\minio_data:/data" -d quay.io/minio/minio server /data --console-address ":9001"
```

Create the bucket `evidence` (use MinIO Client `mc` or the web console at http://localhost:9001):

```powershell
# Optional: using mc inside docker (Windows PowerShell example)
docker run --rm --network host minio/mc -- alias set myminio http://localhost:9000 minioadmin minioadmin
# Create bucket
docker run --rm --network host minio/mc -- mb myminio/evidence
```

> Tip: If `--network host` doesn't work in your environment, use hostnames or customize the commands for your network settings.

### Option B — Run directly (no docker)

Install and run Postgres and MinIO manually or through Cloud providers. Make sure `application.yml` values match your environment.

---

## 📦 Build and Run

1. Build the project

```powershell
mvn clean package -DskipTests
```

2. Run the application (from project root)

```powershell
java -jar target/file-storage-service-1.0.0.jar
# or
mvn spring-boot:run
```

The app will start on the configured port (default `8082`).

### Docker Build and Run

Build the Docker image:

```powershell
docker build -t file-storage-service:latest .
```

Run the container (use environment variables as needed to point to your DB/MinIO):

```powershell
docker run -p 8082:8082 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/filedb" \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=admin \
  -e MINIO_URL="http://localhost:9000" \
  -e MINIO_ACCESS_KEY=minioadmin \
  -e MINIO_SECRET_KEY=minioadmin \
  -e MINIO_BUCKET=evidence \
  file-storage-service:latest
```

Alternatively, use the included `docker-compose.yml` to build and run all required services:

```powershell
docker-compose up --build
```

---

## 🔐 Configuration

All configuration can be set in `src/main/resources/application.yml`, or through environment variables. Common properties:

- `spring.datasource.url` — JDBC URL
- `spring.datasource.username` — DB user
- `spring.datasource.password` — DB pass
- `minio.url` — MinIO endpoint
- `minio.access-key` — MinIO access key
- `minio.secret-key` — MinIO secret key
- `minio.bucket` — MinIO bucket name
- `server.port` — server port

Example override via environment variables (Windows PowerShell example):

```powershell
$env:SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/filedb';
$env:MINIO_URL='http://localhost:9000';
$env:MINIO_ACCESS_KEY='minioadmin';
$env:MINIO_SECRET_KEY='minioadmin';
mvn spring-boot:run
```

---

## 🚀 API Endpoints

Base URL: `http://localhost:8082`

- POST `/files/upload` — Upload a file (multipart form, field name `file`)

  - Response: `FileMetadata` JSON

- GET `/files` — Get list of all files (metadata)

  - Response: JSON array of `FileMetadata` objects

- GET `/files/download/{id}` — Download file as binary

  - Response: file bytes with `Content-Disposition` header

- DELETE `/files/{id}` — Delete file and metadata
  - Response: confirmation message

### Example cURL commands

Upload a file:

```bash
curl -X POST "http://localhost:8082/files/upload" -F "file=@./my-file.pdf" -H "Content-Type: multipart/form-data"
```

List files:

```bash
curl http://localhost:8082/files
```

Download a file (id=1):

```bash
curl -L http://localhost:8082/files/download/1 -o downloaded.pdf
```

Delete a file (id=1):

```bash
curl -X DELETE http://localhost:8082/files/1
```

---

## 🧪 Tests

Run unit tests:

```powershell
mvn test
```

---

## 🛠️ Internals / Implementation notes

- Object storage handled by MinIO via `MinioClient` (`MinioConfig` sets up `MinioClient` bean).
- File metadata saved in PostgreSQL via Spring Data JPA (`FileMetadataRepository`).
- Uploaded files' stored names are randomized with UUIDs stored in `storedName` field, and download uses `storedName` to fetch from MinIO.
- Database schema auto-update is enabled with `spring.jpa.hibernate.ddl-auto: update` for local development.

---

## 👩‍💻 Developer Tips

- Lombok is used for entities: ensure your IDE has Lombok plugin enabled.
- If you want to run on a different port, set `server.port` in config or `server.port` environment variable.
- For production, secure MinIO and Postgres and use proper credentials and network settings.

---

## 📬 Feedback / Contribution

If you run into issues, open an issue or contact the project owner with steps to reproduce and logs.

---

Happy building! 🎉

---

## 🐳 Optional: Docker Compose (dev)

If you want a simple local environment, create a `docker-compose.yml` next to this README and paste the following snippet. It will start Postgres and MinIO. You can then run the app locally or build the JAR and run it while pointing to these services.

```yaml
version: "3.8"
services:
  postgres:
    image: postgres:15
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=admin
      - POSTGRES_DB=filedb
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  minio:
    image: quay.io/minio/minio
    command: server /data --console-address ":9001"
    environment:
      - MINIO_ROOT_USER=minioadmin
      - MINIO_ROOT_PASSWORD=minioadmin
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - miniodata:/data

volumes:
  pgdata:
  miniodata:
```

If you use Docker Compose, the application `application.yml` values will work out of the box.
