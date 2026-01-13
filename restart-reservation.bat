@echo off
REM ========================================
REM Script de redémarrage Reservation Service
REM ========================================

echo.
echo ╔════════════════════════════════════════════╗
echo ║  Redémarrage du Reservation Service        ║
echo ╚════════════════════════════════════════════╝
echo.

REM Arrêter le processus Java utilisant le port 8002
echo Arrêt du service en cours sur le port 8002...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8002" ^| findstr "LISTENING"') do (
    echo Arrêt du processus %%a
    taskkill /F /PID %%a 2>nul
)

echo Attente 5 secondes...
timeout /t 5 /nobreak >nul

echo.
echo Démarrage du Reservation Service...
cd reservation-service
start "Reservation Service" cmd /k "mvn spring-boot:run"

echo.
echo ╔════════════════════════════════════════════╗
echo ║  Redémarrage terminé !                     ║
echo ╚════════════════════════════════════════════╝
echo.
echo Le Reservation Service démarre sur le port 8002
echo Vérifiez la fenêtre "Reservation Service" pour les logs
echo.
pause
