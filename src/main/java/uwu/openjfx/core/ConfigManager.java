package uwu.openjfx.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер конфигурации - кроссплатформенная загрузка и сохранение конфигурации
 * Поддерживает JSON и YAML форматы
 */
public class ConfigManager {
    private static ConfigManager instance;

    private final Map<String, Object> configs = new ConcurrentHashMap<>();
    private final ObjectMapper jsonMapper;
    private final ObjectMapper yamlMapper;
    private final String configDirectory;

    private ConfigManager() {
        // Настраиваем JSON mapper
        jsonMapper = new ObjectMapper();
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Настраиваем YAML mapper
        YAMLFactory yamlFactory = new YAMLFactory()
            .disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID)
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        yamlMapper = new ObjectMapper(yamlFactory);

        configDirectory = Platform.getGameConfigDirectory();
        ensureConfigDirectory();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Создать директорию конфигурации если она не существует
     */
    private void ensureConfigDirectory() {
        try {
            Path dir = Paths.get(configDirectory);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                GameLogger.system("Created config directory: " + configDirectory);
            }
        } catch (IOException e) {
            GameLogger.error("ConfigManager", "Failed to create config directory: " + configDirectory, e);
        }
    }

    /**
     * Загрузить конфигурацию из файла
     */
    public <T> T loadConfig(String configName, Class<T> clazz) {
        return loadConfig(configName, clazz, "json"); // По умолчанию JSON
    }

    /**
     * Загрузить конфигурацию из файла с указанием формата
     */
    public <T> T loadConfig(String configName, Class<T> clazz, String format) {
        String fileName = configName.endsWith("." + format) ? configName : configName + "." + format;
        Path configPath = Paths.get(configDirectory, fileName);

        try {
            // Проверяем существование файла
            if (!Files.exists(configPath)) {
                // Создаем конфигурацию по умолчанию (без warning для первого запуска)
                T defaultConfig = createDefaultConfig(clazz);
                saveConfig(configName, defaultConfig, format);
                GameLogger.debug("ConfigManager", "Created default config: " + configPath);
                return defaultConfig;
            }

            // Определяем формат файла
            String fileFormat = format;
            if (configName.endsWith(".yaml") || configName.endsWith(".yml")) {
                fileFormat = "yaml";
            } else if (configName.endsWith(".json")) {
                fileFormat = "json";
            }

            // Загружаем конфигурацию
            T config;
            try (InputStream is = Files.newInputStream(configPath)) {
                if ("yaml".equalsIgnoreCase(fileFormat) || "yml".equalsIgnoreCase(fileFormat)) {
                    config = yamlMapper.readValue(is, clazz);
                } else {
                    config = jsonMapper.readValue(is, clazz);
                }
            }

            // Кэшируем конфигурацию
            configs.put(configName, config);

            GameLogger.debug("ConfigManager", "Loaded config: " + configName);
            return config;

        } catch (IOException e) {
            GameLogger.error("ConfigManager", "Failed to load config: " + configName, e);
            return createDefaultConfig(clazz);
        }
    }

    /**
     * Сохранить конфигурацию в файл
     */
    public void saveConfig(String configName, Object config) {
        saveConfig(configName, config, "json"); // По умолчанию JSON
    }

    /**
     * Сохранить конфигурацию в файл с указанием формата
     */
    public void saveConfig(String configName, Object config, String format) {
        String fileName = configName.endsWith("." + format) ? configName : configName + "." + format;
        Path configPath = Paths.get(configDirectory, fileName);

        try {
            // Создаем директорию если она не существует
            Files.createDirectories(configPath.getParent());

            // Записываем конфигурацию
            try (OutputStream os = Files.newOutputStream(configPath)) {
                if ("yaml".equalsIgnoreCase(format) || "yml".equalsIgnoreCase(format)) {
                    yamlMapper.writeValue(os, config);
                } else {
                    jsonMapper.writeValue(os, config);
                }
            }

            // Обновляем кэш
            configs.put(configName, config);

            GameLogger.debug("ConfigManager", "Saved config: " + configName);

        } catch (IOException e) {
            GameLogger.error("ConfigManager", "Failed to save config: " + configName, e);
        }
    }

    /**
     * Получить конфигурацию из кэша
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfig(String configName, Class<T> clazz) {
        Object config = configs.get(configName);
        if (config == null) {
            return loadConfig(configName, clazz);
        }
        return (T) config;
    }

    /**
     * Перезагрузить конфигурацию из файла
     */
    public <T> T reloadConfig(String configName, Class<T> clazz) {
        configs.remove(configName);
        return loadConfig(configName, clazz);
    }

    /**
     * Создать конфигурацию по умолчанию
     */
    private <T> T createDefaultConfig(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            GameLogger.error("ConfigManager", "Failed to create default config for " + clazz.getName(), e);
            return null;
        }
    }

    /**
     * Получить директорию конфигурации
     */
    public String getConfigDirectory() {
        return configDirectory;
    }

    /**
     * Получить путь к файлу конфигурации
     */
    public String getConfigPath(String configName) {
        return Paths.get(configDirectory, configName).toString();
    }

    /**
     * Проверить существование конфигурации
     */
    public boolean configExists(String configName) {
        Path jsonPath = Paths.get(configDirectory, configName + ".json");
        Path yamlPath = Paths.get(configDirectory, configName + ".yaml");
        Path ymlPath = Paths.get(configDirectory, configName + ".yml");

        return Files.exists(jsonPath) || Files.exists(yamlPath) || Files.exists(ymlPath);
    }

    /**
     * Удалить конфигурацию
     */
    public void deleteConfig(String configName) {
        configs.remove(configName);

        try {
            Path jsonPath = Paths.get(configDirectory, configName + ".json");
            Path yamlPath = Paths.get(configDirectory, configName + ".yaml");
            Path ymlPath = Paths.get(configDirectory, configName + ".yml");

            Files.deleteIfExists(jsonPath);
            Files.deleteIfExists(yamlPath);
            Files.deleteIfExists(ymlPath);

            GameLogger.debug("ConfigManager", "Deleted config: " + configName);

        } catch (IOException e) {
            GameLogger.error("ConfigManager", "Failed to delete config: " + configName, e);
        }
    }

    /**
     * Создать бэкап всех конфигураций
     */
    public void backupConfigs() {
        try {
            Path backupDir = Paths.get(configDirectory, "backups",
                "backup_" + System.currentTimeMillis());
            Files.createDirectories(backupDir);

            File configDir = new File(configDirectory);
            File[] files = configDir.listFiles((dir, name) ->
                name.endsWith(".json") || name.endsWith(".yaml") || name.endsWith(".yml"));

            if (files != null) {
                for (File file : files) {
                    Files.copy(file.toPath(),
                        Paths.get(backupDir.toString(), file.getName()));
                }
            }

            GameLogger.info("ConfigManager", "Configs backed up to: " + backupDir);

        } catch (IOException e) {
            GameLogger.error("ConfigManager", "Failed to backup configs", e);
        }
    }

    /**
     * Получить все конфигурации
     */
    public Map<String, Object> getAllConfigs() {
        return Collections.unmodifiableMap(configs);
    }
}
