@echo off
echo ========================================
echo   Royal Demons - Windows Launcher
echo ========================================
echo.

REM Проверяем наличие собранного JAR
if not exist "build\libs\royal-demons.jar" (
    echo JAR not found! Building first...
    call build-windows.bat
    if %errorlevel% neq 0 (
        echo ERROR: Build failed!
        pause
        exit /b 1
    )
)

echo Starting Royal Demons...
echo.

REM Запускаем игру
java -jar build\libs\royal-demons.jar

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Game crashed!
    echo Check logs at: %USERPROFILE%\Documents\RoyalDemons\logs\
    pause
    exit /b 1
)

echo.
echo Game exited normally.
pause
