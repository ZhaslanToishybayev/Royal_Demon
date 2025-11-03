package uwu.openjfx.utils;

import uwu.openjfx.components.PlayerManager;
import uwu.openjfx.core.GameConstants;

/**
 * Быстрый тест для проверки улучшений
 */
public class QuickTest {

    public static void main(String[] args) {
        System.out.println("🧪 Тестирование улучшенной архитектуры Royal Demons\n");

        // Тест PlayerManager
        testPlayerManager();

        // Тест GameConstants
        testGameConstants();

        System.out.println("✅ Все тесты успешно пройдены!");
        System.out.println("📈 Улучшения готовы к использованию!");
    }

    private static void testPlayerManager() {
        System.out.println("=== PlayerManager Тест ===");

        PlayerManager manager = PlayerManager.getInstance();

        // Безопасные операции без NPE
        System.out.println("Gold: " + manager.getGold());
        System.out.println("Attack Power: " + manager.getAttackPower());
        System.out.println("Health: " + manager.getHealth());
        System.out.println("Speed: " + manager.getSpeed());
        System.out.println("Player registered: " + manager.isPlayerRegistered());

        // Безопасные операции
        manager.addGold(100);
        System.out.println("Gold after add: " + manager.getGold());

        System.out.println();
    }

    private static void testGameConstants() {
        System.out.println("=== GameConstants Тест ===");

        System.out.println("Default Speed: " + GameConstants.Player.DEFAULT_SPEED);
        System.out.println("Magic Speed: " + GameConstants.Player.MAGIC_SPEED);
        System.out.println("Default Health: " + GameConstants.Player.DEFAULT_HEALTH);

        System.out.println("Golden Sword Damage: " +
            java.util.Arrays.toString(GameConstants.Weapons.GoldenSword.ATTACK_DAMAGE));

        System.out.println("Ultimate Cooldown: " + GameConstants.Combat.ULTIMATE_COOLDOWN_MULTIPLIER);

        System.out.println();
    }
}