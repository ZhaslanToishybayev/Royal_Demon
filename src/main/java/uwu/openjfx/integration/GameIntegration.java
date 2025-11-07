package uwu.openjfx.integration;

import com.almasb.fxgl.dsl.FXGL;
import uwu.openjfx.achievements.SimpleAchievements;
import uwu.openjfx.combo.SimpleComboSystem;
import uwu.openjfx.components.PlayerComponent;
import uwu.openjfx.i18n.EnhancedLocalizationManager;
import uwu.openjfx.progression.SimpleProgression;
import uwu.openjfx.visual.CleanDamageNumbers;
import uwu.openjfx.utils.GameLogger;

/**
 * Класс для интеграции всех новых систем в игру
 */
public class GameIntegration {
    
    private static boolean initialized = false;
    
    /**
     * Инициализировать все новые системы
     */
    public static void initialize() {
        if (initialized) {
            return; // Уже инициализировано
        }
        
        // 1. Инициализация локализации
        if (!EnhancedLocalizationManager.class.getSimpleName().contains("Test")) {
            EnhancedLocalizationManager.getInstance().setLocale(java.util.Locale.forLanguageTag("ru"));
            GameLogger.system("Локализация инициализирована");
        }
        
        // 2. Инициализация систем геймплея
        SimpleComboSystem.getInstance();
        SimpleProgression.getInstance();
        SimpleAchievements.getInstance();
        GameLogger.system("Системы геймплея инициализированы");
        
        // 3. Настройка глобальных переменных
        setupGlobalVariables();
        
        // 4. Интеграция с существующими системами
        integrateWithExistingSystems();
        
        initialized = true;
        GameLogger.info("Все системы успешно интегрированы!");
    }
    
    /**
     * Настроить глобальные переменные для новых систем
     */
    private static void setupGlobalVariables() {
        if (FXGL.getApp() != null) {
            // Переменные для системы комбо
            FXGL.set("comboDamageMultiplier", 1.0f);
            FXGL.set("completedCombos", 0);
            
            // Переменные для системы прогрессии
            FXGL.set("playerLevel", 1);
            FXGL.set("playerExperience", 0);
            
            // Переменные для достижений
            FXGL.set("killsCount", 0);
            FXGL.set("exploredRooms", 0);
            FXGL.set("openedChests", 0);
        }
    }
    
    /**
     * Интегрировать с существующими системами
     */
    private static void integrateWithExistingSystems() {
        // Интеграция с PlayerComponent
        integrateWithPlayerComponent();
        
        // Интеграция с EnemyComponent
        integrateWithEnemyComponent();
        
        // Интеграция с UI
        integrateWithUI();
    }
    
    /**
     * Интеграция с PlayerComponent
     */
    private static void integrateWithPlayerComponent() {
        if (FXGL.getApp() != null) {
            // При атаке игрока вызывать систему комбо
            FXGL.getGameTimer().runAtInterval(() -> {
                // Проверяем, атакует ли игрок
                if (FXGL.getApp() != null) {
                    try {
                        boolean isAttacking = FXGL.getb("playerIsAttacking");
                        if (isAttacking) {
                            String attackType = FXGL.gets("playerAttackType");
                            SimpleComboSystem.getInstance().addAttack(attackType);
                        }
                    } catch (Exception e) {
                        // Игнорируем ошибки, если переменные не установлены
                    }
                }
            }, javafx.util.Duration.seconds(0.1));
        }
    }
    
    /**
     * Интеграция с EnemyComponent
     */
    private static void integrateWithEnemyComponent() {
        // Простая интеграция без использования EventBus
        // Вместо этого будем вызывать методы напрямую из других систем
    }
    
