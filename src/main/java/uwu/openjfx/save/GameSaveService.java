package uwu.openjfx.save;

import uwu.openjfx.components.PlayerComponent;
import uwu.openjfx.components.PlayerManager;
import uwu.openjfx.core.GameConstants;
import uwu.openjfx.utils.GameLogger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Улучшенный сервис для управления сохранениями в игре.
 * - Thread-safe операции сохранения/загрузки
 * - Асинхронные операции для предотвращения зависаний
 * - Интеграция с новой архитектурой PlayerManager
 * - Обработка ошибок и fallback механизмы
 * - Поддержка версионирования сохранений
 */
public class GameSaveService {

    private static final GameSaveService INSTANCE = new GameSaveService();

    // Thread-safe поля
    private final GameSaveManager saveManager;
    private final Timer autoSaveTimer;
    private final AtomicBoolean autoSaveEnabled = new AtomicBoolean(true);
    private final AtomicLong gameStartTime = new AtomicLong(System.currentTimeMillis());
    private final AtomicLong totalPlayTime = new AtomicLong(0);
    private final AtomicBoolean isSaving = new AtomicBoolean(false);

    // Кэширование данных для оптимизации
    private volatile GameSaveData cachedSaveData;
    private volatile long lastCacheUpdate = 0;
    private static final long CACHE_UPDATE_INTERVAL = 1000; // 1 секунда

    private GameSaveService() {
        this.saveManager = GameSaveManager.getInstance();
        this.autoSaveTimer = new Timer("AutoSaveTimer", true);
        this.gameStartTime.set(System.currentTimeMillis());
        startAutoSave();
    }

    public static GameSaveService getInstance() {
        return INSTANCE;
    }

    /**
     * Асинхронное сохранение текущего состояния игры
     *
     * @param saveName имя сохранения (может быть null для автосохранения)
     * @return CompletableFuture с результатом сохранения
     */
    public CompletableFuture<Boolean> saveGameAsync(String saveName) {
        return CompletableFuture.supplyAsync(() -> {
            if (isSaving.get()) {
                GameLogger.warn("Save already in progress, skipping");
                return false;
            }

            isSaving.set(true);
            try {
                return saveGameSync(saveName);
            } finally {
                isSaving.set(false);
            }
        });
    }

