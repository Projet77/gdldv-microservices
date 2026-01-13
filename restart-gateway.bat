@echo off
echo ============================================
echo Redemarrage de l'API Gateway (Port 8000)
echo ============================================

echo.
echo [1/3] Arret du service existant...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8000') do (
    echo Processus trouve sur port 8000: %%a
    taskkill /F /PID %%a
)

echo.
echo [2/3] Attente de liberation du port...
timeout /t 3 /nobreak > nul

echo.
echo [3/3] Demarrage de l'API Gateway...
cd api-gateway
start "API Gateway" cmd /k "mvn spring-boot:run"

echo.
echo ============================================
echo API Gateway demarre!
echo ============================================
echo Port: 8000
echo Logs: Voir la fenetre "API Gateway"
echo ============================================
