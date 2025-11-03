package uwu.openjfx.utils;

import com.almasb.fxgl.entity.Entity;
import uwu.openjfx.components.AdaptiveAI;
import uwu.openjfx.components.EnemyComponent;

/**
 * Фабрика для создания предустановленных AI профилей
 */
public class AIFactory {
    
    /**
     * Создать AI для храброго воина
     */
    public static AdaptiveAI createBraveWarrior(Entity player) {
        return new AdaptiveAI(player, 0.9, 0.4, 0.2); // Высокая агрессия, средний интеллект
    }
    
    /**
     * Создать AI для осторожного стрелка
     */
    public static AdaptiveAI createCautiousArcher(Entity player) {
        return new AdaptiveAI(player, 0.3, 0.8, 0.8); // Низкая агрессия, высокий интеллект, высокая осторожность
    }
    
    /**
     * Создать AI для тактического мага
     */
    public static AdaptiveAI createTacticalMage(Entity player) {
        return new AdaptiveAI(player, 0.5, 0.9, 0.6); // Средняя агрессия, высокий интеллект
    }
    
    /**
     * Создать AI для дикого зверя
     */
    public static AdaptiveAI createWildBeast(Entity player) {
        return new AdaptiveAI(player, 0.8, 0.2, 0.1); // Высокая агрессия, низкий интеллект
    }
    
    /**
     * Создать AI для стражника
     */
    public static AdaptiveAI createGuard(Entity player) {
        return new AdaptiveAI(player, 0.4, 0.6, 0.7); // Низкая агрессия, средний интеллект, высокая осторожность
    }
    
    /**
     * Создать AI для ассасина
     */
    public static AdaptiveAI createAssassin(Entity player) {
        return new AdaptiveAI(player, 0.7, 0.9, 0.4); // Высокая агрессия, высокий интеллект, средняя осторожность
    }
    
    /**
     * Создать AI на основе типа врага
     */
    public static AdaptiveAI createAIForEnemyType(String enemyType, Entity player) {
        switch (enemyType.toLowerCase()) {
            case "warrior":
            case "knight":
            case "swordsman":
                return createBraveWarrior(player);
            case "archer":
            case "ranger":
            case "crossbowman":
                return createCautiousArcher(player);
            case "mage":
            case "wizard":
            case "sorcerer":
                return createTacticalMage(player);
            case "beast":
            case "wolf":
            case "bear":
            case "goblin":
                return createWildBeast(player);
            case "guard":
            case "sentry":
            case "watchman":
                return createGuard(player);
            case "assassin":
            case "rogue":
            case "ninja":
                return createAssassin(player);
            default:
                return createBraveWarrior(player); // По умолчанию
        }
    }
    
    /**
     * Получить описание AI профиля
     */
    public static String getAIDescription(String aiType) {
        switch (aiType.toLowerCase()) {
            case "brave":
                return "Храбрый воин - агрессивный в ближнем бою";
            case "cautious":
                return "Осторожный стрелок - держит дистанцию";
            case "tactical":
                return "Тактический маг - использует окружение";
            case "wild":
                return "Дикий зверь - непредсказуемые атаки";
            case "guard":
                return "Стражник - защищает территорию";
            case "assassin":
                return "Ассасин - атакует из укрытий";
            default:
                return "Стандартный враг";
        }
    }
    
    /**
     * Получить цвет для AI типа
     */
    public static javafx.scene.paint.Color getAIColor(String aiType) {
        switch (aiType.toLowerCase()) {
            case "brave":
                return javafx.scene.paint.Color.RED;
            case "cautious":
                return javafx.scene.paint.Color.BLUE;
            case "tactical":
                return javafx.scene.paint.Color.CYAN;  // Убрал PURPLE - фиолетовые артефакты
            case "wild":
                return javafx.scene.paint.Color.ORANGE;
            case "guard":
                return javafx.scene.paint.Color.GREEN;
            case "assassin":
                return javafx.scene.paint.Color.DARKGRAY;
            default:
                return javafx.scene.paint.Color.WHITE;
        }
    }
    
    /**
     * Обновить параметры AI на основе сложности
     */
    public static AdaptiveAI scaleDifficulty(AdaptiveAI baseAI, String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                baseAI.setAggressiveness(baseAI.getAggressiveness() * 0.7);
                baseAI.setIntelligence(baseAI.getIntelligence() * 0.6);
                break;
            case "normal":
                // Без изменений
                break;
            case "hard":
                baseAI.setAggressiveness(Math.min(1.0, baseAI.getAggressiveness() * 1.3));
                baseAI.setIntelligence(Math.min(1.0, baseAI.getIntelligence() * 1.2));
                baseAI.setCautiousness(baseAI.getCautiousness() * 1.1);
                break;
            case "insane":
                baseAI.setAggressiveness(1.0);
                baseAI.setIntelligence(1.0);
                baseAI.setCautiousness(0.5);
                break;
        }
        
        return baseAI;
    }
}