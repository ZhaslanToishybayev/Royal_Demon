package uwu.openjfx.core.modules;

import uwu.openjfx.core.*;
import uwu.openjfx.config.GameConfig;

/**
 * Основной модуль ядра игры
 */
public class CoreModule implements GameModule {
    private GameConfig gameConfig;
    private ModuleState state = ModuleState.UNLOADED;

    @Override
    public String getName() {
        return "Core";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "Core game systems - configuration, logging, and platform utilities";
    }

    @Override
    public void initialize() throws ModuleException {
        state = ModuleState.INITIALIZING;

        try {
            // Инициализируем систему логирования
            GameLogger.system("Initializing Core Module...");

            // Загружаем конфигурацию
            ConfigManager configManager = ConfigManager.getInstance();
            gameConfig = configManager.getConfig("game", GameConfig.class);

            // Настраиваем логирование по конфигурации
            if (gameConfig != null && gameConfig.isEnableDebugMode()) {
                GameLogger.setGlobalLevel(GameLogger.Level.DEBUG);
            } else {
                GameLogger.setGlobalLevel(GameLogger.Level.INFO);
            }

            // Проверяем платформу
            GameLogger.system("Running on: " + Platform.getCurrent());
            GameLogger.system("Config directory: " + Platform.getGameConfigDirectory());
            GameLogger.system("Saves directory: " + Platform.getGameSavesDirectory());

            state = ModuleState.ACTIVE;

        } catch (Exception e) {
            state = ModuleState.ERROR;
            throw new ModuleException("Failed to initialize Core Module", e);
        }
    }

    @Override
    public void shutdown() throws ModuleException {
        try {
            state = ModuleState.SHUTTING_DOWN;

            // Сохраняем конфигурацию
            if (gameConfig != null) {
                ConfigManager configManager = ConfigManager.getInstance();
                configManager.saveConfig("game", gameConfig);
            }

            // Завершаем логирование
            GameLogger.shutdown();

            state = ModuleState.UNLOADED;

        } catch (Exception e) {
            throw new ModuleException("Failed to shutdown Core Module", e);
        }
    }

    @Override
    public boolean isInitialized() {
        return state == ModuleState.ACTIVE;
    }

    @Override
    public int getPriority() {
        return 0; // Самый высокий приоритет - загружается первым
    }

    @Override
    public ModuleState getState() {
        return state;
    }

    @Override
    public void setState(ModuleState newState) {
        state = newState;
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }
}
