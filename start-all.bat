@echo off
REM ========================================
REM Script de démarrage GDLDV
REM Démarre tous les microservices
REM ========================================

echo.
echo ╔════════════════════════════════════════════╗
echo ║  GDLDV - Démarrage des Microservices       ║
echo ╚════════════════════════════════════════════╝
echo.

REM 1. Config Server
echo [1/8] Démarrage Config Server (Port 8888)...
start "Config Server" cmd /k "cd config-server && mvn spring-boot:run"
echo Attente 30 secondes pour Config Server...
timeout /t 30 /nobreak >nul

REM 2. Eureka Server
echo.
echo [2/8] Démarrage Eureka Server (Port 8761)...
start "Eureka Server" cmd /k "cd eureka-server && mvn spring-boot:run"
echo Attente 30 secondes pour Eureka Server...
timeout /t 30 /nobreak >nul

REM 3. User Service
echo.
echo [3/8] Démarrage User Service (Port 8003)...
start "User Service" cmd /k "cd user-service && mvn spring-boot:run"

REM 4. Vehicle Service
echo.
echo [4/8] Démarrage Vehicle Service (Port 8001)...
start "Vehicle Service" cmd /k "cd vehicle-service && mvn spring-boot:run"

REM 5. Reservation Service
echo.
echo [5/8] Démarrage Reservation Service (Port 8002)...
start "Reservation Service" cmd /k "cd reservation-service && mvn spring-boot:run"

REM 6. Rental Service
echo.
echo [6/8] Démarrage Rental Service (Port 8004)...
start "Rental Service" cmd /k "cd rental-service && mvn spring-boot:run"

echo Attente 40 secondes pour les services métier...
timeout /t 40 /nobreak >nul

REM 7. API Gateway
echo.
echo [7/8] Démarrage API Gateway (Port 8080)...
start "API Gateway" cmd /k "cd api-gateway && mvn spring-boot:run"

echo.
echo ╔════════════════════════════════════════════╗
echo ║  Tous les services backend démarrent...    ║
echo ╚════════════════════════════════════════════╝
echo.
echo Attente finale 20 secondes...
timeout /t 20 /nobreak >nul

REM 8. Frontend
echo.
echo [8/8] Démarrage Frontend React (Port 3000)...
start "Frontend React" cmd /k "cd frontend && npm run dev"

echo.
echo ╔════════════════════════════════════════════╗
echo ║           DÉMARRAGE TERMINÉ !              ║
echo ╚════════════════════════════════════════════╝
echo.
echo Ouvrez ces URLs dans votre navigateur:
echo.
echo   - Eureka Dashboard:  http://localhost:8761
echo   - User Service API:  http://localhost:8003/swagger-ui.html
echo   - Frontend React:    http://localhost:3000
echo.
echo Appuyez sur une touche pour ouvrir Eureka Dashboard...
pause >nul
start http://localhost:8761

echo.
echo Script terminé. Vous pouvez fermer cette fenêtre.
pause
