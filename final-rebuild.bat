@echo off
echo ========================================
echo FINAL REBUILD - Spring Security OAuth2 JWT
echo ========================================
echo.

echo Cleaning old JWT dependencies from Maven cache...
rmdir /s /q "%USERPROFILE%\.m2\repository\io\jsonwebtoken" 2>nul
echo Done.

echo.
echo ========================================
echo Building user-service...
echo ========================================
cd user-service
call mvn clean install -DskipTests -U
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ ERROR: user-service build FAILED!
    echo.
    echo Please check the error above and fix it.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Building api-gateway...
echo ========================================
cd ..\api-gateway
call mvn clean install -DskipTests -U
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ ERROR: api-gateway build FAILED!
    echo.
    echo Please check the error above and fix it.
    pause
    exit /b 1
)

cd ..

echo.
echo ========================================
echo ✅ BUILD SUCCESS!
echo ========================================
echo.
echo All services have been rebuilt with Spring Security OAuth2 JWT!
echo.
echo Next steps:
echo 1. Close the "User Service" CMD window
echo 2. Close the "API Gateway" CMD window
echo 3. Start User Service:
echo    cd user-service ^&^& mvn spring-boot:run
echo 4. Wait 20-30 seconds
echo 5. Start API Gateway:
echo    cd api-gateway ^&^& mvn spring-boot:run
echo 6. Wait 20-30 seconds
echo 7. Try logging in with admin@example.com
echo.
pause
