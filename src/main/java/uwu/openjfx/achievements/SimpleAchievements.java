package uwu.openjfx.achievements;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import uwu.openjfx.i18n.LocalizationManager;
import uwu.openjfx.utils.GameLogger;

import java.util.HashSet;
import java.util.Set;

/**
 * Простая система достижений для учебного проекта
 */
public class SimpleAchievements {
    private static SimpleAchievements instance;
    private Set<String> unlockedAchievements;
    private static boolean isTesting = false;
    
    // ID достижений
    public static final String FIRST_KILL = "first_kill";
    public static final String GOLD_COLLECTOR = "gold_collector";
    public static final String EXPLORER = "explorer";
    public static final String DRAGON_SLAYER = "dragon_slayer";
    public static final String TREASURE_HUNTER = "treasure_hunter";
    public static final String LEVEL_MASTER = "level_master";
    public static final String COMBO_MASTER = "combo_master";
    
    private SimpleAchievements() {
        // Загружаем сохраненные достижения
        loadAchievements();
    }
    
    public static SimpleAchievements getInstance() {
        if (instance == null) {
            instance = new SimpleAchievements();
        }
        return instance;
    }
    
    public static void setTesting(boolean testing) {
        isTesting = testing;
    }
    
    /**
     * Проверить и разблокировать достижения
     */
    public void checkAchievements() {
        // Первая победа
        if (getKillsCount() >= 1 && !isUnlocked(FIRST_KILL)) {
            unlockAchievement(FIRST_KILL, "Первая кровь!", "Победите первого врага", 50);
        }
        
        // Собиратель золота
        if (getGoldAmount() >= 100 && !isUnlocked(GOLD_COLLECTOR)) {
            unlockAchievement(GOLD_COLLECTOR, "Собиратель золота", "Соберите 100 монет", 50);
        }
        
        // Исследователь
        if (getExploredRoomsCount() >= 10 && !isUnlocked(EXPLORER)) {
            unlockAchievement(EXPLORER, "Исследователь", "Посетите 10 комнат", 75);
        }
        
        // Убийца драконов
        if (getKillsCount() >= 50 && !isUnlocked(DRAGON_SLAYER)) {
            unlockAchievement(DRAGON_SLAYER, "Убийца драконов", "Победите 50 врагов", 100);
        }
        
        // Охотник за сокровищами
        if (getOpenedChestsCount() >= 20 && !isUnlocked(TREASURE_HUNTER)) {
            unlockAchievement(TREASURE_HUNTER, "Охотник за сокровищами", "Откройте 20 сундуков", 100);
        }
        
        // Мастер уровней
        if (getPlayerLevel() >= 10 && !isUnlocked(LEVEL_MASTER)) {
            unlockAchievement(LEVEL_MASTER, "Мастер уровней", "Достигните 10 уровня", 150);
        }
        
        // Мастер комбо
        if (getCompletedCombosCount() >= 25 && !isUnlocked(COMBO_MASTER)) {
            unlockAchievement(COMBO_MASTER, "Мастер комбо", "Выполните 25 комбо", 125);
        }
    }
    
    /**
     * Разблокировать достижение
     */
    private void unlockAchievement(String id, String title, String description, int goldReward) {
        unlockedAchievements.add(id);
        
        // Показать уведомление
        if (FXGL.getApp() != null && !isTesting) {
            showAchievementNotification(title, description);
        }
        
        // Выдать награду
        if (goldReward > 0) {
            addGold(goldReward);
        }
        
        // Сохранить достижения
        saveAchievements();
        
        GameLogger.gameplay("Achievement unlocked: " + title);
    }
    
    /**
     * Показать уведомление о достижении
     */
    private void showAchievementNotification(String title, String description) {
        if (FXGL.getApp() != null && !isTesting) {
            // Создаем контейнер для уведомления
            javafx.scene.layout.VBox achievementBox = new javafx.scene.layout.VBox();
            achievementBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); " +
                                    "-fx-background-radius: 10; " +
                                    "-fx-padding: 15; " +
                                    "-fx-border-color: gold; " +
                                    "-fx-border-width: 2; " +
                                    "-fx-border-radius: 10;");
            
            // Заголовок
            Text titleText = new Text("🏆 " + title);
            titleText.setFill(Color.GOLD);
            titleText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            
            // Описание
            Text descText = new Text(description);
            descText.setFill(Color.WHITE);
            descText.setStyle("-fx-font-size: 14px;");
            
