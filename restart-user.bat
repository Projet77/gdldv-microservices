@echo off
echo ============================================
echo Redemarrage du User Service (Port 8003)
echo ============================================

echo.
echo [1/3] Arret du service existant...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8003') do (
    echo Processus trouve sur port 8003: %%a
    taskkill /F /PID %%a
)

echo.
echo [2/3] Attente de liberation du port...
timeout /t 3 /nobreak > nul

echo.
echo [3/3] Demarrage du User Service...
cd user-service
start "User Service" cmd /k "mvn spring-boot:run"

echo.
echo ============================================
echo User Service demarre!
echo ============================================
echo Port: 8003
echo Logs: Voir la fenetre "User Service"
echo ============================================
