@echo off
echo ========================================
echo FORCE REBUILD - Clearing Maven cache
echo ========================================
echo.

echo Step 1: Deleting old JJWT artifacts from Maven cache...
rmdir /s /q "%USERPROFILE%\.m2\repository\io\jsonwebtoken" 2>nul
echo Done.

echo.
echo Step 2: Cleaning user-service...
cd user-service
call mvn clean
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: user-service clean failed!
    exit /b 1
)

echo.
echo Step 3: Installing user-service with fresh dependencies...
call mvn install -DskipTests -U
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: user-service build failed!
    exit /b 1
)

echo.
echo Step 4: Cleaning api-gateway...
cd ..\api-gateway
call mvn clean
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: api-gateway clean failed!
    exit /b 1
)

echo.
echo Step 5: Installing api-gateway with fresh dependencies...
call mvn install -DskipTests -U
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: api-gateway build failed!
    exit /b 1
)

cd ..

echo.
echo ========================================
echo BUILD SUCCESS!
echo ========================================
echo.
echo Next steps:
echo 1. Close User Service and API Gateway CMD windows
echo 2. Start User Service: cd user-service ^&^& mvn spring-boot:run
echo 3. Start API Gateway: cd api-gateway ^&^& mvn spring-boot:run
echo.