            achievementBox.getChildren().addAll(titleText, descText);
            
            // Позиция
            achievementBox.setTranslateX(FXGL.getAppWidth() / 2 - 150);
            achievementBox.setTranslateY(100);
            
            // Добавить на экран
            FXGL.getGameScene().addUINode(achievementBox);
            
            // Анимация появления
            achievementBox.setOpacity(0);
            achievementBox.setScaleX(0.5);
            achievementBox.setScaleY(0.5);
            
            javafx.animation.ScaleTransition scaleIn = new javafx.animation.ScaleTransition(
                javafx.util.Duration.seconds(0.3), achievementBox);
            scaleIn.setToX(1);
            scaleIn.setToY(1);
            
            javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
                javafx.util.Duration.seconds(0.3), achievementBox);
            fadeIn.setToValue(1);
            
            javafx.animation.ParallelTransition showAnimation = new javafx.animation.ParallelTransition(scaleIn, fadeIn);
            
            // Анимация исчезновения
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                javafx.util.Duration.seconds(3));
            
            javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
                javafx.util.Duration.seconds(0.5), achievementBox);
            fadeOut.setToValue(0);
            
            javafx.animation.ScaleTransition scaleOut = new javafx.animation.ScaleTransition(
                javafx.util.Duration.seconds(0.5), achievementBox);
            scaleOut.setToX(0.8);
            scaleOut.setToY(0.8);
            
            javafx.animation.SequentialTransition fullAnimation = new javafx.animation.SequentialTransition();
            fullAnimation.getChildren().addAll(
                new javafx.animation.ParallelTransition(scaleIn, fadeIn),
                pause,
                new javafx.animation.ParallelTransition(scaleOut, fadeOut)
            );
            
            fullAnimation.setOnFinished(e -> FXGL.getGameScene().removeUINode(achievementBox));
            fullAnimation.play();
        }
    }
    
    /**
     * Проверить, разблокировано ли достижение
     */
    public boolean isUnlocked(String id) {
        return unlockedAchievements.contains(id);
    }
    
    /**
     * Получить количество разблокированных достижений
     */
    public int getUnlockedCount() {
        return unlockedAchievements.size();
    }
    
    /**
     * Получить общее количество достижений
     */
    public int getTotalCount() {
        return 7; // У нас 7 достижений
    }
    
    // Вспомогательные методы для получения игровой статистики
    
    private int getKillsCount() {
        if (FXGL.getApp() != null && !isTesting) {
            try {
                return FXGL.geti("killsCount");
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }
    
    private int getGoldAmount() {
        if (FXGL.getApp() != null && !isTesting) {
            try {
                return FXGL.geti("gold");
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }
    
    private int getExploredRoomsCount() {
        if (FXGL.getApp() != null && !isTesting) {
            try {
                return FXGL.geti("exploredRooms");
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }
    
    private int getOpenedChestsCount() {
        if (FXGL.getApp() != null && !isTesting) {
            try {
                return FXGL.geti("openedChests");
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }
    
    private int getPlayerLevel() {
        if (FXGL.getApp() != null && !isTesting) {
            try {
                return FXGL.geti("playerLevel");
            } catch (Exception e) {
                return 1;
            }
        }
        return 1;
    }
    
    private int getCompletedCombosCount() {
        if (FXGL.getApp() != null && !isTesting) {
            try {
                return FXGL.geti("completedCombos");
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }
    
    private void addGold(int amount) {
        if (FXGL.getApp() != null && !isTesting) {
            int currentGold = getGoldAmount();
            FXGL.set("gold", currentGold + amount);
        }
    }
    
    /**
     * Загрузить достижения из сохранения
     */
    private void loadAchievements() {
        // В реальном проекте здесь была бы загрузка из файла
        unlockedAchievements = new HashSet<>();
        
        // Для тестов можно разблокировать несколько достижений
        // unlockedAchievements.add(FIRST_KILL);
    }
    
    /**
     * Сохранить достижения
     */
    private void saveAchievements() {
        // В реальном проекте здесь было бы сохранение в файл
        GameLogger.debug("Saving achievements: " + unlockedAchievements);
    }
    
    /**
     * Сбросить достижения (для нового запуска)
     */
    public void reset() {
        unlockedAchievements.clear();
        saveAchievements();
    }
}