package uwu.openjfx.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса Platform
 */
class PlatformTest {

    @Test
    void testPlatformDetection() {
        Platform platform = Platform.getCurrent();
        assertNotNull(platform);
        assertTrue(platform == Platform.WINDOWS
                   || platform == Platform.LINUX
                   || platform == Platform.MACOS
                   || platform == Platform.UNKNOWN);
    }

    @Test
    void testPathSeparators() {
        String separator = Platform.getPathSeparator();
        assertNotNull(separator);
        assertTrue(separator.equals("/") || separator.equals("\\"));

        String classPathSeparator = Platform.getClassPathSeparator();
        assertEquals("/", classPathSeparator);
    }

    @Test
    void testUserDirectories() {
        String userHome = Platform.getUserHome();
        assertNotNull(userHome);
        assertFalse(userHome.isEmpty());

        String tempDir = Platform.getTempDirectory();
        assertNotNull(tempDir);
        assertFalse(tempDir.isEmpty());

        String savesDir = Platform.getGameSavesDirectory();
        assertNotNull(savesDir);
        assertFalse(savesDir.isEmpty());

        String configDir = Platform.getGameConfigDirectory();
        assertNotNull(configDir);
        assertFalse(configDir.isEmpty());
    }

    @Test
    void testPathNormalization() {
        String input = "path\\to\\file";
        String normalized = Platform.normalizePath(input);
        assertNotNull(normalized);

        // Проверяем, что нет одновременно / и \
        if (normalized.contains("/")) {
            assertFalse(normalized.contains("\\"));
        }
    }
}