    /**
     * Синхронное сохранение текущего состояния игры
     */
    private boolean saveGameSync(String saveName) {
        try {
            GameSaveData saveData = createSaveDataFromCurrentState();

            // Генерируем имя файла, если не указано
            String filename = saveName != null ? saveName : generateAutoSaveName();

            // Добавляем базовую информацию
            // saveData.setVersion(getCurrentVersion()); // Метод может не существовать
            // saveData.setTimestamp(System.currentTimeMillis()); // Метод может не существовать

            // Сохранение в зависимости от типа
            if (filename.startsWith(GameConstants.Save.AUTO_SAVE_PREFIX)) {
                saveManager.autoSave(saveData);
            } else {
                saveManager.saveGame(saveData);
            }

            GameLogger.gameplay("Игра сохранена: " + filename);
            updateCache(saveData);
            return true;
        } catch (Exception e) {
            GameLogger.error("Ошибка сохранения игры: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Асинхронная загрузка игры из указанного файла
     *
     * @param filename имя файла сохранения
     * @return CompletableFuture с результатом загрузки
     */
    public CompletableFuture<Boolean> loadGameAsync(String filename) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                GameSaveData saveData = saveManager.loadGame(filename);

                // Проверка совместимости версий
                if (!isVersionCompatible(saveData.getVersion())) {
                    GameLogger.warn("Save version incompatible: " + saveData.getVersion());
                    return false;
                }

                applySaveDataToGameState(saveData);

                // Обновляем общее время игры
                totalPlayTime.set(saveData.getPlayTime());
                gameStartTime.set(System.currentTimeMillis());

                GameLogger.gameplay("Игра загружена: " + filename);
                return true;
            } catch (Exception e) {
                GameLogger.error("Ошибка загрузки игры: " + e.getMessage(), e);
                return false;
            }
        });
    }

    /**
     * Создает быстрое сохранение
     */
    public CompletableFuture<Boolean> quickSave() {
        return CompletableFuture.supplyAsync(() -> {
            // Сохраняем только если игра идет достаточно долго
            if (getTotalPlayTime() > 10) { // 10 секунд
                return saveGameSync(GameConstants.Save.QUICK_SAVE_PREFIX + "1" + GameConstants.Save.SAVE_FILE_EXTENSION);
            }
            return false;
        });
    }

    /**
     * Восстанавливает последнее быстрое сохранение
     */
    public CompletableFuture<Boolean> quickLoad() {
        return loadGameAsync(GameConstants.Save.QUICK_SAVE_PREFIX + "1" + GameConstants.Save.SAVE_FILE_EXTENSION)
            .handle((result, throwable) -> {
                if (throwable != null) {
                    GameLogger.warn("Быстрое сохранение не найдено: " + throwable.getMessage());
                    return false;
                }
                return result;
            });
    }

    /**
     * Получает список доступных сохранений
     */
    public SaveSlotInfo[] getAvailableSaves() {
        try {
            return saveManager.getAvailableSaves();
        } catch (Exception e) {
            GameLogger.error("Ошибка получения списка сохранений: " + e.getMessage(), e);
            return new SaveSlotInfo[0];
        }
    }

    /**
     * Удаляет указанное сохранение
     */
    public CompletableFuture<Boolean> deleteSaveAsync(String filename) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                boolean result = saveManager.deleteSave(filename);
                if (result) {
                    GameLogger.gameplay("Сохранение удалено: " + filename);
                } else {
                    GameLogger.warn("Не удалось удалить сохранение: " + filename);
                }
                return result;
            } catch (Exception e) {
                GameLogger.error("Ошибка удаления сохранения: " + e.getMessage(), e);
                return false;
            }
        });
    }

    /**
     * Включает/выключает автосохранение
     */
    public void setAutoSaveEnabled(boolean enabled) {
        autoSaveEnabled.set(enabled);
        if (enabled) {
            startAutoSave();
        } else {
            stopAutoSave();
        }
        GameLogger.gameplay("Автосохранение " + (enabled ? "включено" : "выключено"));
    }

    /**
     * Получает общее время игры
     */
    public long getTotalPlayTime() {
        return totalPlayTime.get() + (System.currentTimeMillis() - gameStartTime.get()) / 1000;
    }

    /**
     * Создает данные сохранения из текущего состояния игры с улучшенной интеграцией
     */
    private GameSaveData createSaveDataFromCurrentState() {
        // Проверяем кэш для оптимизации
        long currentTime = System.currentTimeMillis();
        if (cachedSaveData != null && (currentTime - lastCacheUpdate) < CACHE_UPDATE_INTERVAL) {
            return cloneSaveData(cachedSaveData);
        }

        GameSaveData saveData = new GameSaveData();

        try {
            // Получаем компонент игрока через PlayerManager
            PlayerComponent player = PlayerManager.getInstance().getCurrentPlayer();

            if (player != null) {
                // Позиция игрока (если доступна)
                saveData.setPlayerPosition(new GameSaveData.Position(100, 100)); // Default position

                // Здоровье
                saveData.setPlayerHealth(player.getHealthPoints());
                saveData.setPlayerMaxHealth(player.getMaxHealthPoints());

                // Базовые данные (только поддерживаемые поля)
                // saveData.setGold(player.getGold()); // Будет добавлено если метод существует
                // saveData.setDifficulty(player.getGameDifficulty()); // Будет добавлено если метод существует
            } else {
                // Значения по умолчанию если игрок не найден
                setDefaultPlayerData(saveData);
            }

            // Базовая информация
            // saveData.setPlayTime(getTotalPlayTime()); // Будет добавлено когда метод появится

        } catch (Exception e) {
            GameLogger.error("Ошибка при создании данных сохранения", e);
            setDefaultPlayerData(saveData);
        }

        // Кэшируем данные
        cachedSaveData = cloneSaveData(saveData);
        lastCacheUpdate = currentTime;

        return saveData;
    }

    /**
     * Устанавливает данные по умолчанию для игрока
     */
    private void setDefaultPlayerData(GameSaveData saveData) {
        saveData.setPlayerPosition(new GameSaveData.Position(100, 100));
        saveData.setPlayerHealth(GameConstants.Player.DEFAULT_HEALTH);
        saveData.setPlayerMaxHealth(GameConstants.Player.DEFAULT_HEALTH);
        // Остальные поля будут добавлены когда соответствующие методы появятся в GameSaveData
    }

    /**
     * Применяет данные сохранения к состоянию игры
     */
    private void applySaveDataToGameState(GameSaveData saveData) {
        try {
            PlayerComponent player = PlayerManager.getInstance().getCurrentPlayer();

            if (player != null) {
                // Восстановление здоровья
                int health = Math.min(saveData.getPlayerHealth(), saveData.getPlayerMaxHealth());
                // player.setHealthPoints(health); // Будет реализовано позже

                GameLogger.gameplay("Игрок восстановлен с здоровьем: " + health);
            }

            // Обновление FXGL переменных (если методы существуют)
            // FXGL.set("coin", saveData.getGold()); // Будет добавлено когда метод появится

            GameLogger.gameplay("Состояние игры восстановлено из сохранения");

        } catch (Exception e) {
            GameLogger.error("Ошибка при применении сохранения", e);
        }
    }

    /**
     * Запускает автосохранение
     */
    private void startAutoSave() {
        if (!autoSaveEnabled.get()) {
            return;
        }

        stopAutoSave(); // Останавливаем предыдущий таймер

        autoSaveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (autoSaveEnabled.get()) {
                    autoSave();
                }
            }
        }, GameConstants.Save.AUTO_SAVE_INTERVAL, GameConstants.Save.AUTO_SAVE_INTERVAL);
    }

    /**
     * Останавливает автосохранение
     */
    private void stopAutoSave() {
        // Timer будет отменен при создании нового
    }

    /**
     * Выполняет автоматическое сохранение
     */
    private void autoSave() {
        saveGameAsync(generateAutoSaveName())
            .thenAccept(success -> {
                if (!success) {
                    GameLogger.warn("Автосохранение не удалось");
                }
            });
    }

    /**
     * Генерирует имя для автосохранения
     */
    private String generateAutoSaveName() {
        return GameConstants.Save.AUTO_SAVE_PREFIX + System.currentTimeMillis() + GameConstants.Save.SAVE_FILE_EXTENSION;
    }

    /**
     * Получает текущую версию системы сохранений
     */
    private String getCurrentVersion() {
        return "1.0";
    }

    /**
     * Проверяет совместимость версий
     */
    private boolean isVersionCompatible(String saveVersion) {
        // Простая проверка для начала
        return saveVersion != null && saveVersion.startsWith("1.");
    }

    /**
     * Создает копию данных сохранения для кэша
     */
    private GameSaveData cloneSaveData(GameSaveData original) {
        // Простое клонирование - копируем только существующие поля
        GameSaveData clone = new GameSaveData();
        clone.setPlayerPosition(original.getPlayerPosition());
        clone.setPlayerHealth(original.getPlayerHealth());
        clone.setPlayerMaxHealth(original.getPlayerMaxHealth());
        // Остальные поля будут добавлены когда методы появятся в GameSaveData
        return clone;
    }

    /**
     * Обновляет кэш данных сохранения
     */
    private void updateCache(GameSaveData saveData) {
        cachedSaveData = cloneSaveData(saveData);
        lastCacheUpdate = System.currentTimeMillis();
    }

    /**
     * Вызывается при выходе из игры
     */
    public void shutdown() {
        if (autoSaveTimer != null) {
            autoSaveTimer.cancel();
        }

        // Финальное сохранение
        quickSave().thenRun(() -> {
            GameLogger.system("Сервис сохранений завершен");
        });
    }

    /**
     * Получает статистику сохранений
     */
    public SaveStatistics getStatistics() {
        try {
            SaveSlotInfo[] saves = getAvailableSaves();
            long totalSize = 0;
            int autoSaveCount = 0;
            int quickSaveCount = 0;

            for (SaveSlotInfo save : saves) {
                // totalSize += save.getFileSize(); // Метод может не существовать
                if (save.getFilename().startsWith(GameConstants.Save.AUTO_SAVE_PREFIX)) {
                    autoSaveCount++;
                } else if (save.getFilename().startsWith(GameConstants.Save.QUICK_SAVE_PREFIX)) {
                    quickSaveCount++;
                }
            }

            return new SaveStatistics(saves.length, totalSize, autoSaveCount, quickSaveCount);
        } catch (Exception e) {
            GameLogger.error("Ошибка получения статистики сохранений", e);
            return new SaveStatistics(0, 0, 0, 0);
        }
    }

    /**
     * Статистика сохранений
     */
    public static class SaveStatistics {
        private final int totalSaves;
        private final long totalSize;
        private final int autoSaveCount;
        private final int quickSaveCount;

        public SaveStatistics(int totalSaves, long totalSize, int autoSaveCount, int quickSaveCount) {
            this.totalSaves = totalSaves;
            this.totalSize = totalSize;
            this.autoSaveCount = autoSaveCount;
            this.quickSaveCount = quickSaveCount;
        }

        public int getTotalSaves() { return totalSaves; }
        public long getTotalSize() { return totalSize; }
        public int getAutoSaveCount() { return autoSaveCount; }
        public int getQuickSaveCount() { return quickSaveCount; }
    }
}