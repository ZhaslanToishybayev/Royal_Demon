package uwu.openjfx.core;

/**
 * Базовый интерфейс для всех игровых модулей
 * Каждый модуль представляет собой независимую часть системы
 */
public interface GameModule {
    /**
     * Имя модуля
     */
    String getName();

    /**
     * Версия модуля
     */
    String getVersion();

    /**
     * Описание модуля
     */
    String getDescription();

    /**
     * Инициализация модуля
     */
    void initialize() throws ModuleException;

    /**
     * Завершение работы модуля
     */
    void shutdown() throws ModuleException;

    /**
     * Проверить, инициализирован ли модуль
     */
    boolean isInitialized();

    /**
     * Получить приоритет загрузки модуля
     * Меньшее значение = более ранняя загрузка
     */
    default int getPriority() {
        return 100;
    }

    /**
     * Проверить зависимости модуля
     */
    default String[] getDependencies() {
        return new String[0];
    }

    /**
     * Получить состояние модуля
     */
    ModuleState getState();

    /**
     * Установить состояние модуля
     */
    default void setState(ModuleState state) {}

    /**
     * Событие инициализации завершена
     */
    default void onInitialized() {
        GameLogger.info(getName(), "Module initialized: " + getName() + " v" + getVersion());
    }

    /**
     * Событие завершения работы
     */
    default void onShutdown() {
        GameLogger.info(getName(), "Module shutdown: " + getName());
    }

    /**
     * Ошибка инициализации
     */
    default void onError(Exception e) {
        GameLogger.error(getName(), "Module error in " + getName() + ": " + e.getMessage(), e);
    }

    /**
     * Исключение для ошибок модуля
     */
    class ModuleException extends Exception {
        public ModuleException(String message) {
            super(message);
        }

        public ModuleException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Состояние модуля
     */
    enum ModuleState {
        UNLOADED,    // Модуль не загружен
        LOADING,     // Модуль загружается
        INITIALIZING, // Модуль инициализируется
        ACTIVE,      // Модуль активен и работает
        SHUTTING_DOWN, // Модуль завершает работу
        ERROR        // Модуль в состоянии ошибки
    }
}
