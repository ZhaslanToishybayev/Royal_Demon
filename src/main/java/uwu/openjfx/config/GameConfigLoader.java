package uwu.openjfx.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import uwu.openjfx.utils.GameLogger;

import java.io.InputStream;
import java.util.Map;

/**
 * Загрузчик конфигурационных файлов игры
 */
public class GameConfigLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static GameConfigLoader instance;

    private Map<String, Object> weaponsConfig;
    private Map<String, Object> enemiesConfig;
    private Map<String, Object> difficultyConfig;
    private Map<String, Object> itemsConfig;

    private GameConfigLoader() {
        loadConfigurations();
    }

    public static GameConfigLoader getInstance() {
        if (instance == null) {
            instance = new GameConfigLoader();
        }
        return instance;
    }

    private void loadConfigurations() {
        try {
            weaponsConfig = loadConfig("/config/weapons.json");
            enemiesConfig = loadConfig("/config/enemies.json");
            difficultyConfig = loadConfig("/config/difficulty.json");
            itemsConfig = loadConfig("/config/items.json");

            GameLogger.system("Конфигурации успешно загружены");
        } catch (Exception e) {
            GameLogger.error("Ошибка загрузки конфигураций: " + e.getMessage(), e);
            throw new RuntimeException("Failed to load game configurations", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadConfig(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Configuration file not found: " + resourcePath);
            }
            return objectMapper.readValue(is, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration: " + resourcePath, e);
        }
    }

    public Map<String, Object> getWeaponsConfig() {
        return weaponsConfig;
    }

    public Map<String, Object> getEnemiesConfig() {
        return enemiesConfig;
    }

    public Map<String, Object> getDifficultyConfig() {
        return difficultyConfig;
    }

    public Map<String, Object> getItemsConfig() {
        return itemsConfig;
    }

    @SuppressWarnings("unchecked")
    public double getWeaponDamage(String weaponId) {
        if (weaponId == null || weaponId.trim().isEmpty()) {
            GameLogger.warn("Weapon ID is null or empty, using default damage");
            return 50.0;
        }

        Map<String, Object> weapon = (Map<String, Object>) weaponsConfig.get(weaponId);
        if (weapon == null) {
            GameLogger.warn("Weapon not found in config: " + weaponId + ", using default damage");
            return 50.0;
        }

        Object damage = weapon.get("attackDamage");
        if (damage instanceof Number) {
            double value = ((Number) damage).doubleValue();
            if (value < 0) {
                GameLogger.warn("Invalid damage value for weapon " + weaponId + ": " + value + ", using default");
                return 50.0;
            }
            return value;
        } else {
            GameLogger.warn("Invalid damage type for weapon " + weaponId + ", using default");
            return 50.0;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getEnemyStats(String enemyType) {
        if (enemyType == null || enemyType.trim().isEmpty()) {
            GameLogger.warn("Enemy type is null or empty");
            return null;
        }

        Map<String, Object> enemy = (Map<String, Object>) enemiesConfig.get(enemyType);
        if (enemy == null) {
            GameLogger.warn("Enemy type not found in config: " + enemyType);
        }
        return enemy;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getDifficultySettings(String difficulty) {
        if (difficulty == null || difficulty.trim().isEmpty()) {
            GameLogger.warn("Difficulty is null or empty");
            return null;
        }

        Map<String, Object> settings = (Map<String, Object>) difficultyConfig.get(difficulty);
        if (settings == null) {
            GameLogger.warn("Difficulty setting not found in config: " + difficulty);
        }
        return settings;
    }
}