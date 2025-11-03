package uwu.openjfx.integration;

import com.almasb.fxgl.dsl.FXGL;
import uwu.openjfx.achievements.SimpleAchievements;
import uwu.openjfx.combo.SimpleComboSystem;

/**
 * Вспомогательные методы для интеграции новых систем
 */
public class IntegrationHelpers {
    
    /**
     * Вызвать при атаке игрока
     */
    public static void onPlayerAttack(String attackType) {
        FXGL.set("playerIsAttacking", true);
        FXGL.set("playerAttackType", attackType);
        SimpleComboSystem.getInstance().addAttack(attackType);
        
        // Сбросим флаг атаки через короткое время
        FXGL.getGameTimer().runOnceAfter(() -> {
            FXGL.set("playerIsAttacking", false);
        }, javafx.util.Duration.millis(200));
    }
    
    /**
     * Вызвать при открытии сундука
     */
    public static void onChestOpened() {
        try {
            int currentChests = FXGL.geti("openedChests");
            FXGL.set("openedChests", currentChests + 1);
        } catch (Exception e) {
            FXGL.set("openedChests", 1);
        }
        SimpleAchievements.getInstance().checkAchievements();
    }
    
    /**
     * Вызвать при исследовании новой комнаты
     */
    public static void onRoomExplored() {
        try {
            int currentRooms = FXGL.geti("exploredRooms");
            FXGL.set("exploredRooms", currentRooms + 1);
        } catch (Exception e) {
            FXGL.set("exploredRooms", 1);
        }
        SimpleAchievements.getInstance().checkAchievements();
    }
    
    /**
     * Вызвать при выполнении комбо
     */
    public static void onComboCompleted() {
        try {
            int currentCombos = FXGL.geti("completedCombos");
            FXGL.set("completedCombos", currentCombos + 1);
        } catch (Exception e) {
            FXGL.set("completedCombos", 1);
        }
        SimpleAchievements.getInstance().checkAchievements();
    }
    
    /**
     * Показать уведомление о локализованной строке
     */
    public static void showLocalizedNotification(String key, Object... args) {
        String message;
        try {
            // Пробуем получить локализованную строку
            uwu.openjfx.i18n.LocalizationManager locManager = uwu.openjfx.i18n.LocalizationManager.getInstance();
            message = locManager.getString(key);
        } catch (Exception e) {
            // Fallback если локализация не инициализирована
            message = key;
        }
        
        if (args.length > 0) {
            message = String.format(message, args);
        }
        
        // Показать простое текстовое уведомление
        javafx.scene.text.Text notification = new javafx.scene.text.Text(message);
        notification.setFill(javafx.scene.paint.Color.WHITE);
        notification.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        notification.setX(FXGL.getAppWidth() / 2 - 100);
        notification.setY(50);
        
        FXGL.getGameScene().addUINode(notification);
        
        // Анимация исчезновения
        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(
            javafx.util.Duration.seconds(2), notification);
        fade.setToValue(0);
        fade.setOnFinished(e -> FXGL.getGameScene().removeUINode(notification));
        fade.play();
    }
}