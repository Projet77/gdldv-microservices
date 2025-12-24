@echo off
REM ============================================
REM Backup GDLDV Databases
REM ============================================

echo ========================================
echo GDLDV Database Backup
echo ========================================
echo.

REM Create backup directory if not exists
if not exist backups mkdir backups

REM Generate timestamp
for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value') do set datetime=%%I
set TIMESTAMP=%datetime:~0,8%_%datetime:~8,6%

echo [INFO] Backup timestamp: %TIMESTAMP%
echo.

REM Backup each database
echo [1/4] Backing up Vehicle Database...
docker exec gdldv-mysql-vehicle mysqladmin ping -h localhost -u root -p%MYSQL_ROOT_PASSWORD% >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Vehicle database is not accessible
) else (
    docker exec gdldv-mysql-vehicle mysqldump -u root -p%MYSQL_ROOT_PASSWORD% gdldv_vehicle_db > backups\vehicle_%TIMESTAMP%.sql
    echo [OK] Vehicle database backed up
)
echo.

echo [2/4] Backing up Reservation Database...
docker exec gdldv-mysql-reservation mysqladmin ping -h localhost -u root -p%MYSQL_ROOT_PASSWORD% >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Reservation database is not accessible
) else (
    docker exec gdldv-mysql-reservation mysqldump -u root -p%MYSQL_ROOT_PASSWORD% gdldv_reservation_db > backups\reservation_%TIMESTAMP%.sql
    echo [OK] Reservation database backed up
)
echo.

echo [3/4] Backing up User Database...
docker exec gdldv-mysql-user mysqladmin ping -h localhost -u root -p%MYSQL_ROOT_PASSWORD% >nul 2>&1
if errorlevel 1 (
    echo [ERROR] User database is not accessible
) else (
    docker exec gdldv-mysql-user mysqldump -u root -p%MYSQL_ROOT_PASSWORD% gdldv_user_db > backups\user_%TIMESTAMP%.sql
    echo [OK] User database backed up
)
echo.

echo [4/4] Backing up Rental Database...
docker exec gdldv-mysql-rental mysqladmin ping -h localhost -u root -p%MYSQL_ROOT_PASSWORD% >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Rental database is not accessible
) else (
    docker exec gdldv-mysql-rental mysqldump -u root -p%MYSQL_ROOT_PASSWORD% gdldv_rental_db > backups\rental_%TIMESTAMP%.sql
    echo [OK] Rental database backed up
)
echo.

echo ========================================
echo Backup Location: backups\
echo ========================================
dir backups\*%TIMESTAMP%.sql
echo.
echo [OK] Backup completed
pause
