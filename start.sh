#!/bin/bash

echo "======================================"
echo "  XIso - Starting Application"
echo "======================================"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running!"
    echo "Please start Docker and try again."
    exit 1
fi

echo "✅ Docker is running"
echo ""

# Check if docker compose is available
if ! command -v docker compose &> /dev/null; then
    echo "❌ Error: docker compose is not installed!"
    echo "Please install docker compose and try again."
    exit 1
fi

echo "✅ docker compose is available"
echo ""

# Start services
echo "🚀 Starting services with docker compose..."
docker compose up -d --build

# Wait for services to be ready
echo ""
echo "⏳ Waiting for services to be ready..."
sleep 10

# Check service status
echo ""
echo "📊 Service Status:"
docker compose ps

echo ""
echo "======================================"
echo "  ✅ Application Started!"
echo "======================================"
echo ""
echo "🌐 Access the application:"
echo "  - Application:      http://localhost"
echo "  - Backend API:      http://localhost/evidence/"
echo "  - PostgreSQL:       localhost:5432"
echo "  - MinIO Storage:    http://localhost:9000"
echo "  - MinIO Console:    http://localhost:9001"
echo ""
echo "Frontend:"
echo "  Open your browser and go to: http://localhost"
echo ""
echo "MinIO Credentials:"
echo "  Username: minioadmin"
echo "  Password: minioadmin"
echo ""
echo "To view logs:     docker compose logs -f"
echo "To stop:          docker compose down"
echo "======================================"
