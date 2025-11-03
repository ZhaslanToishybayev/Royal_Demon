package uwu.openjfx.core;

/**
 * Платформенно-независимые утилиты для работы с операционной системой
 */
public enum Platform {
    WINDOWS,
    LINUX,
    MACOS,
    UNKNOWN;

    private static final Platform CURRENT = detectPlatform();

    /**
     * Определяет текущую платформу
     */
    private static Platform detectPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return WINDOWS;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return LINUX;
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            return MACOS;
        }
        return UNKNOWN;
    }

    /**
     * Получить текущую платформу
     */
    public static Platform getCurrent() {
        return CURRENT;
    }

    /**
     * Проверка на Windows
     */
    public static boolean isWindows() {
        return CURRENT == WINDOWS;
    }

    /**
     * Проверка на Linux
     */
    public static boolean isLinux() {
        return CURRENT == LINUX;
    }

    /**
     * Проверка на macOS
     */
    public static boolean isMacOS() {
        return CURRENT == MACOS;
    }

    /**
     * Получить разделитель пути для текущей платформы
     */
    public static String getPathSeparator() {
        return System.getProperty("file.separator");
    }

    /**
     * Получить разделитель пути classpath (всегда '/')
     */
    public static String getClassPathSeparator() {
        return "/";
    }

    /**
     * Нормализовать путь для текущей платформы
     */
    public static String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }

        // Для classpath всегда используем прямой слэш
        if (path.startsWith("classpath:") || path.startsWith("/")) {
            return path.replaceAll("\\\\", "/");
        }

        // Для файловых путей используем правильный разделитель
        String separator = getPathSeparator();
        if (!separator.equals("/")) {
            path = path.replace("/", separator);
        }

        return path;
    }

    /**
     * Получить домашнюю директорию пользователя
     */
    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    /**
     * Получить временную директорию
     */
    public static String getTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * Получить директорию сохранений игры
     */
    public static String getGameSavesDirectory() {
        String home = getUserHome();
        String separator = getPathSeparator();

        if (isWindows()) {
            return home + separator + "Documents" + separator + "RoyalDemons" + separator + "saves";
        } else if (isLinux()) {
            return home + separator + ".local" + separator + "share" + separator + "RoyalDemons" + separator + "saves";
        } else if (isMacOS()) {
            return home + separator + "Library" + separator + "Application Support" + separator + "RoyalDemons" + separator + "saves";
        }

        // Fallback
        return home + separator + "RoyalDemons" + separator + "saves";
    }

    /**
     * Получить директорию конфигурации игры
     */
    public static String getGameConfigDirectory() {
        String home = getUserHome();
        String separator = getPathSeparator();

        if (isWindows()) {
            return home + separator + "Documents" + separator + "RoyalDemons" + separator + "config";
        } else if (isLinux()) {
            return home + separator + ".config" + separator + "RoyalDemons";
        } else if (isMacOS()) {
            return home + separator + "Library" + separator + "Preferences" + separator + "RoyalDemons";
        }

        // Fallback
        return home + separator + "RoyalDemons" + separator + "config";
    }

    /**
     * Получить директорию логов игры
     */
    public static String getGameLogsDirectory() {
        String home = getUserHome();
        String separator = getPathSeparator();

        if (isWindows()) {
            return home + separator + "Documents" + separator + "RoyalDemons" + separator + "logs";
        } else if (isLinux()) {
            return home + separator + ".local" + separator + "share" + separator + "RoyalDemons" + separator + "logs";
        } else if (isMacOS()) {
            return home + separator + "Library" + separator + "Logs" + separator + "RoyalDemons";
        }

        // Fallback
        return home + separator + "RoyalDemons" + separator + "logs";
    }
}
