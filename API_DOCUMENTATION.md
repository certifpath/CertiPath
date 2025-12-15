# XIso API Documentation

## Base URL

```
http://localhost:8083
```

## Authentication

Currently, no authentication is required. Future versions will implement JWT-based authentication.

---

## Evidence Endpoints

### 1. Create Evidence

Create a new evidence with file upload.

**Endpoint:** `POST /evidence/create`

**Content-Type:** `multipart/form-data`

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| title | string | Yes | Evidence title |
| controlId | string | Yes | ISO 27001 control ID (e.g., A.5.1) |
| description | string | No | Evidence description |
| file | file | Yes | File to upload (max 10MB) |

**Success Response:**

```json
{
  "id": 1,
  "title": "Security Policy Document",
  "description": "Main security policy",
  "controlId": "A.5.1",
  "fileId": "uuid-filename.pdf",
  "status": "BROUILLON",
  "createdAt": "2024-02-10T14:30:00",
  "updatedAt": null,
  "createdBy": "RSSI Admin",
  "updatedBy": null
}
```

**Example:**

```bash
curl -X POST http://localhost:8083/evidence/create \
  -F "title=Security Policy v2.0" \
  -F "controlId=A.5.1" \
  -F "description=Annual security policy update" \
  -F "file=@policy.pdf"
```

---

### 2. List All Evidences

Get all evidences in the system.

**Endpoint:** `GET /evidence/list`

**Success Response:**

```json
[
  {
    "id": 1,
    "title": "Security Policy Document",
    "description": "Main security policy",
    "controlId": "A.5.1",
    "fileId": "uuid-filename.pdf",
    "status": "BROUILLON",
    "createdAt": "2024-02-10T14:30:00",
    "updatedAt": null,
    "createdBy": "RSSI Admin",
    "updatedBy": null
  }
]
```

**Example:**

```bash
curl http://localhost:8083/evidence/list
```

---

### 3. Get Evidence by ID

Get a specific evidence by its ID.

**Endpoint:** `GET /evidence/{id}`

**URL Parameters:**
| Name | Type | Description |
|------|------|-------------|
| id | integer | Evidence ID |

**Success Response:**

```json
{
  "id": 1,
  "title": "Security Policy Document",
  "description": "Main security policy",
  "controlId": "A.5.1",
  "fileId": "uuid-filename.pdf",
  "status": "BROUILLON",
  "createdAt": "2024-02-10T14:30:00",
  "updatedAt": null,
  "createdBy": "RSSI Admin",
  "updatedBy": null
}
```

**Error Response (404):**

```json
{
  "timestamp": "2024-02-10T14:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Evidence not found with id: 999"
}
```

**Example:**

```bash
curl http://localhost:8083/evidence/1
```

---

### 4. Get Evidences by Status

Filter evidences by status.

**Endpoint:** `GET /evidence/status/{status}`

**URL Parameters:**
| Name | Type | Description |
|------|------|-------------|
| status | string | Evidence status: BROUILLON, EN_ATTENTE, VALIDEE, REFUSEE |

**Success Response:**

```json
[
  {
    "id": 1,
    "title": "Security Policy Document",
    "status": "BROUILLON",
    ...
  }
]
```

**Example:**

```bash
curl http://localhost:8083/evidence/status/BROUILLON
curl http://localhost:8083/evidence/status/EN_ATTENTE
```

---

### 5. Get Evidences by Control ID

Filter evidences by ISO 27001 control.

**Endpoint:** `GET /evidence/control/{controlId}`

**URL Parameters:**
| Name | Type | Description |
|------|------|-------------|
| controlId | string | ISO 27001 control ID (e.g., A.5.1) |

**Success Response:**

```json
[
  {
    "id": 1,
    "title": "Security Policy Document",
    "controlId": "A.5.1",
    ...
  }
]
```

**Example:**

```bash
curl http://localhost:8083/evidence/control/A.5.1
```

---

### 6. Send Evidence to Auditor

Change evidence status from BROUILLON to EN_ATTENTE.

**Endpoint:** `PUT /evidence/{id}/send`

**URL Parameters:**
| Name | Type | Description |
|------|------|-------------|
| id | integer | Evidence ID |

**Success Response:**

```json
{
  "id": 1,
  "status": "EN_ATTENTE",
  "updatedAt": "2024-02-10T15:00:00",
  "updatedBy": "RSSI Admin",
  ...
}
```

**Error Response (400):**

```json
{
  "timestamp": "2024-02-10T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Only draft evidences can be sent to auditor"
}
```

**Example:**

```bash
curl -X PUT http://localhost:8083/evidence/1/send
```

---

### 7. Validate Evidence (Auditor)

Change evidence status to VALIDEE.

**Endpoint:** `PUT /evidence/{id}/validate`

**URL Parameters:**
| Name | Type | Description |
|------|------|-------------|
| id | integer | Evidence ID |

**Success Response:**

```json
{
  "id": 1,
  "status": "VALIDEE",
  "updatedAt": "2024-02-10T16:00:00",
  "updatedBy": "Auditor",
  ...
}
```

**Example:**

```bash
curl -X PUT http://localhost:8083/evidence/1/validate
```

---

### 8. Reject Evidence (Auditor)

