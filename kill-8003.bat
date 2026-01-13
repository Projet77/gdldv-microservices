@echo off
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8003 ^| findstr LISTENING') do (
    echo Killing process %%a on port 8003
    taskkill /F /PID %%a
)
