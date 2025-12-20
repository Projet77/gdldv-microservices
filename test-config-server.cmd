@echo off
echo ====================================
echo  TEST CONFIG SERVER ENDPOINTS
echo ====================================
echo.

echo [1] Testing application/default...
curl -s http://localhost:8888/application/default
echo.
echo.

echo [2] Testing application/dev...
curl -s http://localhost:8888/application/dev
echo.
echo.

echo [3] Testing user-service/dev...
curl -s http://localhost:8888/user-service/dev
echo.
echo.

echo [4] Testing vehicle-service/dev...
curl -s http://localhost:8888/vehicle-service/dev
echo.
echo.

echo [5] Testing api-gateway/dev...
curl -s http://localhost:8888/api-gateway/dev
echo.
echo.

echo [6] Testing reservation-service/dev...
curl -s http://localhost:8888/reservation-service/dev
echo.
echo.

echo [7] Testing rental-service/dev...
curl -s http://localhost:8888/rental-service/dev
echo.
echo.

echo ====================================
echo  TESTS COMPLETED
echo ====================================
pause
