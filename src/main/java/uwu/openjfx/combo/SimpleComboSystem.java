package uwu.openjfx.combo;

import com.almasb.fxgl.dsl.FXGL;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import uwu.openjfx.i18n.LocalizationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Простая система комбо для учебного проекта
 */
public class SimpleComboSystem {
    private static SimpleComboSystem instance;
    private List<String> comboSequence;
    private long lastAttackTime;
    private static final long COMBO_WINDOW_MS = 1000; // 1 секунда для комбо
    private static boolean isTesting = false;
    private IntegerProperty comboCount = new SimpleIntegerProperty(0);
    
    // Определенные комбо
    private static final String TRIPLE_STRIKE = "light-heavy-heavy";
    private static final String CIRCLE_ATTACK = "heavy-light-heavy";
    private static final String RAPID_STRIKES = "light-light-heavy";
    
    private SimpleComboSystem() {
        this.comboSequence = new ArrayList<>();
        this.lastAttackTime = 0;
    }
    
    public static SimpleComboSystem getInstance() {
        if (instance == null) {
            instance = new SimpleComboSystem();
        }
        return instance;
    }
    
    public static void setTesting(boolean testing) {
        isTesting = testing;
    }
    
    /**
     * Добавить атаку в последовательность комбо
     */
    public void addAttack(String attackType) {
        long currentTime = System.currentTimeMillis();
        
        // Проверяем, находится ли атака в окне комбо
        if (currentTime - lastAttackTime < COMBO_WINDOW_MS) {
            comboSequence.add(attackType);
            
            // Ограничиваем длину последовательности
            if (comboSequence.size() > 5) {
                comboSequence.remove(0);
            }
            
            // Проверяем комбо
            checkCombo();
        } else {
            // Сбрасываем последовательность, если прошло слишком много времени
            comboSequence.clear();
            comboSequence.add(attackType);
        }
        
        lastAttackTime = currentTime;
        comboCount.set(comboSequence.size());
    }
    
    /**
     * Проверить, выполнено ли комбо
     */
    private void checkCombo() {
        if (comboSequence.size() < 3) {
            return; // Для комбо нужно минимум 3 атаки
        }
        
        String comboString = String.join("-", comboSequence);
        
        switch (comboString) {
            case TRIPLE_STRIKE:
                executeCombo("Тройной удар", 1.5f, ComboType.DAMAGE);
                break;
            case CIRCLE_ATTACK:
                executeCombo("Круговая атака", 1.3f, ComboType.AREA);
                break;
            case RAPID_STRIKES:
                executeCombo("Быстрые удары", 1.4f, ComboType.SPEED);
                break;
        }
    }
    
    /**
     * Выполнить комбо
     */
    private void executeCombo(String comboName, float multiplier, ComboType type) {
        if (FXGL.getApp() != null && !isTesting) {
            // Показать уведомление о комбо
            showComboMessage(comboName);
        }
        
        // Применить эффекты комбо
        applyComboEffects(multiplier, type);
        
        // Сбросить последовательность после выполнения комбо
        comboSequence.clear();
        comboCount.set(0);
    }
    
    /**
     * Применить эффекты комбо
     */
    private void applyComboEffects(float multiplier, ComboType type) {
        // Сохраняем множитель урона в глобальной переменной для использования в атаках
        FXGL.set("comboDamageMultiplier", multiplier);
        
        if (FXGL.getApp() != null && !isTesting) {
            // Показываем визуальный эффект
            showComboVisualEffect(type);
        }
    }
    
    /**
     * Получить текущую последовательность комбо
     */
    public List<String> getCurrentComboSequence() {
        return new ArrayList<>(comboSequence);
    }
    
    /**
     * Сбросить комбо
     */
    public void resetCombo() {
        comboSequence.clear();
        resetDamageMultiplier();
        comboCount.set(0);
    }
    
    /**
     * Получить время до сброса комбо
     */
    public long getTimeUntilReset() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastAttack = currentTime - lastAttackTime;
        return Math.max(0, COMBO_WINDOW_MS - timeSinceLastAttack);
    }
    
    /**
     * Типы комбо
     */
    /**
     * Показать сообщение о комбо
     */
    private void showComboMessage(String comboName) {
        if (FXGL.getApp() != null && !isTesting) {
            Text comboText = new Text(comboName);
            comboText.setFill(Color.GOLD);
            comboText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            comboText.setX(FXGL.getAppWidth() / 2 - 50);
            comboText.setY(FXGL.getAppHeight() / 2);
            
            FXGL.getGameScene().addUINode(comboText);
            
            // Анимация исчезновения
            javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(
                javafx.util.Duration.seconds(1.0), comboText);
            fade.setToValue(0);
            fade.setOnFinished(e -> FXGL.getGameScene().removeUINode(comboText));
            fade.play();
        }
    }
    
    /**
     * Показать визуальный эффект комбо
     */
    private void showComboVisualEffect(ComboType type) {
        if (FXGL.getApp() != null && !isTesting) {
            Color effectColor;
            
            switch (type) {
                case DAMAGE:
                    effectColor = Color.RED;
                    break;
                case AREA:
                    effectColor = Color.BLUE;
                    break;
                case SPEED:
                    effectColor = Color.GREEN;
                    break;
                default:
                    effectColor = Color.WHITE;
            }
            
            // Создаем эффект на экране
            Text effectText = new Text("💫");
            effectText.setFill(effectColor);
            effectText.setStyle("-fx-font-size: 48px;");
            effectText.setX(FXGL.getAppWidth() / 2 - 24);
            effectText.setY(FXGL.getAppHeight() / 2 - 50);
            
            FXGL.getGameScene().addUINode(effectText);
            
            // Анимация
            javafx.animation.ScaleTransition scale = new javafx.animation.ScaleTransition(
                javafx.util.Duration.seconds(0.5), effectText);
            scale.setToX(2);
            scale.setToY(2);
            
            javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(
            javafx.util.Duration.seconds(0.5), effectText);
            fade.setToValue(0);
            
            javafx.animation.ParallelTransition parallel = new javafx.animation.ParallelTransition(scale, fade);
            parallel.setOnFinished(e -> FXGL.getGameScene().removeUINode(effectText));
            parallel.play();
        }
    }
    
    /**
     * Получить множитель урона комбо
     */
    public float getDamageMultiplier() {
        return (float) FXGL.geto("comboDamageMultiplier");
    }
    
    /**
     * Сбросить множитель урона
     */
    public void resetDamageMultiplier() {
        FXGL.set("comboDamageMultiplier", 1.0f);
    }

    public IntegerProperty getComboCountProperty() {
        return comboCount;
    }

    public enum ComboType {
        DAMAGE,    // Увеличение урона
        AREA,      // Атака по области
        SPEED      // Увеличение скорости
    }
}