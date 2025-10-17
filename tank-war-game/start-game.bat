@echo off
cls
echo Tank War Game Launcher - Mobile Hotspot LAN Version
echo ================================

rem Check and kill processes occupying ports 8080 and 8000
echo Checking port usage...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr "8080"') do taskkill /F /PID %%a >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr "8000"') do taskkill /F /PID %%a >nul 2>&1
echo Port cleanup completed

echo Checking Java environment...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java environment not found. Please install Java 11 or higher
    pause
    exit /b 1
)

echo Java environment check passed

echo Checking Maven environment...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Maven environment not found. Please install Maven 3.6 or higher
    pause
    exit /b 1
)

echo Maven environment check passed

rem Start backend service
echo Starting backend server...
for /f "tokens=2 delims=: " %%a in ('ipconfig ^| findstr "IPv4 Address" ^| findstr "192.168"') do set HOTSPOT_IP=%%a
start "TankWar-Backend" cmd /k "cd backend && echo Cleaning and compiling project... && mvn clean compile && echo Server address: http://%HOTSPOT_IP%:8080 && echo WebSocket address: ws://%HOTSPOT_IP%:8080/tank-war && echo H2 Console: http://localhost:8080/h2-console && echo. && echo Press Ctrl+C to stop server && echo ================================ && mvn spring-boot:run"

rem Wait for backend service to start
echo Waiting for backend service to start...
ping -n 5 127.0.0.1 >nul

rem Start frontend service
echo Starting frontend service...
start "TankWar-Frontend" cmd /k "cd frontend && echo Frontend service started at http://localhost:8000 && echo Game connection address: ws://%HOTSPOT_IP%:8080/tank-war && echo. && echo Press Ctrl+C to stop frontend service && echo ================================ && python -m http.server 8000"

rem Wait for frontend service to start
echo Waiting for frontend service to start...
ping -n 3 127.0.0.1 >nul

rem Open game page
echo Opening game page...
start http://localhost:8000
echo Game started!
echo Other players please connect to hotspot and visit: http://%HOTSPOT_IP%:8000
echo ================================
echo. 
echo Note: To close the game, please close all command windows
echo. 
pause