Change evidence status to REFUSEE.

**Endpoint:** `PUT /evidence/{id}/reject`

**URL Parameters:**
| Name | Type | Description |
|------|------|-------------|
| id | integer | Evidence ID |

**Success Response:**

```json
{
  "id": 1,
  "status": "REFUSEE",
  "updatedAt": "2024-02-10T16:00:00",
  "updatedBy": "Auditor",
  ...
}
```

**Example:**

```bash
curl -X PUT http://localhost:8083/evidence/1/reject
```

---

### 9. Download Evidence File

Download the file associated with an evidence.

**Endpoint:** `GET /evidence/{id}/download`

**URL Parameters:**
| Name | Type | Description |
|------|------|-------------|
| id | integer | Evidence ID |

**Success Response:**
Binary file with `Content-Disposition` header

**Example:**

```bash
curl -O -J http://localhost:8083/evidence/1/download
```

---

### 10. Delete Evidence

Delete an evidence and its associated file.

**Endpoint:** `DELETE /evidence/{id}`

**URL Parameters:**
| Name | Type | Description |
|------|------|-------------|
| id | integer | Evidence ID |

**Success Response:**

```
Evidence deleted successfully
```

**Example:**

```bash
curl -X DELETE http://localhost:8083/evidence/1
```

---

### 11. Get Statistics

Get evidence statistics.

**Endpoint:** `GET /evidence/stats`

**Success Response:**

```json
{
  "total": 10,
  "draft": 3,
  "pending": 4,
  "validated": 2,
  "rejected": 1
}
```

**Example:**

```bash
curl http://localhost:8083/evidence/stats
```

---

## File Storage Endpoints

### 1. Upload File

Upload a file to MinIO storage.

**Endpoint:** `POST /files/upload`

**Content-Type:** `multipart/form-data`

**Parameters:**
| Name | Type | Required | Description |
|------|------|----------|-------------|
| file | file | Yes | File to upload |

**Success Response:**

```json
{
  "id": 1,
  "fileName": "document.pdf",
  "contentType": "application/pdf",
  "size": 1024000,
  "url": "http://localhost:9000/evidence/uuid-document.pdf",
  "storedName": "uuid-document.pdf"
}
```

**Example:**

```bash
curl -X POST http://localhost:8083/files/upload \
  -F "file=@document.pdf"
```

---

### 2. List All Files

Get metadata for all uploaded files.

**Endpoint:** `GET /files`

**Success Response:**

```json
[
  {
    "id": 1,
    "fileName": "document.pdf",
    "contentType": "application/pdf",
    "size": 1024000,
    "url": "http://localhost:9000/evidence/uuid-document.pdf",
    "storedName": "uuid-document.pdf"
  }
]
```

**Example:**

```bash
curl http://localhost:8083/files
```

---

### 3. Download File

Download a file by its metadata ID.

**Endpoint:** `GET /files/download/{id}`

**URL Parameters:**
| Name | Type | Description |
|------|------|-------------|
| id | integer | File metadata ID |

**Success Response:**
Binary file with `Content-Disposition` header

**Example:**

```bash
curl -O -J http://localhost:8083/files/download/1
```

---

### 4. Delete File

Delete a file and its metadata.

**Endpoint:** `DELETE /files/{id}`

**URL Parameters:**
| Name | Type | Description |
|------|------|-------------|
| id | integer | File metadata ID |

**Success Response:**

```
File deleted successfully
```

**Example:**

```bash
curl -X DELETE http://localhost:8083/files/1
```

---

## Health Check

### Health Status

Check if the service is running.

**Endpoint:** `GET /health`

**Success Response:**

```json
{
  "status": "UP",
  "timestamp": "2024-02-10T14:30:00",
  "service": "XIso File Storage Service",
  "version": "1.0.0"
}
```

**Example:**

```bash
curl http://localhost:8083/health
```

---

## Error Codes

| Status Code | Description                     |
| ----------- | ------------------------------- |
| 200         | Success                         |
| 201         | Created                         |
| 400         | Bad Request                     |
| 404         | Not Found                       |
| 413         | Payload Too Large (file > 10MB) |
| 500         | Internal Server Error           |

---

## Evidence Status Values

| Status     | Description                                          |
| ---------- | ---------------------------------------------------- |
| BROUILLON  | Draft - Evidence created but not yet sent to auditor |
| EN_ATTENTE | Pending - Evidence sent to auditor, awaiting review  |
| VALIDEE    | Validated - Evidence approved by auditor             |
| REFUSEE    | Rejected - Evidence rejected by auditor              |

---

## Supported ISO 27001 Controls

- A.5.1 - Politiques de sécurité
- A.5.2 - Rôles et responsabilités
- A.6.1 - Analyse des risques
- A.6.2 - Traitement des risques
- A.7.1 - Screening du personnel
- A.7.2 - Formation à la sécurité
- A.8.1 - Dispositifs des utilisateurs
- A.8.2 - Gestion des privilèges
- A.8.23 - Filtrage Web

---

## File Upload Constraints

- **Maximum file size:** 10MB
- **Supported formats:** PDF, DOCX, PNG, JPG, JPEG, XLSX
- **Storage:** MinIO object storage
- **Bucket:** evidence
