#!/bin/bash

echo "========================================"
echo "  Royal Demons - Linux Build Script"
echo "========================================"
echo

# Проверяем наличие Java
if ! command -v java &> /dev/null; then
    echo "ERROR: Java not found! Please install Java 21+"
    echo "  Ubuntu/Debian: sudo apt install openjdk-21-jdk"
    echo "  Fedora/RHEL:   sudo dnf install java-21-openjdk-devel"
    exit 1
fi

echo "[1/4] Cleaning previous build..."
./gradlew clean

echo
echo "[2/4] Building project..."
./gradlew build -x test

if [ $? -ne 0 ]; then
    echo "ERROR: Build failed!"
    exit 1
fi

echo
echo "[3/4] Creating JAR..."
./gradlew jar

if [ $? -ne 0 ]; then
    echo "ERROR: JAR creation failed!"
    exit 1
fi

echo
echo "[4/4] Testing launch..."
./gradlew run -x checkstyleMain -x checkstyleTest -x test -x spotbugsMain

if [ $? -ne 0 ]; then
    echo "WARNING: Launch test failed, but build was successful"
else
    echo "SUCCESS: Build completed successfully!"
fi

echo
echo "Build artifacts:"
echo "  - build/libs/royal-demons.jar"
echo
echo "To run the game later, use:"
echo "  ./run-linux.sh"
echo

read -p "Press Enter to continue..."
