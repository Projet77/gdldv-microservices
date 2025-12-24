@echo off
REM ============================================
REM View GDLDV Microservices Logs
REM ============================================

if "%1"=="" (
    echo Usage: logs.bat [service-name]
    echo.
    echo Available services:
    echo   - eureka-server
    echo   - config-server
    echo   - api-gateway
    echo   - vehicle-service
    echo   - reservation-service
    echo   - user-service
    echo   - rental-service
    echo   - mysql-vehicle
    echo   - mysql-reservation
    echo   - mysql-user
    echo   - mysql-rental
    echo.
    echo Example: logs.bat vehicle-service
    pause
    exit /b 1
)

docker-compose -f docker-compose-full.yml logs -f %1
