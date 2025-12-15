# XIso - Quick Reference Card

## 🚀 Start Application

**Linux/Mac:**

```bash
./start.sh
```

**Windows:**

```bash
start.bat
```

## 🌐 URLs

| Service       | URL                   | Credentials             |
| ------------- | --------------------- | ----------------------- |
| Backend API   | http://localhost:8083 | None                    |
| MinIO Console | http://localhost:9001 | minioadmin / minioadmin |
| PostgreSQL    | localhost:5432        | postgres / admin        |
| Frontend      | Open index.html       | None                    |

## 📋 Quick Commands

### Check Health

```bash
curl http://localhost:8083/health
```

### Create Evidence

```bash
curl -X POST http://localhost:8083/evidence/create \
  -F "title=My Evidence" \
  -F "controlId=A.5.1" \
  -F "description=Description here" \
  -F "file=@myfile.pdf"
```

### List All Evidences

```bash
curl http://localhost:8083/evidence/list
```

### Get Statistics

```bash
curl http://localhost:8083/evidence/stats
```

### Send to Auditor

```bash
curl -X PUT http://localhost:8083/evidence/1/send
```

### Download Evidence

```bash
curl -O -J http://localhost:8083/evidence/1/download
```

### Delete Evidence

```bash
curl -X DELETE http://localhost:8083/evidence/1
```

## 🐳 Docker Commands

### Start All Services

```bash
docker-compose up -d
```

### Stop All Services

```bash
docker-compose down
```

### View Logs

```bash
docker-compose logs -f
```

### Restart Backend

```bash
docker-compose restart file-storage-service
```

### Complete Reset

```bash
docker-compose down -v
docker-compose up -d
```

## 📊 Evidence Status Flow

```
BROUILLON → EN_ATTENTE → VALIDEE
                      ↘ REFUSEE
```

## 🎯 ISO 27001 Controls

- A.5.1 - Politiques de sécurité
- A.5.2 - Rôles et responsabilités
- A.6.1 - Analyse des risques
- A.6.2 - Traitement des risques
- A.7.1 - Screening du personnel
- A.7.2 - Formation à la sécurité
- A.8.1 - Dispositifs des utilisateurs
- A.8.2 - Gestion des privilèges
- A.8.23 - Filtrage Web

## 🔍 Troubleshooting

### Backend Not Starting

```bash
docker-compose logs file-storage-service
```

### Check Database

```bash
docker exec -it certipath-file-storage-service-postgres-1 psql -U postgres -d filedb
```

### Check MinIO

Open http://localhost:9001 and login

### Port Already in Use

Edit `application.yml` and `docker-compose.yml` to change ports

## 📚 Documentation

- **README_XIso.md** - Full documentation
- **API_DOCUMENTATION.md** - API reference
- **TESTING.md** - Testing guide
- **IMPLEMENTATION_SUMMARY.md** - Implementation details

## 🎨 Frontend Features

- Create/upload evidence
- View all evidences
- Filter by status/control
- Download files
- Send to auditor
- Delete evidence
- View statistics

## 🔐 Default Credentials

**MinIO:**

- User: minioadmin
- Pass: minioadmin

**PostgreSQL:**

- User: postgres
- Pass: admin
- DB: filedb

## ⚠️ Important Notes

- Max file size: 10MB
- Supported formats: PDF, DOCX, PNG, JPG, JPEG, XLSX
- Port 8083 must be available
- Docker must be running

## 💡 Tips

1. Start with `./start.sh` or `start.bat`
2. Wait 10 seconds for services to be ready
3. Open index.html in browser
4. Create test evidences using the form
5. View MinIO console to see uploaded files
6. Check PostgreSQL for evidence metadata

---

**Need Help?** See TESTING.md for detailed examples.
