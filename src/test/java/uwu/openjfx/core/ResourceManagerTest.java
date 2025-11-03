package uwu.openjfx.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для ResourceManager
 */
class ResourceManagerTest {

    @Test
    void testResourceManagerSingleton() {
        ResourceManager manager1 = ResourceManager.getInstance();
        ResourceManager manager2 = ResourceManager.getInstance();
        assertSame(manager1, manager2);
    }

    @Test
    void testResourceExists() {
        ResourceManager manager = ResourceManager.getInstance();

        // Проверяем существование стандартных ресурсов
        assertTrue(manager.resourceExists("i18n/strings_ru.properties"));
        assertFalse(manager.resourceExists("nonexistent/resource.txt"));
    }

    @Test
    void testResourceList() {
        ResourceManager manager = ResourceManager.getInstance();

        // Получаем список оружия
        var weapons = manager.listResourcesByExtension("assets/textures/ui/inventory", "png");
        assertNotNull(weapons);
        assertTrue(weapons.size() > 0);

        // Проверяем, что список содержит файлы
        for (String weapon : weapons) {
            assertTrue(weapon.endsWith(".png"));
        }
    }

    @Test
    void testResourceNormalization() {
        ResourceManager manager = ResourceManager.getInstance();

        // Тестируем нормализацию путей
        String path1 = "/assets/textures/player.png";
        String normalized1 = manager.normalizeResourcePath(path1);
        assertEquals("assets/textures/player.png", normalized1);

        String path2 = "assets\\textures\\player.png";
        String normalized2 = manager.normalizeResourcePath(path2);
        assertEquals("assets/textures/player.png", normalized2);
    }

    @Test
    void testLoadResourceAsStream() {
        ResourceManager manager = ResourceManager.getInstance();

        // Пробуем загрузить существующий ресурс
        var is = manager.getResourceAsStream("i18n/strings_ru.properties");
        assertNotNull(is);

        // Пробуем загрузить несуществующий ресурс
        var nullIs = manager.getResourceAsStream("nonexistent/file.txt");
        assertNull(nullIs);
    }
}
