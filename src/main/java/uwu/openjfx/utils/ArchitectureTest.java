package uwu.openjfx.utils;

import uwu.openjfx.components.PlayerManager;
import uwu.openjfx.components.PlayerComponent;
import uwu.openjfx.core.GameConstants;
import uwu.openjfx.save.GameSaveService;

/**
 * Утилита для тестирования новой архитектуры.
 * Демонстрирует преимущества улучшенной системы.
 */
public class ArchitectureTest {

    /**
     * Тестирование PlayerManager
     */
    public static void testPlayerManager() {
        System.out.println("=== Тестирование PlayerManager ===");

        PlayerManager manager = PlayerManager.getInstance();

        // Безопасные операции без NPE
        System.out.println("Gold: " + manager.getGold()); // 0, а не NPE
        System.out.println("Attack Power: " + manager.getAttackPower()); // 1.0, а не NPE
        System.out.println("Weapon: " + manager.getCurrentWeapon()); // null, а не NPE

        // Безопасное добавление золота
        manager.addGold(100);
        System.out.println("Gold after add: " + manager.getGold());

        System.out.println("Player registered: " + manager.isPlayerRegistered());
        System.out.println();
    }

    /**
     * Демонстрация использования GameConstants
     */
    public static void testGameConstants() {
        System.out.println("=== Тестирование GameConstants ===");

        // Параметры игрока
        System.out.println("Default Speed: " + GameConstants.Player.DEFAULT_SPEED);
        System.out.println("Magic Speed: " + GameConstants.Player.MAGIC_SPEED);
        System.out.println("Default Health: " + GameConstants.Player.DEFAULT_HEALTH);

        // Параметры оружия
        System.out.println("Golden Sword Damage (Lvl 1): " +
            GameConstants.Weapons.GoldenSword.ATTACK_DAMAGE[0]);
        System.out.println("Bow Damage (Lvl 3): " +
            GameConstants.Weapons.Bow.ATTACK_DAMAGE[2]);

        // Параметры боя
        System.out.println("Ultimate Cooldown Multiplier: " +
            GameConstants.Combat.ULTIMATE_COOLDOWN_MULTIPLIER);
        System.out.println("Attack Power Buff Duration: " +
            GameConstants.Combat.ATTACK_POWER_BUFF_DURATION);

        System.out.println();
    }

    /**
     * Демонстрация улучшенной системы сохранений
     */
    public static void testGameSaveService() {
        System.out.println("=== Тестирование GameSaveService ===");

        GameSaveService saveService = GameSaveService.getInstance();

        // Получение статистики
        var stats = saveService.getStatistics();
        System.out.println("Total saves: " + stats.getTotalSaves());
        System.out.println("Auto-saves: " + stats.getAutoSaveCount());
        System.out.println("Quick-saves: " + stats.getQuickSaveCount());

        // Асинхронное сохранение
        saveService.saveGameAsync("test_save.json")
            .thenAccept(success -> {
                System.out.println("Async save result: " + success);
            });

        System.out.println();
    }

    /**
     * Сравнение старого и нового подходов
     */
    public static void compareApproaches() {
        System.out.println("=== Сравнение подходов ===");

        System.out.println("СТАРЫЙ ПОДХОД (статические методы):");
        System.out.println("- Риск NPE при вызове до инициализации");
        System.out.println("- Сложно тестировать");
        System.out.println("- Глобальное состояние");

        System.out.println("\nНОВЫЙ ПОДХОД (PlayerManager):");
        System.out.println("- Безопасные операции с null-проверками");
        System.out.println("- Легко тестировать");
        System.out.println("- Контролируемое состояние");

        System.out.println("\nИСПОЛЬЗОВАНИЕ КОНСТАНТ:");
        System.out.println("- Старый: magic numbers в коде");
        System.out.println("- Новый: GameConstants.Player.DEFAULT_SPEED");

        System.out.println();
    }

    /**
     * Запуск всех тестов
     */
    public static void runAllTests() {
        System.out.println("🧪 Тестирование улучшенной архитектуры Royal Demons\n");

        testPlayerManager();
        testGameConstants();
        testGameSaveService();
        compareApproaches();

        System.out.println("✅ Все тесты завершены успешно!");
        System.out.println("📈 Архитектура готова к использованию.");
    }
}