# 🎮 КРАТКОЕ РУКОВОДСТВО: СОХРАНЕНИЯ В ROYAL DEMONS

---

## 📌 ОСНОВЫ

### **Где сохраняются игры?**
```
Linux:   ~/.royal-demons/saves/
Windows: %USERPROFILE%\.royal-demons\saves\
macOS:   ~/.royal-demons/saves/
```

### **Как часто сохраняется?**
- ✅ **Автоматически:** каждые 5 минут
- ✅ **При выходе:** всегда сохраняется
- ✅ **Вручную:** можно сохраниться в любой момент

---

## 🎯 ПРАКТИЧЕСКОЕ ИСПОЛЬЗОВАНИЕ

### **1. Автосохранение (автоматически)**

```java
// Включено по умолчанию в GameConfig
boolean autoSave = true; // В config/game.json

// Автосохранение создает файл: autosave.json
```

### **2. Ручное сохранение**

```java
// Сохранение в указанный файл
GameSaveService.getInstance().saveGameAsync("my_save.json");

// Сохранение с проверкой результата
CompletableFuture<Boolean> result = GameSaveService.getInstance()
    .saveGameAsync("checkpoint_before_boss.json");

result.thenAccept(success -> {
    if (success) {
        System.out.println("✅ Игра сохранена!");
    } else {
        System.out.println("❌ Ошибка сохранения!");
    }
});
```

### **3. Загрузка игры**

```java
try {
    // Загрузка из файла
    GameSaveData data = GameSaveManager.getInstance()
        .loadGame("my_save.json");

    // Восстановление состояния
    restoreGameState(data);

    System.out.println("Игра загружена успешно!");
} catch (Exception e) {
    System.err.println("Ошибка загрузки: " + e.getMessage());
    startNewGame(); // Запускаем новую игру
}
```

---

## 📊 ЧТО СОХРАНЯЕТСЯ

### **✅ Сохраняется:**
- 👤 Уровень игрока, опыт, здоровье
- 💰 Золото и предметы
- 🗺️ Текущая комната и позиция
- 🗝️ Посещенные комнаты
- ⚔️ Разблокированное оружие
- 🏆 Достижения
- 🧪 Зелья и инвентарь

### **❌ НЕ сохраняется:**
- Текущее состояние врагов (генерируются заново)
- Временные эффекты (баффы/дебаффы)
- Кэш текстур и спрайтов

---

## 🔧 ПРИМЕРЫ КОДА

### **Создание быстрого сохранения:**
```java
// В обработчике клавиши (например, F5)
@FXML
private void handleQuickSave() {
    String timestamp = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
    String filename = "quicksave_" + timestamp + ".json";

    GameSaveService.getInstance().saveGameAsync(filename)
        .thenRun(() -> showMessage("Сохранено: " + filename));
}
```

### **Загрузка последнего автосохранения:**
```java
// При запуске игры
public void onGameStart() {
    File autoSave = new File(SAVE_DIR, "autosave.json");

    if (autoSave.exists()) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Загрузка игры");
        alert.setHeaderText("Найдено автосохранение");
        alert.setContentText("Загрузить последнее сохранение?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            loadGame("autosave.json");
        }
    } else {
        startNewGame();
    }
}
```

### **Получение списка сохранений:**
```java
// Для меню загрузки
SaveSlotInfo[] saves = GameSaveManager.getInstance().getAvailableSaves();

for (int i = 0; i < saves.length; i++) {
    SaveSlotInfo save = saves[i];
    System.out.println((i+1) + ". " + save.getFilename());
    System.out.println("   Уровень: " + save.getPlayerLevel());
    System.out.println("   Время: " + save.getSaveTime());
    System.out.println("   Игра: " + formatTime(save.getPlayTime()));
}
```

---

## 🚨 ВАЖНЫЕ МОМЕНТЫ

### **1. Асинхронность**
```java
// ✅ ПРАВИЛЬНО - не блокирует игру
GameSaveService.getInstance().saveGameAsync("save.json");

// ❌ НЕПРАВИЛЬНО - может зависнуть игру
GameSaveManager.getInstance().saveGame(saveData); // Синхронно!
```

### **2. Обработка ошибок**
```java
CompletableFuture<Boolean> result = saveGameAsync("save.json");

result.exceptionally(throwable -> {
    GameLogger.error("Save failed", throwable);
    return false; // Возвращаем false при ошибке
});
```

### **3. Валидация данных**
```java
// Проверяем валидность при загрузке
GameSaveData data = loadGame("save.json");

if (data.getPlayerHealth() <= 0) {
    GameLogger.warn("Invalid health value, starting new game");
    startNewGame();
    return;
}
```

---

## 📁 СТРУКТУРА ФАЙЛОВ

### **Стандартное сохранение (JSON):**
```json
{
  "version": "1.0",
  "saveTime": "2025-11-05T22:00:00",
  "playTime": 3600,
  "playerLevel": 5,
  "playerHealth": 85,
  "playerMaxHealth": 100,
  "gold": 1500,
  "currentRoomId": "room_1_2",
  "inventory": {
    "currentWeapon": "GoldenSword2",
    "potions": {"health": 3, "rage": 1}
  }
}
```

### **Именование файлов:**
- `autosave.json` - автосохранение
- `quicksave.json` - быстрое сохранение
- `save_2025-11-05.json` - именованное сохранение
- `checkpoint_boss.json` - сохранение перед боссом

---

## 🎛️ НАСТРОЙКА

### **Включение/отключение автосохранения:**
```json
// config/game.json
{
  "autoSave": true,
  "autoSaveInterval": 300  // секунды (5 минут)
}
```

### **Отключение автосохранения:**
```java
GameSaveService service = GameSaveService.getInstance();
service.disableAutoSave(); // Остановить автосохранение

// или в конфиге:
autoSave: false
```

---

## 🆘 УСТРАНЕНИЕ ПРОБЛЕМ

### **Проблема: "Save already in progress"**
```java
// Подождите завершения предыдущего сохранения
boolean saved = GameSaveService.getInstance().saveGameAsync("save.json").join();
```

### **Проблема: "Save file corrupted"**
```java
try {
    loadGame("save.json");
} catch (Exception e) {
    // Создать новую игру
    createNewGame();

    // Опционально: удалить поврежденный файл
    GameSaveManager.getInstance().deleteSave("save.json");
}
```

### **Проблема: "No space left on device"**
```java
// Автосохранение пропустится, игра продолжится
GameLogger.warn("Auto-save failed: disk full");

// Очистите место на диске вручную
```

---

## ✅ ЧЕКЛИСТ ДЛЯ РАЗРАБОТЧИКОВ

- [ ] Используйте `GameSaveService` для всех операций
- [ ] Никогда не вызывайте `GameSaveManager` напрямую
- [ ] Всегда обрабатывайте `CompletableFuture`
- [ ] Логируйте важные события сохранения
- [ ] Валидируйте данные при загрузке
- [ ] Тестируйте восстановление после сбоев
- [ ] Создавайте бэкапы перед важными изменениями

---

## 🎉 ЗАКЛЮЧЕНИЕ

**Система сохранений Royal Demons проста в использовании:**

1. ✅ **Автосохранение** работает автоматически
2. ✅ **Ручное сохранение** - одна строка кода
3. ✅ **Загрузка** - безопасная с валидацией
4. ✅ **Thread-safe** - не блокирует игру

**Просто используйте `GameSaveService.getInstance().saveGameAsync()` и наслаждайтесь! 🎮**
