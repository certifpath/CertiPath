
# Audit Comment Service

Microservice de gestion des commentaires pour la plateforme d'audit.

### Quick Start

### Prérequis
- Java 17+
- Docker & Docker Compose
- Maven 3.8+

### Installation

1. **Cloner et configurer**
```bash
git clone <repository-url>
cd CommentService
```

2. **Démarrer l'infrastructure**
```bash
docker-compose up -d postgres nats
```

3. **Compiler l'application**
```bash
mvn clean package
```

4. **Lancer le service**
```bash
java -jar target/*.jar
```

##  API Endpoints


| `POST` | `/api/v1/comments` >> Créer un commentaire 
| `GET` | `/api/v1/comments/audit/{auditId}` >> Lister les commentaires d'un audit 
| `PUT` | `/api/v1/comments/{id}` >> Modifier un commentaire 
| `DELETE` | `/api/v1/comments/{id}` >> Supprimer un commentaire 
| `PATCH` | `/api/v1/comments/{id}/resolve` >> Marquer comme résolu 

##  Docker

```bash
# Build l'image
docker build -t audit-comment-service:1.0.0 .

# Lancer tous les services
docker-compose up -d

# Vérifier l'état
docker-compose ps
```

##  Configuration

Variables d'environnement principales :
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/audit_comments_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=nouhaila2026
NATS_SERVERS=nats://localhost:4222
```

##  Monitoring

- **Health Check** : `http://localhost:8082/actuator/health`
- **Swagger UI** : `http://localhost:8082/swagger-ui.html`
- **Logs** : `docker-compose logs -f audit-comment-service`

##  Base de Données

**Schéma principal** :
```sql
audit_comments (id, content, audit_id, requirement_id, proof_id, is_resolved, created_by, created_at)
audit_comment_history (id, comment_id, old_content, new_content, modified_by, action_type)
```

##  Tests

```bash
# Tests unitaires
mvn test

# Tests d'intégration
mvn verify -P integration-tests

# Coverage
mvn jacoco:report
```

##  Sécurité

- Authentification JWT
- Rôles : `AUDITOR`, `REVIEWER`, `ADMIN`
- OAuth2 Resource Server

##  Événements

Le service publie des événements NATS sur `audit.comments.events` :
- `COMMENT_CREATED`
- `COMMENT_UPDATED`
- `COMMENT_RESOLVED`
- `COMMENT_DELETED`

##  Dépannage

### PostgreSQL inaccessible
```bash
docker-compose restart postgres
```

### NATS non connecté
```bash
telnet localhost 4222
```
