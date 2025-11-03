package uwu.openjfx.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Утилитарный класс для логгирования в игре Royal Demons
 */
public class GameLogger {
    
    private static final Logger logger = LoggerFactory.getLogger(GameLogger.class);
    private static final Logger gameplayLogger = LoggerFactory.getLogger("GameLogger.Gameplay");
    private static final Logger systemLogger = LoggerFactory.getLogger("GameLogger.System");
    private static final Logger errorLogger = LoggerFactory.getLogger("GameLogger.Error");
    
    public static void info(String message) {
        logger.info("[Royal Demons] " + message);
    }
    
    public static void debug(String message) {
        logger.debug("[Royal Demons] " + message);
    }
    
    public static void warn(String message) {
        logger.warn("[Royal Demons] " + message);
    }
    
    public static void error(String message) {
        errorLogger.error("[Royal Demons] " + message);
    }
    
    public static void error(String message, Throwable throwable) {
        errorLogger.error("[Royal Demons] " + message, throwable);
    }
    
    public static void gameplay(String message) {
        gameplayLogger.info("[Gameplay] " + message);
    }
    
    public static void system(String message) {
        systemLogger.info("[System] " + message);
    }
}
