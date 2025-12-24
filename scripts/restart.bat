@echo off
REM ============================================
REM Restart GDLDV Microservices
REM ============================================

echo ========================================
echo Restarting GDLDV Microservices
echo ========================================
echo.

if "%1"=="" (
    echo [INFO] Restarting all services...
    docker-compose -f docker-compose-full.yml restart
) else (
    echo [INFO] Restarting %1...
    docker-compose -f docker-compose-full.yml restart %1
)

echo.
echo [OK] Restart completed
pause