    /**
     * Вызвать при победе над врагом
     */
    public static void onEnemyDefeated(int damage, int experience,
                                      javafx.geometry.Point2D position, boolean critical) {
        try {
            if (FXGL.getApp() != null) {
                // Увеличиваем счетчик убийств
                try {
                    int currentKills = FXGL.geti("killsCount");
                    FXGL.set("killsCount", currentKills + 1);
                } catch (Exception e) {
                    FXGL.set("killsCount", 1);
                }
            }

            // Добавляем монеты при убийстве врага
            int goldReward = calculateGoldReward(damage, experience);
            PlayerComponent.addGold(goldReward);
            System.out.println("💰 Получено " + goldReward + " монет за убийство врага!");

            // Добавляем опыт
            try {
                System.out.println("⚔️ Добавляем опыт за врага: " + experience);
                SimpleProgression.getInstance().addExperience(experience);
            } catch (Exception e) {
                // Игнорируем ошибки, если система прогрессии не инициализирована
                System.err.println("❌ Ошибка при добавлении опыта: " + e.getMessage());
            }

            // Показываем числа урона
            try {
                CleanDamageNumbers.showDamageNumber(damage, position, critical);
            } catch (Exception e) {
                // Игнорируем ошибки показа урона
            }

            // Проверяем достижения
            try {
                SimpleAchievements.getInstance().checkAchievements();
            } catch (Exception e) {
                // Игнорируем ошибки системы достижений
            }
        } catch (Exception e) {
            System.err.println("❌ КРИТИЧЕСКАЯ ОШИБКА в onEnemyDefeated: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Вычислить награду в монетах за убийство врага
     */
    private static int calculateGoldReward(int damage, int experience) {
        // Базовое количество монет = опыт / 5 (минимум 5, максимум 50)
        int baseGold = Math.max(5, Math.min(50, experience / 5));

        // Бонус за урон (каждые 10 урона = +1 монета)
        int damageBonus = damage / 10;

        // Случайный бонус от 0 до 5
        int randomBonus = (int) (Math.random() * 6);

        int totalGold = baseGold + damageBonus + randomBonus;

        System.out.println("💰 Награда: " + totalGold + " монет (база:" + baseGold +
                          ", урон:" + damageBonus + ", случайный:" + randomBonus + ")");

        return totalGold;
    }
    
    /**
     * Интеграция с UI
     */
    private static void integrateWithUI() {
        if (FXGL.getApp() != null) {
            // Обновление UI при изменении прогресса
            SimpleProgression.getInstance().levelProperty().addListener((obs, oldVal, newVal) -> {
                // Здесь можно обновить UI элементы
                updateLevelDisplay(newVal.intValue());
            });
        }
    }
    
    /**
     * Обновить отображение уровня
     */
    private static void updateLevelDisplay(int level) {
        // Код для обновления UI элемента уровня
        GameLogger.gameplay("Level updated to: " + level);
    }
    
    /**
     * Показать число урона (удобный метод)
     */
    public static void showDamage(int damage, javafx.geometry.Point2D position, boolean critical) {
        CleanDamageNumbers.showDamageNumber(damage, position, critical);
    }
    
    /**
     * Добавить опыт игроку
     */
    public static void addExperience(int exp) {
        SimpleProgression.getInstance().addExperience(exp);
    }
    
    /**
     * Выполнить комбо атаку
     */
    public static void performComboAttack(String attackType) {
        SimpleComboSystem.getInstance().addAttack(attackType);
    }
    
    /**
     * Проверить достижения
     */
    public static void checkAchievements() {
        SimpleAchievements.getInstance().checkAchievements();
    }
    
    /**
     * Получить локализованную строку
     */
    public static String getLocalizedString(String key) {
        return EnhancedLocalizationManager.getInstance().getString(key);
    }

    /**
     * Изменить язык игры
     */
    public static void setLanguage(String languageTag) {
        java.util.Locale locale = java.util.Locale.forLanguageTag(languageTag);
        EnhancedLocalizationManager.getInstance().setLocale(locale);
        GameLogger.system("Язык изменен на: " + languageTag);
    }

    /**
     * Получить текущий язык
     */
    public static java.util.Locale getCurrentLanguage() {
        return EnhancedLocalizationManager.getInstance().getCurrentLocale();
    }
    
    /**
     * Получить множитель урона комбо
     */
    public static float getComboDamageMultiplier() {
        return SimpleComboSystem.getInstance().getDamageMultiplier();
    }
    
    /**
     * Сбросить множитель комбо
     */
    public static void resetCombo() {
        SimpleComboSystem.getInstance().resetCombo();
        SimpleComboSystem.getInstance().resetDamageMultiplier();
    }
}