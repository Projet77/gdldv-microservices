@echo off
echo Killing process on port 8003...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8003') do (
    echo Killing PID: %%a
    taskkill /F /PID %%a
)
echo Done!
