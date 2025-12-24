@echo off
REM ========================================
REM Guide de démarrage IntelliJ GDLDV
REM À utiliser si Maven CLI ne fonctionne pas
REM ========================================

echo.
echo ╔════════════════════════════════════════════════════════╗
echo ║  GDLDV - Guide de démarrage avec IntelliJ IDEA         ║
echo ╚════════════════════════════════════════════════════════╝
echo.
echo IMPORTANT: Utilisez ce guide si Maven en ligne de commande
echo ne fonctionne pas à cause de l'erreur JNI.
echo.
echo ════════════════════════════════════════════════════════
echo ÉTAPE 1: Ouvrir le projet dans IntelliJ IDEA
echo ════════════════════════════════════════════════════════
echo.
echo 1. Ouvrez IntelliJ IDEA
echo 2. File → Open
echo 3. Sélectionnez le dossier:
echo    %CD%
echo 4. Attendez l'indexation Maven (barre de progression en bas)
echo.
pause
echo.
echo ════════════════════════════════════════════════════════
echo ÉTAPE 2: Démarrer les services (DANS CET ORDRE)
echo ════════════════════════════════════════════════════════
echo.
echo 1. Config Server (Port 8888)
echo    → Cherchez: config-server/src/.../ConfigServerApplication.java
echo    → Clic droit → Run 'ConfigServerApplication'
echo    → Attendez: "Started ConfigServerApplication"
echo.
pause
echo.
echo 2. Eureka Server (Port 8761)
echo    → Cherchez: eureka-server/src/.../EurekaServerApplication.java
echo    → Clic droit → Run 'EurekaServerApplication'
echo    → Attendez: "Started EurekaServerApplication"
echo    → Vérifiez: http://localhost:8761
echo.
pause
echo.
echo 3. User Service (Port 8003)
echo    → Cherchez: user-service/src/.../UserServiceApplication.java
echo    → Clic droit → Run 'UserServiceApplication'
echo.
echo 4. Vehicle Service (Port 8001)
echo    → Cherchez: vehicle-service/src/.../VehicleServiceApplication.java
echo    → Clic droit → Run 'VehicleServiceApplication'
echo.
echo 5. Reservation Service (Port 8002)
echo    → Cherchez: reservation-service/src/.../ReservationServiceApplication.java
echo    → Clic droit → Run 'ReservationServiceApplication'
echo.
echo 6. Rental Service (Port 8004)
echo    → Cherchez: rental-service/src/.../RentalServiceApplication.java
echo    → Clic droit → Run 'RentalServiceApplication'
echo.
pause
echo.
echo 7. API Gateway (Port 8080)
echo    → Cherchez: api-gateway/src/.../ApiGatewayApplication.java
echo    → Clic droit → Run 'ApiGatewayApplication'
echo.
pause
echo.
echo ════════════════════════════════════════════════════════
echo ÉTAPE 3: Démarrer le Frontend
echo ════════════════════════════════════════════════════════
echo.
echo Dans un terminal (cmd ou PowerShell):
echo.
echo   cd frontend
echo   npm install
echo   npm run dev
echo.
echo Démarrer le frontend maintenant? (O/N)
set /p startFrontend=

if /i "%startFrontend%"=="O" (
    echo.
    echo Démarrage du frontend...
    start "Frontend React" cmd /k "cd frontend && npm install && npm run dev"
)

echo.
echo ════════════════════════════════════════════════════════
echo ÉTAPE 4: Vérification
echo ════════════════════════════════════════════════════════
echo.
echo Ouvrez ces URLs pour vérifier:
echo.
echo   - Eureka Dashboard:     http://localhost:8761
echo   - User Service Swagger: http://localhost:8003/swagger-ui.html
echo   - Frontend React:       http://localhost:3000
echo.
echo Voulez-vous ouvrir Eureka Dashboard? (O/N)
set /p openEureka=

if /i "%openEureka%"=="O" (
    start http://localhost:8761
)

echo.
echo ════════════════════════════════════════════════════════
echo Guide terminé! Bonne utilisation de GDLDV!
echo ════════════════════════════════════════════════════════
echo.
pause
