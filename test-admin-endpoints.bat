@echo off
echo ========================================
echo Test des Endpoints Admin et Super Admin
echo ========================================
echo.

echo Recuperation du token JWT...
echo.
echo Veuillez vous connecter avec vos identifiants Admin:
set /p username="Username: "
set /p password="Password: "

echo.
echo Authentification en cours...
curl -X POST http://localhost:8000/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"%username%\",\"password\":\"%password%\"}" ^
  -o token-response.json

echo.
echo.
echo ========================================
echo Test 1: Admin Dashboard
echo ========================================
curl -X GET http://localhost:8000/api/admin/dashboard ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE" ^
  -H "Content-Type: application/json"

echo.
echo.
echo ========================================
echo Test 2: Super Admin Dashboard
echo ========================================
curl -X GET http://localhost:8000/api/super-admin/dashboard ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE" ^
  -H "Content-Type: application/json"

echo.
echo.
echo ========================================
echo Test 3: System Health
echo ========================================
curl -X GET http://localhost:8000/api/super-admin/system-health ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE" ^
  -H "Content-Type: application/json"

echo.
echo.
pause
