#!/bin/bash

echo "========================================"
echo "  Royal Demons - RELEASE BUILD SCRIPT"
echo "========================================"
echo

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Функция для вывода сообщений
print_step() {
    echo -e "${YELLOW}[STEP]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Проверка Java
print_step "Checking Java installation..."
if ! command -v java &> /dev/null; then
    print_error "Java not found! Please install Java 21+"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    print_error "Java version must be 21 or higher! Current: $(java -version 2>&1 | head -n 1)"
    exit 1
fi

print_success "Java $(java -version 2>&1 | head -n 1) detected"

# Очистка предыдущей сборки
print_step "Cleaning previous build..."
./gradlew clean
if [ $? -ne 0 ]; then
    print_error "Clean failed!"
    exit 1
fi

# Проверка стиля кода
print_step "Running code style check (Checkstyle)..."
./gradlew checkstyleMain checkstyleTest
if [ $? -ne 0 ]; then
    print_error "Code style check failed!"
    exit 1
fi
print_success "Code style check passed"

# Статический анализ
print_step "Running static analysis (SpotBugs)..."
./gradlew spotbugsMain spotbugsTest
if [ $? -ne 0 ]; then
    print_error "Static analysis failed!"
    exit 1
fi
print_success "Static analysis passed"

# Компиляция
print_step "Compiling Java sources..."
./gradlew compileJava
if [ $? -ne 0 ]; then
    print_error "Compilation failed!"
    exit 1
fi
print_success "Compilation successful"

# Запуск тестов
print_step "Running unit tests..."
./gradlew test --info
if [ $? -ne 0 ]; then
    print_error "Tests failed!"
    exit 1
fi
print_success "All tests passed"

# Полная сборка
print_step "Building release JAR..."
./gradlew build
if [ $? -ne 0 ]; then
    print_error "Build failed!"
    exit 1
fi
print_success "Release build completed"

# Создание JAR
print_step "Creating standalone JAR..."
./gradlew jar
if [ $? -ne 0 ]; then
    print_error "JAR creation failed!"
    exit 1
fi
print_success "Standalone JAR created"

# Проверка размера JAR
JAR_SIZE=$(du -h build/libs/royal-demons.jar | cut -f1)
print_success "JAR created: build/libs/royal-demons.jar ($JAR_SIZE)"

# Проверка манифеста
print_step "Verifying JAR manifest..."
if unzip -l build/libs/royal-demons.jar | grep -q "META-INF/MANIFEST.MF"; then
    print_success "JAR manifest verified"
else
    print_error "JAR manifest missing!"
    exit 1
fi

# Тестовый запуск (headless)
print_step "Testing application launch..."
timeout 10s ./gradlew run -x test 2>&1 > /tmp/launch_test.log &
LAUNCH_PID=$!

sleep 3

if kill -0 $LAUNCH_PID 2>/dev/null; then
    print_success "Application launched successfully (PID: $LAUNCH_PID)"
    kill $LAUNCH_PID 2>/dev/null
    wait $LAUNCH_PID 2>/dev/null
else
    print_error "Application failed to launch!"
    echo "Check /tmp/launch_test.log for details"
    cat /tmp/launch_test.log
    exit 1
fi

# Создание релизного архива
print_step "Creating release archive..."
RELEASE_NAME="royal-demons-$(date +%Y%m%d-%H%M%S)"
mkdir -p "releases/$RELEASE_NAME"

cp build/libs/royal-demons.jar "releases/$RELEASE_NAME/"
cp -r src/main/resources "releases/$RELEASE_NAME/resources"
cp build-windows.bat build-linux.sh run-windows.bat run-linux.sh "releases/$RELEASE_NAME/"
cp CROSSPLATFORM_README.md ARCHITECTURE.md FOUNDATION_REPORT.md "releases/$RELEASE_NAME/"

cd releases
tar -czf "${RELEASE_NAME}.tar.gz" "$RELEASE_NAME"
zip -r "${RELEASE_NAME}.zip" "$RELEASE_NAME" > /dev/null
cd ..

RELEASE_SIZE=$(du -h "releases/${RELEASE_NAME}.tar.gz" | cut -f1)
print_success "Release archive created: releases/${RELEASE_NAME}.tar.gz ($RELEASE_SIZE)"

# Отчет о сборке
echo
echo "========================================"
echo "  RELEASE BUILD COMPLETED SUCCESSFULLY"
echo "========================================"
echo
echo "Artifacts created:"
echo "  - build/libs/royal-demons.jar (standalone)"
echo "  - releases/${RELEASE_NAME}.tar.gz"
echo "  - releases/${RELEASE_NAME}.zip"
echo
echo "Build information:"
echo "  - Java version: $(java -version 2>&1 | head -n 1)"
echo "  - Gradle version: $(./gradlew --version | grep Gradle | head -n 1)"
echo "  - Build time: $(date)"
echo
echo "To run the game:"
echo "  Linux:   ./run-linux.sh"
echo "  Windows: run-windows.bat"
echo "  Manual:  java -jar build/libs/royal-demons.jar"
echo

read -p "Press Enter to continue..."
