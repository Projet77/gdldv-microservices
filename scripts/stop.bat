@echo off
REM ============================================
REM Stop GDLDV Microservices
REM ============================================

echo ========================================
echo Stopping GDLDV Microservices
echo ========================================
echo.

docker-compose -f docker-compose-full.yml down

echo.
echo [OK] All services stopped
pause
