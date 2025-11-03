package uwu.openjfx.components;

import com.almasb.fxgl.entity.component.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Компонент для управления комбо-системой
 */
public class ComboManager extends Component {
    
    // Предустановленные комбо-цепочки
    private static final List<String[]> COMBO_PATTERNS = new ArrayList<>();
    static {
        COMBO_PATTERNS.add(new String[]{"light", "light", "heavy"});      // LLH
        COMBO_PATTERNS.add(new String[]{"light", "heavy", "light"});      // LHL
        COMBO_PATTERNS.add(new String[]{"heavy", "light", "heavy"});      // HLH
        COMBO_PATTERNS.add(new String[]{"light", "light", "light", "heavy"}); // LLLH
        COMBO_PATTERNS.add(new String[]{"heavy", "heavy", "light"});      // HHL
    }
    
    // Множители урона для каждого типа комбо
    private static final double[] COMBO_MULTIPLIERS = {1.0, 1.5, 2.0, 2.5, 3.0};
    
    private List<String> recentAttacks = new ArrayList<>();
    private long lastAttackTime = 0;
    private long comboTimeout = 2000; // 2 секунды на комбо
    private int currentComboLevel = 0;
    private double comboMultiplier = 1.0;
    private boolean isComboActive = false;
    
    // Слушатели событий комбо
    private List<ComboListener> listeners = new ArrayList<>();
    
    public interface ComboListener {
        void onComboAchieved(String comboType, int level, double multiplier);
        void onComboBreak();
    }
    
    /**
     * Зарегистрировать новую атаку для комбо-системы
     */
    public void registerAttack(String attackType) {
        long currentTime = System.currentTimeMillis();
        
        // Проверяем таймаут комбо
        if (currentTime - lastAttackTime > comboTimeout) {
            // Комбо прервано
            if (!recentAttacks.isEmpty()) {
                notifyComboBreak();
            }
            recentAttacks.clear();
            currentComboLevel = 0;
            comboMultiplier = 1.0;
            isComboActive = false;
        }
        
        // Добавляем атаку в историю
        recentAttacks.add(attackType);
        lastAttackTime = currentTime;
        
        // Ограничиваем историю последними 5 атаками
        if (recentAttacks.size() > 5) {
            recentAttacks.remove(0);
        }
        
        // Проверяем комбо-паттерны
        checkForCombo();
        
        // Обновляем множитель
        updateComboMultiplier();
    }
    
    /**
     * Проверить наличие комбо-паттернов
     */
    private void checkForCombo() {
        String[] recentAttackArray = recentAttacks.toArray(new String[0]);
        
        for (int i = 0; i < COMBO_PATTERNS.size(); i++) {
            String[] pattern = COMBO_PATTERNS.get(i);
            
            if (matchesPattern(recentAttackArray, pattern)) {
                activateCombo(i);
                break;
            }
        }
    }
    
    /**
     * Проверить совпадение паттерна с историей атак
     */
    private boolean matchesPattern(String[] history, String[] pattern) {
        if (history.length < pattern.length) {
            return false;
        }
        
        int startIndex = history.length - pattern.length;
        for (int i = 0; i < pattern.length; i++) {
            if (!pattern[i].equals(history[startIndex + i])) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Активировать найденное комбо
     */
    private void activateCombo(int patternIndex) {
        String[] pattern = COMBO_PATTERNS.get(patternIndex);
        String comboName = String.join("-", pattern);
        
        currentComboLevel = Math.min(currentComboLevel + 1, COMBO_MULTIPLIERS.length - 1);
        comboMultiplier = COMBO_MULTIPLIERS[currentComboLevel];
        isComboActive = true;
        
        // Уведомляем слушателей
        listeners.forEach(listener -> 
            listener.onComboAchieved(comboName, currentComboLevel, comboMultiplier));
        
        // Создаем визуальный эффект
        createComboVisual(patternIndex);
    }
    
    /**
     * Обновить множитель комбо
     */
    private void updateComboMultiplier() {
        if (!isComboActive) {
            return;
        }
        
        // Снижаем множитель со временем
        long timeSinceLastAttack = System.currentTimeMillis() - lastAttackTime;
        if (timeSinceLastAttack > comboTimeout / 2) {
            comboMultiplier = Math.max(1.0, comboMultiplier * 0.9);
            if (comboMultiplier <= 1.0) {
                isComboActive = false;
                currentComboLevel = 0;
            }
        }
    }
    
    /**
     * Получить текущий множитель урона от комбо
     */
    public double getDamageMultiplier() {
        return comboMultiplier;
    }
    
    /**
     * Получить уровень текущего комбо
     */
    public int getComboLevel() {
        return currentComboLevel;
    }
    
    /**
     * Активно ли комбо
     */
    public boolean isComboActive() {
        return isComboActive;
    }
    
    /**
     * Получить историю последних атак
     */
    public List<String> getRecentAttacks() {
        return new ArrayList<>(recentAttacks);
    }
    
    /**
     * Сбросить комбо-систему
     */
    public void resetCombo() {
        recentAttacks.clear();
        currentComboLevel = 0;
        comboMultiplier = 1.0;
        isComboActive = false;
        notifyComboBreak();
    }
    
    /**
     * Добавить слушатель комбо
     */
    public void addComboListener(ComboListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Удалить слушатель комбо
     */
    public void removeComboListener(ComboListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Уведомить о прерывании комбо
     */
    private void notifyComboBreak() {
        listeners.forEach(ComboListener::onComboBreak);
    }
    
    /**
     * Создать визуальный эффект комбо
     */
    private void createComboVisual(int patternIndex) {
        // Здесь можно создать визуальные эффекты для разных уровней комбо
        // Например: Flash эффект, цветные частицы, текст с множителем
        
        // Переиспользуем существующую систему DamageNumbers
        // для отображения множителя над персонажем
    }
    
    @Override
    public void onUpdate(double tpf) {
        // Обновляем множитель со временем
        updateComboMultiplier();
    }
    
    @Override
    public void onAdded() {
        // Инициализация при добавлении
    }
    
    @Override
    public void onRemoved() {
        // Очистка при удалении
        listeners.clear();
        recentAttacks.clear();
    }
}
