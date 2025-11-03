package uwu.openjfx.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.*;

/**
 * Централизованная система логирования для игры
 * Поддерживает консольный вывод и запись в файл
 */
public class GameLogger {
    private static final ConcurrentMap<String, Logger> loggers = new ConcurrentHashMap<>();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    // Уровни логирования
    public enum Level {
        DEBUG(10), INFO(20), GAMEPLAY(25), WARNING(30), ERROR(40), SYSTEM(50);

        private final int value;

        Level(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // Создать логгер для категории
    public static Logger getLogger(String category) {
        return loggers.computeIfAbsent(category, GameLogger::createLogger);
    }

    // Создать логгер
    private static Logger createLogger(String category) {
        Logger logger = Logger.getLogger("RoyalDemons." + category);
        logger.setUseParentHandlers(false);

        // Создаем форматировщик
        Formatter formatter = createFormatter();

        // Консольный обработчик
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        consoleHandler.setLevel(java.util.logging.Level.INFO);
        logger.addHandler(consoleHandler);

        // Файловый обработчик
        try {
            File logDir = new File(Platform.getGameLogsDirectory());
            if (!logDir.exists() && !logDir.mkdirs()) {
                consoleHandler.getFormatter().format(new LogRecord(java.util.logging.Level.WARNING,
                    "Failed to create log directory: " + logDir.getAbsolutePath()));
            } else {
                File logFile = new File(logDir, "royal-demons.log");

                FileHandler fileHandler = new FileHandler(logFile.getAbsolutePath(), true);
                fileHandler.setFormatter(formatter);
                fileHandler.setLevel(java.util.logging.Level.ALL);
                logger.addHandler(fileHandler);
            }
        } catch (IOException e) {
            consoleHandler.getFormatter().format(new LogRecord(java.util.logging.Level.WARNING,
                "Failed to create file handler: " + e.getMessage()));
        }

        logger.setLevel(java.util.logging.Level.ALL);
        return logger;
    }

    // Создать форматировщик
    private static Formatter createFormatter() {
        return new Formatter() {
            @Override
            public String format(LogRecord record) {
                String timestamp = dateFormat.format(new Date(record.getMillis()));
                String level = record.getLevel().getName();
                String category = record.getLoggerName().replace("RoyalDemons.", "");
                String message = record.getMessage();

                if (record.getThrown() != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    record.getThrown().printStackTrace(pw);
                    message += "\n" + sw.toString();
                }

                return String.format("[%s] [%s] [%s] %s%n",
                    timestamp, level, category, message);
            }
        };
    }

    // Логирование отладки
    public static void debug(String category, String message) {
        Logger logger = getLogger(category);
        logger.log(java.util.logging.Level.FINE, message);
    }

    // Информационное логирование
    public static void info(String category, String message) {
        Logger logger = getLogger(category);
        logger.log(java.util.logging.Level.INFO, message);
    }

    // Логирование геймплея
    public static void gameplay(String category, String message) {
        Logger logger = getLogger(category);
        logger.log(java.util.logging.Level.CONFIG, message);
    }

    // Предупреждение
    public static void warning(String category, String message) {
        Logger logger = getLogger(category);
        logger.log(java.util.logging.Level.WARNING, message);
    }

    // Ошибка
    public static void error(String category, String message) {
        Logger logger = getLogger(category);
        logger.log(java.util.logging.Level.SEVERE, message);
    }

    // Ошибка с исключением
    public static void error(String category, String message, Throwable throwable) {
        Logger logger = getLogger(category);
        logger.log(java.util.logging.Level.SEVERE, message, throwable);
    }

    // Системное логирование
    public static void system(String message) {
        Logger logger = getLogger("System");
        logger.log(java.util.logging.Level.SEVERE, "[SYSTEM] " + message);
    }

    // Логирование в консоль (без категории)
    public static void console(String message) {
        System.out.println("[CONSOLE] " + message);
    }

    // Очистить все логгеры
    public static void shutdown() {
        for (Logger logger : loggers.values()) {
            for (Handler handler : logger.getHandlers()) {
                handler.close();
            }
        }
        loggers.clear();
    }

    // Настройка глобального уровня логирования
    public static void setGlobalLevel(Level level) {
        java.util.logging.Level javaLevel = mapLevel(level);
        Logger globalLogger = Logger.getLogger("uwu.openjfx");
        globalLogger.setLevel(javaLevel);

        // Устанавливаем уровень для всех существующих логгеров
        for (Logger logger : loggers.values()) {
            logger.setLevel(javaLevel);
            for (Handler handler : logger.getHandlers()) {
                handler.setLevel(javaLevel);
            }
        }
    }

    // Маппинг нашего Level на java.util.logging.Level
    private static java.util.logging.Level mapLevel(Level level) {
        return switch (level) {
            case DEBUG -> java.util.logging.Level.FINE;
            case INFO -> java.util.logging.Level.INFO;
            case GAMEPLAY -> java.util.logging.Level.CONFIG;
            case WARNING -> java.util.logging.Level.WARNING;
            case ERROR -> java.util.logging.Level.SEVERE;
            case SYSTEM -> java.util.logging.Level.SEVERE;
        };
    }

    // Получить директорию логов
    public static String getLogDirectory() {
        return Platform.getGameLogsDirectory();
    }
}
