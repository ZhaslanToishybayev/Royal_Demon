package uwu.openjfx.core;

import com.almasb.fxgl.dsl.FXGL;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Менеджер ресурсов - кроссплатформенная загрузка ресурсов из JAR и файловой системы
 */
public class ResourceManager {

    private static ResourceManager instance;

    private ResourceManager() {}

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    /**
     * Получить InputStream ресурса из classpath
     */
    public InputStream getResourceAsStream(String path) {
        // Убираем ведущий слэш если есть
        String normalizedPath = path.startsWith("/") ? path.substring(1) : path;

        // Используем ClassLoader
        return getClass().getClassLoader().getResourceAsStream(normalizedPath);
    }

    /**
     * Получить URL ресурса
     */
    public URL getResource(String path) {
        String normalizedPath = path.startsWith("/") ? path.substring(1) : path;

        // Пытаемся загрузить через ClassLoader
        URL url = getClass().getClassLoader().getResource(normalizedPath);
        if (url != null) {
            return url;
        }

        // Fallback к Class.getResource
        return getClass().getResource(path.startsWith("/") ? path : "/" + path);
    }

    /**
     * Проверить существование ресурса
     */
    public boolean resourceExists(String path) {
        return getResource(path) != null;
    }

    /**
     * Получить список всех ресурсов в директории (работает из JAR!)
     */
    public List<String> listResources(String directory) {
        List<String> resources = new ArrayList<>();

        try {
            String path = directory.startsWith("/") ? directory.substring(1) : directory;
            if (!path.endsWith("/")) {
                path += "/";
            }

            // Получаем URL директории
            URL dirURL = getClass().getClassLoader().getResource(path);

            if (dirURL == null) {
                // Пытаемся как файл
                return listFileSystemResources(directory);
            }

            // Проверяем JAR URL
            if (dirURL.getProtocol().equals("jar")) {
                return listJarResources(dirURL, path);
            } else if (dirURL.getProtocol().equals("file")) {
                // Файловая система (dev mode)
                return listFileSystemResources(directory);
            }

        } catch (Exception e) {
            GameLogger.error("ResourceManager", "Error listing resources in: " + directory + " - " + e.getMessage());
        }

        return Collections.emptyList();
    }

    /**
     * Список ресурсов в JAR файле
     */
    private List<String> listJarResources(URL jarURL, String path) throws Exception {
        List<String> resources = new ArrayList<>();

        try {
            // Получаем путь к JAR файлу
            String jarPath = jarURL.getPath();
            int bangIndex = jarPath.indexOf("!");
            if (bangIndex > 0) {
                jarPath = jarPath.substring(0, bangIndex);
            }

            // Декодируем URL-encoded символы (например %20 для пробелов)
            jarPath = java.net.URLDecoder.decode(jarPath, "UTF-8");

            // Убираем "file:" префикс если есть
            if (jarPath.startsWith("file:")) {
                jarPath = jarPath.substring(5);
            }

            GameLogger.debug("ResourceManager", "Opening JAR: " + jarPath);
            GameLogger.debug("ResourceManager", "Looking for path: " + path);

            // Открываем JAR и читаем записи
            try (JarFile jarFile = new JarFile(jarPath)) {

                Enumeration<JarEntry> entries = jarFile.entries();
                String prefix = path;

                int matchCount = 0;
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    if (name.startsWith(prefix) && !name.equals(prefix)) {
                        // Убираем префикс
                        String relativeName = name.substring(prefix.length());
                        // Проверяем что это файл (не директория) и нет слешей
                        if (!relativeName.isEmpty() && !relativeName.contains("/")) {
                            resources.add(relativeName);
                            matchCount++;
                        }
                    }
                }

                GameLogger.debug("ResourceManager", "Found " + matchCount + " resources in " + path);
            }
        } catch (Exception e) {
            GameLogger.error("ResourceManager", "Error reading JAR: " + e.getMessage(), e);
            throw e;
        }

        return resources;
    }

    /**
     * Список ресурсов в файловой системе
     */
    private List<String> listFileSystemResources(String directory) {
        List<String> resources = new ArrayList<>();

        try {
            String path = directory.startsWith("/") ? directory.substring(1) : directory;
            URL dirURL = getClass().getClassLoader().getResource(path);

            if (dirURL != null && dirURL.getProtocol().equals("file")) {
                java.io.File dir = new java.io.File(dirURL.getPath());
                if (dir.exists() && dir.isDirectory()) {
                    String[] files = dir.list();
                    if (files != null) {
                        for (String file : files) {
                            resources.add(file);
                        }
                    }
                }
            }
        } catch (Exception e) {
            GameLogger.error("ResourceManager", "Error listing file system resources: " + directory + " - " + e.getMessage());
        }

        return resources;
    }

    /**
     * Получить все файлы с определенным расширением в директории
     */
    public List<String> listResourcesByExtension(String directory, String extension) {
        List<String> allResources = listResources(directory);
        List<String> filteredResources = new ArrayList<>();

        for (String resource : allResources) {
            if (resource.endsWith("." + extension)) {
                filteredResources.add(resource);
            }
        }

        return filteredResources;
    }

    /**
     * Нормализовать путь ресурса
     */
    public String normalizeResourcePath(String path) {
        if (path == null) {
            return null;
        }

        // Убираем ведущий слэш
        String normalized = path.startsWith("/") ? path.substring(1) : path;

        // Заменяем обратные слэши на прямые
        normalized = normalized.replace('\\', '/');

        return normalized;
    }
}
