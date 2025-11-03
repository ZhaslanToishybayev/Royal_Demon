package uwu.openjfx.utils;

import uwu.openjfx.components.ComboManager;

/**
 * Фабрика для создания предустановленных комбо-эффектов
 */
public class ComboFactory {
    
    /**
     * Получить текстовое представление уровня комбо
     */
    public static String getComboLevelText(int level) {
        switch (level) {
            case 1: return "COMBO!";
            case 2: return "SUPER COMBO!";
            case 3: return "MEGA COMBO!";
            case 4: return "ULTRA COMBO!";
            default: return "INSANE COMBO!";
        }
    }
    
    /**
     * Получить цвет для уровня комбо
     */
    public static javafx.scene.paint.Color getComboLevelColor(int level) {
        switch (level) {
            case 0: return javafx.scene.paint.Color.WHITE;
            case 1: return javafx.scene.paint.Color.YELLOW;
            case 2: return javafx.scene.paint.Color.ORANGE;
            case 3: return javafx.scene.paint.Color.RED;
            case 4: return javafx.scene.paint.Color.ORANGE;  // Убрал MAGENTA
            default: return javafx.scene.paint.Color.RED;  // Убрал PURPLE - фиолетовые артефакты
        }
    }
    
    /**
     * Получить описание комбо-цепочки
     */
    public static String getComboDescription(String[] pattern) {
        String description = String.join(" → ", pattern);
        return "Комбо: " + description;
    }
    
    /**
     * Проверить является ли атака легкой
     */
    public static boolean isLightAttack(String attackType) {
        return "light".equals(attackType) || "sword".equals(attackType);
    }
    
    /**
     * Проверить является ли атака тяжелой
     */
    public static boolean isHeavyAttack(String attackType) {
        return "heavy".equals(attackType) || "ultimate".equals(attackType);
    }
    
    /**
     * Конвертировать тип атаки в стандартное имя
     */
    public static String normalizeAttackType(String attackType) {
        if (isLightAttack(attackType)) {
            return "light";
        } else if (isHeavyAttack(attackType)) {
            return "heavy";
        }
        return attackType.toLowerCase();
    }
}
