#!/bin/bash

echo "========================================"
echo "  Royal Demons - Linux Launcher"
echo "========================================"
echo

# Проверяем наличие собранного JAR
if [ ! -f "build/libs/royal-demons.jar" ]; then
    echo "JAR not found! Building first..."
    ./build-linux.sh
    if [ $? -ne 0 ]; then
        echo "ERROR: Build failed!"
        exit 1
    fi
fi

echo "Starting Royal Demons..."
echo

# Запускаем игру
java -jar build/libs/royal-demons.jar

if [ $? -ne 0 ]; then
    echo
    echo "ERROR: Game crashed!"
    echo "Check logs at: $(xdg-user-dir DOCUMENTS 2>/dev/null || echo ~)/RoyalDemons/logs/"
    read -p "Press Enter to continue..."
    exit 1
fi

echo
echo "Game exited normally."
read -p "Press Enter to continue..."
