package uwu.openjfx.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;

/**
 * Тесты для ConfigManager
 */
class ConfigManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void testConfigManagerSingleton() {
        ConfigManager manager1 = ConfigManager.getInstance();
        ConfigManager manager2 = ConfigManager.getInstance();
        assertSame(manager1, manager2);
    }

    @Test
    void testDefaultConfigCreation() {
        TestConfig config = ConfigManager.getInstance().loadConfig("test", TestConfig.class);
        assertNotNull(config);
        assertEquals("default", config.getValue());
    }

    @Test
    void testConfigSaveAndLoad() {
        ConfigManager manager = ConfigManager.getInstance();

        // Создаем и сохраняем конфигурацию
        TestConfig config = new TestConfig();
        config.setValue("test_value");
        manager.saveConfig("test_config", config);

        // Загружаем и проверяем
        TestConfig loadedConfig = manager.loadConfig("test_config", TestConfig.class);
        assertNotNull(loadedConfig);
        assertEquals("test_value", loadedConfig.getValue());
    }

    @Test
    void testConfigUpdate() {
        ConfigManager manager = ConfigManager.getInstance();

        // Загружаем конфигурацию
        TestConfig config = manager.getConfig("test_update", TestConfig.class);
        assertEquals("default", config.getValue());

        // Обновляем
        config.setValue("updated");
        manager.saveConfig("test_update", config);

        // Проверяем обновление
        TestConfig updatedConfig = manager.getConfig("test_update", TestConfig.class);
        assertEquals("updated", updatedConfig.getValue());
    }

    @Test
    void testConfigExists() {
        ConfigManager manager = ConfigManager.getInstance();

        assertFalse(manager.configExists("nonexistent"));

        TestConfig config = new TestConfig();
        manager.saveConfig("existence_test", config);
        assertTrue(manager.configExists("existence_test"));
    }

    @Test
    void testAllConfigsMap() {
        ConfigManager manager = ConfigManager.getInstance();

        // Сохраняем несколько конфигураций
        manager.saveConfig("config1", new TestConfig());
        manager.saveConfig("config2", new TestConfig());

        var allConfigs = manager.getAllConfigs();
        assertTrue(allConfigs.size() >= 2);
        assertTrue(allConfigs.containsKey("config1"));
        assertTrue(allConfigs.containsKey("config2"));
    }

    /**
     * Тестовый класс конфигурации
     */
    private static class TestConfig {
        private String value = "default";

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
