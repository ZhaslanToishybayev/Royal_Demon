package uwu.openjfx.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import uwu.openjfx.utils.GameLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Менеджер сохранения и загрузки игры
 */
public class GameSaveManager {
    private static final String SAVE_DIRECTORY =
            System.getProperty("user.home") + "/.royal-demons/saves";
    private static final String SAVE_FILE_EXTENSION = ".json";
    private static final String SAVE_VERSION = "1.0.0"; // Версия формата сохранения

    private static GameSaveManager instance;
    private File saveDirectory;

    private GameSaveManager() {
        initializeSaveDirectory();
    }

    public static GameSaveManager getInstance() {
        if (instance == null) {
            instance = new GameSaveManager();
        }
        return instance;
    }

    private void initializeSaveDirectory() {
        try {
            Path savePath = Paths.get(SAVE_DIRECTORY);
            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath);
                GameLogger.system("Создана директория для сохранений: " + SAVE_DIRECTORY);
            }
            saveDirectory = savePath.toFile();
        } catch (IOException e) {
            GameLogger.error("Не удалось создать директорию сохранений: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize save directory", e);
        }
    }

    /**
     * Сохраняет игру
     *
     * @param saveData данные игры для сохранения
     */
    public void saveGame(GameSaveData saveData) {
        try {
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            String filename = generateSaveFileName(saveData);
            File saveFile = new File(saveDirectory, filename);

            saveData.setSaveTime(LocalDateTime.now());
            mapper.writeValue(saveFile, saveData);

            GameLogger.system("Игра сохранена в файл: " + filename);
        } catch (IOException e) {
            GameLogger.error("Ошибка сохранения игры: " + e.getMessage(), e);
            throw new RuntimeException("Failed to save game", e);
        }
    }

    /**
     * Загружает игру по имени файла
     *
     * @param filename имя файла сохранения
     * @return данные загруженной игры
     */
    public GameSaveData loadGame(String filename) {
        try {
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            File saveFile = new File(saveDirectory, filename);
            if (!saveFile.exists()) {
                throw new RuntimeException("Save file not found: " + filename);
            }

            GameSaveData saveData = mapper.readValue(saveFile,
                    GameSaveData.class);
            GameLogger.system("Игра загружена из файла: " + filename);
            return saveData;
        } catch (IOException e) {
            GameLogger.error("Ошибка загрузки игры: " + e.getMessage(), e);
            throw new RuntimeException("Failed to load game", e);
        }
    }

    /**
     * Возвращает список доступных сохранений
     *
     * @return массив информации о доступных сохранениях
     */
    public SaveSlotInfo[] getAvailableSaves() {
        File[] saveFiles = saveDirectory.listFiles((dir, name) ->
                name.endsWith(SAVE_FILE_EXTENSION));

        if (saveFiles == null) {
            return new SaveSlotInfo[0];
        }

        SaveSlotInfo[] slots = new SaveSlotInfo[saveFiles.length];
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        for (int i = 0; i < saveFiles.length; i++) {
            try {
                GameSaveData saveData = mapper.readValue(saveFiles[i],
                        GameSaveData.class);
                slots[i] = new SaveSlotInfo(
                        saveFiles[i].getName(),
                        saveData.getPlayerLevel(),
                        saveData.getSaveTime(),
                        saveData.getPlayTime()
                );
            } catch (IOException e) {
                GameLogger.warn("Пропущен поврежденный файл сохранения: " + saveFiles[i].getName());
                slots[i] = new SaveSlotInfo(saveFiles[i].getName(), 0, null, 0);
            }
        }

        // Сортировка по времени последнего сохранения
        java.util.Arrays.sort(slots, (a, b) -> {
            if (a.getSaveTime() == null && b.getSaveTime() == null) {
                return 0;
            }
            if (a.getSaveTime() == null) {
                return 1;
            }
            if (b.getSaveTime() == null) {
                return -1;
            }
            return b.getSaveTime().compareTo(a.getSaveTime());
        });

        return slots;
    }

    /**
     * Удаляет сохранение
     *
     * @param filename имя файла сохранения для удаления
     * @return true если сохранение успешно удалено
     */
    public boolean deleteSave(String filename) {
        File saveFile = new File(saveDirectory, filename);
        if (saveFile.exists() && saveFile.delete()) {
            GameLogger.system("Удалено сохранение: " + filename);
            return true;
        }
        return false;
    }

    /**
     * Автосохранение
     *
     * @param saveData данные игры для автосохранения
     */
    public void autoSave(GameSaveData saveData) {
        try {
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            File autoSaveFile = new File(saveDirectory, "autosave" + SAVE_FILE_EXTENSION);
            saveData.setSaveTime(LocalDateTime.now());
            mapper.writeValue(autoSaveFile, saveData);
            GameLogger.system("Автосохранение выполнено");
        } catch (IOException e) {
            GameLogger.warn("Не удалось выполнить автосохранение: " + e.getMessage());
        }
    }

    /**
     * Вычисляет MD5 checksum для файла
     *
     * @param file файл для вычисления checksum
     * @return строка с контрольной суммой или null при ошибке
     */
    private String calculateChecksum(File file) {
        try {
            byte[] data = Files.readAllBytes(file.toPath());
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data);

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            GameLogger.warn("Failed to calculate checksum: " + e.getMessage());
            return null;
        }
    }

    /**
     * Проверяет целостность файла сохранения
     *
     * @param file файл для проверки
     * @return true если файл прошел валидацию
     */
    private boolean validateSaveIntegrity(File file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            GameSaveData saveData = mapper.readValue(file, GameSaveData.class);

            // Проверяем наличие обязательных полей
            String difficulty = saveData.getDifficulty();
            if (difficulty == null || difficulty.trim().isEmpty()) {
                GameLogger.warn("Save file has invalid difficulty: " + file.getName());
                return false;
            }

            int playerLevel = saveData.getPlayerLevel();
            if (playerLevel < 0) {
                GameLogger.warn("Save file has invalid player level: " + file.getName());
                return false;
            }

            int gold = saveData.getGold();
            if (gold < 0) {
                GameLogger.warn("Save file has invalid gold amount: " + file.getName());
                return false;
            }

            int playerHealth = saveData.getPlayerHealth();
            int maxHealth = saveData.getPlayerMaxHealth();
            if (playerHealth <= 0 || playerHealth > maxHealth || maxHealth <= 0) {
                GameLogger.warn("Save file has invalid health: " + file.getName() +
                                " (health=" + playerHealth + ", maxHealth=" + maxHealth + ")");
                return false;
            }

            GameLogger.debug("Save file validation passed: " + file.getName());
            return true;
        } catch (Exception e) {
            GameLogger.warn("Failed to validate save file: " + file.getName() + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Возвращает версию формата сохранения
     *
     * @return строка с версией
     */
    public String getSaveVersion() {
        return SAVE_VERSION;
    }

    /**
     * Загрузка последнего автосохранения
     *
     * @return данные автосохранения или null если файл не найден
     */
    public GameSaveData loadAutoSave() {
        File autoSaveFile = new File(saveDirectory, "autosave" + SAVE_FILE_EXTENSION);
        if (!autoSaveFile.exists()) {
            return null;
        }

        return loadGame("autosave" + SAVE_FILE_EXTENSION);
    }

    /**
     * Генерирует имя файла сохранения
     *
     * @param saveData данные игры
     * @return имя файла сохранения
     */
    private String generateSaveFileName(GameSaveData saveData) {
        String timestamp = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        return String.format(
            "save_%s_level_%d%s",
            timestamp,
            saveData.getPlayerLevel(),
            SAVE_FILE_EXTENSION);
    }
}