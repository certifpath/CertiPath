# 🛡️ CertiPath / XIso - Système de Gestion d'Audit ISO 27001

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4-green)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791)
![MinIO](https://img.shields.io/badge/MinIO-Storage-c72c48)

Une plateforme complète basée sur une architecture **Microservices** pour faciliter la collaboration entre les **RSSI** (Responsables Sécurité) et les **Auditeurs** dans le cadre de la certification ISO 27001.

---

## 🏗️ Architecture du Projet

Le projet suit une architecture distribuée stricte, conteneurisée avec Docker :

| Service | Port (Interne/Docker) | Port (Host) | Description |
| :--- | :--- | :--- | :--- |
| **Frontend** | 80 | **3000** | Interface utilisateur (HTML/JS/CSS) servie par **Nginx**. |
| **API Gateway** | 8080 | **8080** | Point d'entrée unique (**Spring Cloud Gateway**). Gère le routage et le CORS. |
| **Service Registry** | 8761 | **8761** | Annuaire des services (**Eureka Server**). |
| **Auth Service** | 8081 | - | Gestion des utilisateurs, JWT, et **2FA (Google Authenticator)**. |
| **Core Service** | 8083 | - | Gestion des preuves (Evidence) et stockage de fichiers via **MinIO**. |
| **Comment Service** | 8082 | - | Gestion des discussions. Utilise **NATS** pour la communication. |
| **Notification Service**| 8086 | - | Gestion des alertes et notifications in-app. |

### 🗄️ Infrastructure (Docker)
*   **PostgreSQL** : Une seule instance contenant 4 bases de données (`authdb`, `filedb`, `audit_comments_db`, `notificationdb`).
*   **MinIO** : Stockage d'objets compatible S3 pour les fichiers de preuves.
*   **NATS** : Système de messagerie pour les microservices.

---

## ✨ Fonctionnalités Principales

### 🔐 Sécurité & Authentification
*   Inscription et Connexion sécurisée.
*   **Authentification Multi-Facteurs (MFA)** via QR Code (Google Authenticator).
*   Sécurisation via **JWT (JSON Web Tokens)**.
*   Contrôle d'accès basé sur les rôles (RBAC) : `RSSI`, `AUDITEUR`, `ADMIN`.

### 📂 Gestion des Preuves (RSSI)
*   Upload de fichiers de preuves (PDF, Docx, Images).
*   Association aux contrôles ISO 27001 (ex: A.5.1, A.8.2).
*   Workflow de validation : *Brouillon* -> *En Attente*.
*   Visualisation de l'état des preuves.

### 🕵️‍♂️ Audit & Validation (Auditeur)
*   Tableau de bord dédié.
*   Filtrage des preuves par **Entreprise** (Multi-tenant).
*   Téléchargement et visualisation des fichiers.
*   **Validation** ou **Rejet** des preuves avec commentaires.

### 💬 Collaboration & Notifications
*   Système de **commentaires** en temps réel sur chaque preuve.
*   **Notifications** automatiques :
    *   L'auditeur est notifié quand une preuve est soumise.
    *   Le RSSI est notifié quand une preuve est validée ou refusée.

---

## 🚀 Installation et Démarrage

### Prérequis
*   [Docker Desktop](https://www.docker.com/products/docker-desktop/) installé et lancé.
*   Git.

### 1. Cloner le projet
git clone https://github.com/VOTRE_NOM/CertiPath-Microservices.git
cd CertiPath

## 2. Lancer l'application
Le projet utilise un fichier docker-compose.yml unique pour orchestrer tous les services.
# Construire les images et lancer les conteneurs en arrière-plan
docker-compose up --build -d
☕ Patientez quelques minutes lors du premier lancement. Maven doit télécharger les dépendances et compiler les 5 microservices Java.
Vous pouvez suivre l'avancement avec : docker-compose logs -f
## 3. Accéder à l'application
Une fois que tous les services sont "UP" :
👉 Interface Utilisateur : http://localhost:3000
🛠️ Eureka Dashboard : http://localhost:8761
🗃️ MinIO Console : http://localhost:9001 (User: minioadmin / Pass: minioadmin)
🧪 Guide de Test Rapide
Inscription RSSI :
Allez sur localhost:3000, cliquez sur "Créer un compte".
Rôle : RSSI. Société : "MaSociete".
Connectez-vous et scannez le QR Code pour le 2FA.
Dépôt de Preuve :
Sur le Dashboard RSSI, uploadez un fichier pour le contrôle "A.5.1".
La preuve est en "BROUILLON". Cliquez sur le bouton "Envoyer" (📤).
Inscription Auditeur :
Ouvrez une fenêtre de navigation privée.
Créez un compte avec le rôle AUDITEUR.
Validation :
Sur le Dashboard Auditeur, sélectionnez l'entreprise "MaSociete".
Voyez la preuve "EN_ATTENTE".
Ouvrez la modale, ajoutez un commentaire et cliquez sur "Valider".
Notification :
Retournez sur la session RSSI.
Cliquez sur la cloche 🔔 : vous verrez la confirmation de validation.
## 🛠️ Structure du Projet
code
Text
/
├── api-gateway/            # Spring Cloud Gateway (Port 8080)
├── service-registry/       # Eureka Server (Port 8761)
├── auth-service/           # Gestion Identité & JWT
├── core-service/           # Gestion Fichiers & Preuves
├── comment-service/        # Gestion Discussions
├── notification-service/   # Gestion Alertes
├── frontend/               # Site Web statique (Nginx)
├── docker-init/            # Script SQL d'initialisation DB
└── docker-compose.yml      # Orchestration
