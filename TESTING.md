# XIso - Quick Setup and Testing Guide

## 🚀 Quick Start (5 minutes)

### Step 1: Start the Backend Services

**On Linux/Mac:**

```bash
chmod +x start.sh
./start.sh
```

**On Windows:**

```bash
start.bat
```

This will start:

- ✅ PostgreSQL Database (port 5432)
- ✅ MinIO Object Storage (port 9000, console: 9001)
- ✅ Backend API (port 8083)

### Step 2: Open the Frontend

**Option A - Simple File Open:**
Open `index.html` directly in your browser (double-click the file)

**Option B - With HTTP Server (Recommended):**

Using Python:

```bash
python -m http.server 8080
```

Using Node.js:

```bash
npx serve . -p 8080
```

Then open: `http://localhost:8080/index.html`

---

## 🧪 Testing the Application

### Test 1: Create Evidence

1. Scroll down to "Déposer une Nouvelle Preuve"
2. Fill in:
   - **Title**: "Politique de sécurité informatique 2024"
   - **Control**: Select "A.5.1 - Politiques de sécurité"
   - **Description**: "Document principal des politiques de sécurité"
   - **File**: Upload a PDF or any supported file (max 10MB)
3. Click "Déposer la Preuve"
4. You should see a success message
5. The evidence appears in the table below

### Test 2: View Statistics

Check the sidebar and top statistics:

- Total evidences count should increase
- Draft count should increase
- Coverage percentage updates

### Test 3: Send to Auditor

1. Find a BROUILLON (Draft) evidence in the table
2. Click the 📤 (send) icon
3. Confirm the action
4. Status should change to "EN_ATTENTE" (Pending)
5. The send button should disappear

### Test 4: Download Evidence

1. Click the ⬇️ (download) icon on any evidence
2. The file should download with the correct filename

### Test 5: View Comments

1. Click the 💬 (comments) icon
2. A modal should open showing evidence details
3. Comments section displays (demo comments for now)

### Test 6: Delete Evidence

1. Click the 🗑️ (delete) icon
2. Confirm the deletion
3. Evidence is removed from the list
4. Statistics are updated

---

## 📊 API Testing with cURL

### Create Evidence

```bash
curl -X POST http://localhost:8083/evidence/create \
  -F "title=Test Policy Document" \
  -F "controlId=A.5.1" \
  -F "description=This is a test evidence" \
  -F "file=@/path/to/your/file.pdf"
```

### List All Evidences

```bash
curl http://localhost:8083/evidence/list
```

### Get Evidence by ID

```bash
curl http://localhost:8083/evidence/1
```

### Send to Auditor

```bash
curl -X PUT http://localhost:8083/evidence/1/send
```

### Download Evidence

```bash
curl -O http://localhost:8083/evidence/1/download
```

### Delete Evidence

```bash
curl -X DELETE http://localhost:8083/evidence/1
```

### Get Statistics

```bash
curl http://localhost:8083/evidence/stats
```

---

## 🔍 Verify Services

### Check Backend API

```bash
curl http://localhost:8083/evidence/list
```

Expected: JSON array (empty or with evidences)

### Check PostgreSQL

```bash
docker exec -it certipath-file-storage-service-postgres-1 psql -U postgres -d filedb
```

Then run:

```sql
\dt  -- List tables
SELECT * FROM evidences;  -- View evidences
\q  -- Quit
```

### Check MinIO

1. Open browser: `http://localhost:9001`
2. Login:
   - Username: `minioadmin`
   - Password: `minioadmin`
3. Navigate to "Buckets" → "evidence"
4. You should see uploaded files

---

## 🐛 Troubleshooting

### Backend Not Starting

**Check logs:**

```bash
docker-compose logs file-storage-service
```

**Common issues:**

- Port 8083 already in use → Change port in `application.yml` and `docker-compose.yml`
- Database connection failed → Check PostgreSQL is running: `docker-compose ps`

### Frontend Not Loading Evidences

**Check browser console (F12):**

- CORS errors → Backend should have CORS enabled (already configured)
- Network errors → Ensure backend is running on port 8083
- 404 errors → Check API endpoint URLs in index.html

**Test backend directly:**

```bash
curl http://localhost:8083/evidence/list
```

### File Upload Fails

**Check file size:**

- Maximum: 10MB
- Supported formats: PDF, DOCX, PNG, JPG, JPEG, XLSX

**Check MinIO:**

```bash
docker-compose logs minio
```

### Database Errors

**Reset database:**

```bash
docker-compose down -v  # Remove volumes
docker-compose up -d    # Restart
```

---

## 📁 Test Data

Create test evidences for all controls:

```bash
# A.5.1 - Security Policies
curl -X POST http://localhost:8083/evidence/create \
  -F "title=Security Policy v2.0" \
  -F "controlId=A.5.1" \
  -F "description=Main security policy document" \
  -F "file=@test-policy.pdf"

# A.6.1 - Risk Analysis
curl -X POST http://localhost:8083/evidence/create \
  -F "title=Risk Assessment Report Q4 2024" \
  -F "controlId=A.6.1" \
  -F "description=Quarterly risk assessment" \
  -F "file=@risk-report.pdf"

# A.8.1 - User Devices
curl -X POST http://localhost:8083/evidence/create \
  -F "title=Device Management Procedure" \
  -F "controlId=A.8.1" \
  -F "description=User device security procedures" \
  -F "file=@device-policy.docx"
```

---

## 🔄 Reset Everything

To completely reset the application:

```bash
# Stop all services
docker-compose down -v

# Remove all volumes (data will be lost)
docker volume prune -f

# Restart
docker-compose up -d
```

---

## ✅ Expected Results

After following this guide:

1. ✅ Backend API running on port 8083
2. ✅ PostgreSQL with `evidences` and `files` tables
3. ✅ MinIO with `evidence` bucket
4. ✅ Frontend displaying evidence management interface
5. ✅ Able to create, view, send, download, and delete evidences
6. ✅ Statistics updating correctly
7. ✅ All ISO 27001 controls selectable

---

## 📝 Next Steps

1. **Add Authentication**: Implement Spring Security for user authentication
2. **Add Auditor Role**: Create separate interface for auditors
3. **Add Email Notifications**: Send emails when evidence is sent to auditor
4. **Add Comments System**: Implement backend for comments
5. **Add Audit Trail**: Track all changes to evidences
6. **Add Report Generation**: Export evidences to PDF/Excel

---

## 📞 Support

If you encounter issues:

1. Check logs: `docker-compose logs -f`
2. Verify all services: `docker-compose ps`
3. Test API directly with cURL
4. Check browser console (F12) for frontend errors

For detailed API documentation, see [README_XIso.md](README_XIso.md)
