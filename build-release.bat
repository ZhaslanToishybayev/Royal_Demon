@echo off
echo ========================================
echo   Royal Demons - RELEASE BUILD SCRIPT
echo ========================================
echo.

:: Проверка Java
echo [STEP] Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java not found! Please install Java 21+
    pause
    exit /b 1
)

echo [SUCCESS] Java detected

:: Очистка предыдущей сборки
echo [STEP] Cleaning previous build...
call gradlew.bat clean
if %errorlevel% neq 0 (
    echo [ERROR] Clean failed!
    pause
    exit /b 1
)

:: Проверка стиля кода
echo [STEP] Running code style check...
call gradlew.bat checkstyleMain checkstyleTest
if %errorlevel% neq 0 (
    echo [ERROR] Code style check failed!
    pause
    exit /b 1
)
echo [SUCCESS] Code style check passed

:: Статический анализ
echo [STEP] Running static analysis...
call gradlew.bat spotbugsMain spotbugsTest
if %errorlevel% neq 0 (
    echo [ERROR] Static analysis failed!
    pause
    exit /b 1
)
echo [SUCCESS] Static analysis passed

:: Компиляция
echo [STEP] Compiling Java sources...
call gradlew.bat compileJava
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)
echo [SUCCESS] Compilation successful

:: Запуск тестов
echo [STEP] Running unit tests...
call gradlew.bat test
if %errorlevel% neq 0 (
    echo [ERROR] Tests failed!
    pause
    exit /b 1
)
echo [SUCCESS] All tests passed

:: Полная сборка
echo [STEP] Building release JAR...
call gradlew.bat build
if %errorlevel% neq 0 (
    echo [ERROR] Build failed!
    pause
    exit /b 1
)
echo [SUCCESS] Release build completed

:: Создание JAR
echo [STEP] Creating standalone JAR...
call gradlew.bat jar
if %errorlevel% neq 0 (
    echo [ERROR] JAR creation failed!
    pause
    exit /b 1
)

:: Проверка манифеста
echo [STEP] Verifying JAR manifest...
for /f %%i in ('dir build\libs\royal-demons.jar /b 2^>nul') do set JAR_FILE=%%i
if defined JAR_FILE (
    echo [SUCCESS] JAR created: build\libs\%JAR_FILE%
) else (
    echo [ERROR] JAR creation failed!
    pause
    exit /b 1
)

:: Создание релизного архива
echo [STEP] Creating release archive...
set RELEASE_NAME=royal-demons-%date:~-4,4%%date:~-10,2%%date:~-7,2%-%time:~0,2%%time:~3,2%
set RELEASE_NAME=%RELEASE_NAME: =0%
mkdir releases\%RELEASE_NAME% 2>nul

copy build\libs\royal-demons.jar releases\%RELEASE_NAME%\ >nul
xcopy /E /I /Y src\main\resources releases\%RELEASE_NAME%\resources >nul
copy build-windows.bat releases\%RELEASE_NAME%\ >nul 2>&1
copy build-linux.sh releases\%RELEASE_NAME%\ >nul 2>&1
copy run-windows.bat releases\%RELEASE_NAME%\ >nul 2>&1
copy run-linux.sh releases\%RELEASE_NAME%\ >nul 2>&1
copy CROSSPLATFORM_README.md releases\%RELEASE_NAME%\ >nul 2>&1
copy ARCHITECTURE.md releases\%RELEASE_NAME%\ >nul 2>&1
copy FOUNDATION_REPORT.md releases\%RELEASE_NAME%\ >nul 2>&1

powershell -command "Compress-Archive -Path 'releases\%RELEASE_NAME%' -DestinationPath 'releases\%RELEASE_NAME%.zip'" >nul 2>&1

echo [SUCCESS] Release archive created: releases\%RELEASE_NAME%.zip

:: Отчет о сборке
echo.
echo ========================================
echo   RELEASE BUILD COMPLETED SUCCESSFULLY
echo ========================================
echo.
echo Artifacts created:
echo   - build\libs\royal-demons.jar (standalone)
echo   - releases\%RELEASE_NAME%.zip
echo.
echo Build information:
java -version 2>&1 | findstr /C:"version" | findstr /V "java version"
echo.
echo To run the game:
echo   Windows: run-windows.bat
echo   Manual:  java -jar build\libs\royal-demons.jar
echo.

pause
