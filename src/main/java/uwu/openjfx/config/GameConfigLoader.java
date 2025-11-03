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
        Map<String, Object> weapon = (Map<String, Object>) weaponsConfig.get(weaponId);
        return weapon != null ? ((Number) weapon.get("attackDamage")).doubleValue() : 50.0;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getEnemyStats(String enemyType) {
        return (Map<String, Object>) enemiesConfig.get(enemyType);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getDifficultySettings(String difficulty) {
        return (Map<String, Object>) difficultyConfig.get(difficulty);
    }
}