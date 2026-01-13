@echo off
echo Cleaning and rebuilding user-service...
cd user-service
call mvn clean install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: user-service build failed!
    exit /b 1
)

echo.
echo Cleaning and rebuilding api-gateway...
cd ..\api-gateway
call mvn clean install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: api-gateway build failed!
    exit /b 1
)

echo.
echo ========================================
echo BUILD SUCCESS!
echo ========================================
echo.
echo You can now restart the services:
echo 1. Close User Service and API Gateway windows
echo 2. Run: cd user-service ^&^& mvn spring-boot:run
echo 3. Run: cd api-gateway ^&^& mvn spring-boot:run
echo.
