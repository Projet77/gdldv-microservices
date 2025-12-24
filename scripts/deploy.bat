@echo off
REM ============================================
REM GDLDV Microservices Deployment Script (Windows)
REM ============================================

echo ========================================
echo GDLDV Microservices Deployment
echo ========================================
echo.

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running. Please start Docker Desktop.
    pause
    exit /b 1
)
echo [OK] Docker is running

REM Check if .env file exists
if not exist .env (
    echo [WARNING] .env file not found
    echo [INFO] Copying .env.example to .env
    copy .env.example .env
    echo.
    echo [WARNING] Please update .env file with your configuration!
    pause
)

echo.
echo ========================================
echo Building Services
echo ========================================
echo [INFO] Building Docker images...
docker-compose -f docker-compose-full.yml build --no-cache
if errorlevel 1 (
    echo [ERROR] Build failed
    pause
    exit /b 1
)
echo [OK] Build completed

echo.
echo ========================================
echo Starting Services
echo ========================================

echo [INFO] Starting databases...
docker-compose -f docker-compose-full.yml up -d mysql-vehicle mysql-reservation mysql-user mysql-rental
timeout /t 10 /nobreak >nul

echo [INFO] Starting Eureka Server...
docker-compose -f docker-compose-full.yml up -d eureka-server
timeout /t 30 /nobreak >nul

echo [INFO] Starting Config Server...
docker-compose -f docker-compose-full.yml up -d config-server
timeout /t 20 /nobreak >nul

echo [INFO] Starting API Gateway...
docker-compose -f docker-compose-full.yml up -d api-gateway
timeout /t 20 /nobreak >nul

echo [INFO] Starting Business Services...
docker-compose -f docker-compose-full.yml up -d vehicle-service reservation-service user-service
timeout /t 30 /nobreak >nul

echo [INFO] Starting Rental Service...
docker-compose -f docker-compose-full.yml up -d rental-service
timeout /t 20 /nobreak >nul

echo.
echo ========================================
echo Deployment Status
echo ========================================
docker-compose -f docker-compose-full.yml ps

echo.
echo ========================================
echo Service URLs
echo ========================================
echo Eureka Dashboard: http://localhost:8761
echo API Gateway: http://localhost:8000
echo Vehicle Service: http://localhost:8001/swagger-ui.html
echo Reservation Service: http://localhost:8002/swagger-ui.html
echo User Service: http://localhost:8003/swagger-ui.html
echo Rental Service: http://localhost:8004/swagger-ui.html
echo.
echo [OK] Deployment completed successfully!
pause
