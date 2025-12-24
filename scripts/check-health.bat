@echo off
REM ============================================
REM Check Health of GDLDV Microservices
REM ============================================

echo ========================================
echo GDLDV Microservices Health Check
echo ========================================
echo.

echo [1/7] Eureka Server (8761)...
curl -s http://localhost:8761/actuator/health
echo.
echo.

echo [2/7] Config Server (8888)...
curl -s http://localhost:8888/actuator/health
echo.
echo.

echo [3/7] API Gateway (8000)...
curl -s http://localhost:8000/actuator/health
echo.
echo.

echo [4/7] Vehicle Service (8001)...
curl -s http://localhost:8001/actuator/health
echo.
echo.

echo [5/7] Reservation Service (8002)...
curl -s http://localhost:8002/actuator/health
echo.
echo.

echo [6/7] User Service (8003)...
curl -s http://localhost:8003/actuator/health
echo.
echo.

echo [7/7] Rental Service (8004)...
curl -s http://localhost:8004/actuator/health
echo.
echo.

echo ========================================
echo Health Check Complete
echo ========================================
pause
