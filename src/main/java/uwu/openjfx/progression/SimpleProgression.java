package uwu.openjfx.progression;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import uwu.openjfx.components.PlayerComponent;
import uwu.openjfx.utils.GameLogger;

/**
 * Простая система прогрессии игрока (уровни, опыт)
 */
public class SimpleProgression {
    private static SimpleProgression instance;
    private IntegerProperty level;
    private IntegerProperty experience;
    private static boolean isTesting = false;

    private SimpleProgression() {
        this.level = new SimpleIntegerProperty(1);
        this.experience = new SimpleIntegerProperty(0);
        if (FXGL.getApp() != null && !isTesting) {
            // Привязка к глобальным переменным FXGL
            level.addListener((obs, oldVal, newVal) -> FXGL.set("playerLevel", newVal.intValue()));
            experience.addListener((obs, oldVal, newVal) -> FXGL.set("playerExperience", newVal.intValue()));
        }
    }

    public static SimpleProgression getInstance() {
        if (instance == null) {
            instance = new SimpleProgression();
        }
        return instance;
    }

    public static void setTesting(boolean testing) {
        isTesting = testing;
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public int getLevel() {
        return level.get();
    }

    public IntegerProperty experienceProperty() {
        return experience;
    }

    public int getExperience() {
        return experience.get();
    }

    public void addExperience(int amount) {
        experience.set(experience.get() + amount);
        checkLevelUp();
    }

    private void checkLevelUp() {
        int currentLevel = level.get();
        int currentExperience = experience.get();

        // Улучшенная формула: требуется больше опыта на каждом уровне
        int experienceNeeded = getExperienceNeededForLevel(currentLevel + 1);

        if (currentExperience >= experienceNeeded) {
            level.set(currentLevel + 1);
            experience.set(0); // Сброс опыта при повышении уровня

            if (FXGL.getApp() != null && !isTesting) {
                FXGL.play("level_up.wav"); // Звук повышения уровня
            }

            GameLogger.gameplay("Player leveled up to: " + level.get());
            applyLevelUpBonus();
        }
    }

    /**
     * Calculate experience needed for a specific level
     * Uses exponential scaling for balanced progression
     */
    private int getExperienceNeededForLevel(int targetLevel) {
        // Exponential growth: 100, 250, 450, 700, 1000, 1350, 1750, 2200, 2700, 3250...
        return (targetLevel - 1) * 100 + (targetLevel - 1) * (targetLevel - 1) * 50;
    }

    /**
     * Apply bonuses when player levels up
     */
    private void applyLevelUpBonus() {
        if (FXGL.getApp() != null && !isTesting) {
            // Auto-heal player on level up
            Entity player = FXGL.geto("player");
            if (player != null && player.hasComponent(PlayerComponent.class)) {
                PlayerComponent playerComp = player.getComponent(PlayerComponent.class);
                playerComp.increaseHealth(10); // Heal 10 HP (2 сердца) on level up
            }
        }
    }
}