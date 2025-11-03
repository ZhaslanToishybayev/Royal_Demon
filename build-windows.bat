@echo off
echo ========================================
echo   Royal Demons - Windows Build Script
echo ========================================
echo.

REM Проверяем наличие Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java not found! Please install Java 21+
    pause
    exit /b 1
)

echo [1/4] Cleaning previous build...
call gradlew.bat clean

echo.
echo [2/4] Building project...
call gradlew.bat build -x test

if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [3/4] Creating Windows JAR...
call gradlew.bat jar

if %errorlevel% neq 0 (
    echo ERROR: JAR creation failed!
    pause
    exit /b 1
)

echo.
echo [4/4] Testing launch...
call gradlew.bat run -x checkstyleMain -x checkstyleTest -x test -x spotbugsMain

if %errorlevel% neq 0 (
    echo WARNING: Launch test failed, but build was successful
) else (
    echo SUCCESS: Build completed successfully!
)

echo.
echo Build artifacts:
echo   - build/libs/royal-demons.jar
echo.
echo To run the game later, use:
echo   run-windows.bat
echo.

pause
