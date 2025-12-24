@echo off
REM ========================================
REM Script d'arrêt GDLDV
REM Arrête tous les services Java
REM ========================================

echo.
echo ╔════════════════════════════════════════════╗
echo ║  GDLDV - Arrêt des Microservices           ║
echo ╚════════════════════════════════════════════╝
echo.

echo Recherche des processus Java en cours...
echo.

REM Arrêter tous les processus Maven/Spring Boot
for /f "tokens=2" %%i in ('tasklist ^| findstr "java.exe"') do (
    echo Arrêt du processus %%i...
    taskkill /PID %%i /F >nul 2>&1
)

REM Arrêter Node.js (Frontend)
for /f "tokens=2" %%i in ('tasklist ^| findstr "node.exe"') do (
    echo Arrêt du processus Node.js %%i...
    taskkill /PID %%i /F >nul 2>&1
)

echo.
echo ╔════════════════════════════════════════════╗
echo ║     Tous les services sont arrêtés !       ║
echo ╚════════════════════════════════════════════╝
echo.

pause
