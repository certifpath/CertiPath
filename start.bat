@echo off
echo ======================================
echo   XIso - Starting Application
echo ======================================
echo.

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo Error: Docker is not running!
    echo Please start Docker and try again.
    pause
    exit /b 1
)

echo Docker is running
echo.

REM Start services
echo Starting services with docker-compose...
docker-compose up -d

REM Wait for services to be ready
echo.
echo Waiting for services to be ready...
timeout /t 10 /nobreak >nul

REM Check service status
echo.
echo Service Status:
docker-compose ps

echo.
echo ======================================
echo   Application Started!
echo ======================================
echo.
echo Access the application:
echo   - Application:      http://localhost
echo   - Backend API:      http://localhost/evidence/
echo   - PostgreSQL:       localhost:5432
echo   - MinIO Storage:    http://localhost:9000
echo   - MinIO Console:    http://localhost:9001
echo.
echo Frontend:
echo   Open your browser and go to: http://localhost
echo.
echo MinIO Credentials:
echo   Username: minioadmin
echo   Password: minioadmin
echo.
echo To view logs:     docker-compose logs -f
echo To stop:          docker-compose down
echo ======================================
echo.
pause
