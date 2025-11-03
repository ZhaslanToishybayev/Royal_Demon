package uwu.openjfx.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.util.Duration;
import uwu.openjfx.components.CreatureComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Компонент для управления статусными эффектами сущности
 */
public class StatusEffectComponent extends Component {
    
    private ConcurrentHashMap<String, StatusEffect> activeEffects = new ConcurrentHashMap<>();
    private List<StatusEffectListener> listeners = new ArrayList<>();
    
    public enum StatusType {
        POISON("Яд", javafx.scene.paint.Color.GREEN, 2),
        BURN("Ожог", javafx.scene.paint.Color.ORANGE, 3),
        FREEZE("Заморозка", javafx.scene.paint.Color.LIGHTBLUE, 1),
        STUN("Оглушение", javafx.scene.paint.Color.YELLOW, 0);
        
        private final String name;
        private final javafx.scene.paint.Color color;
        private final int damagePerSecond;
        
        StatusType(String name, javafx.scene.paint.Color color, int damagePerSecond) {
            this.name = name;
            this.color = color;
            this.damagePerSecond = damagePerSecond;
        }
        
        public String getName() { return name; }
        public javafx.scene.paint.Color getColor() { return color; }
        public int getDamagePerSecond() { return damagePerSecond; }
    }
    
    /**
     * Класс представляющий отдельный статусный эффект
     */
    public static class StatusEffect {
        private final StatusType type;
        private final double duration;
        private final int damagePerSecond;
        private final double movementModifier;
        private final double attackModifier;
        private double remainingTime;
        private long lastTickTime;
        
        public StatusEffect(StatusType type, double duration, int damagePerSecond, 
                         double movementModifier, double attackModifier) {
            this.type = type;
            this.duration = duration;
            this.damagePerSecond = damagePerSecond;
            this.movementModifier = movementModifier;
            this.attackModifier = attackModifier;
            this.remainingTime = duration;
            this.lastTickTime = System.currentTimeMillis();
        }
        
        public StatusType getType() { return type; }
        public double getDuration() { return duration; }
        public double getRemainingTime() { return remainingTime; }
        public double getMovementModifier() { return movementModifier; }
        public double getAttackModifier() { return attackModifier; }
        public boolean isExpired() { return remainingTime <= 0; }
        
        public void update(double deltaTime) {
            remainingTime -= deltaTime;
            lastTickTime = System.currentTimeMillis();
        }
        
        public boolean shouldTick() {
            return System.currentTimeMillis() - lastTickTime >= 1000; // Каждую секунду
        }
    }
    
    @FunctionalInterface
    public interface StatusEffectListener {
        void onStatusEffectApplied(StatusType type, boolean isApplied);
    }
    
    /**
     * Добавить новый статусный эффект
     */
    public boolean applyStatus(StatusEffect effect) {
        String effectId = effect.getType().name();
        
        // Если эффект уже активен, обновляем его
        if (activeEffects.containsKey(effectId)) {
            StatusEffect existing = activeEffects.get(effectId);
            existing.remainingTime = Math.max(existing.remainingTime, effect.remainingTime);
            return false;
        }
        
        activeEffects.put(effectId, effect);
        
        // Уведомляем слушателей
        listeners.forEach(listener -> listener.onStatusEffectApplied(effect.getType(), true));
        
        // Создаем визуальный эффект
        createVisualEffect(effect);
        
        return true;
    }
    
    /**
     * Удалить статусный эффект
     */
    public boolean removeStatus(StatusType type) {
        StatusEffect removed = activeEffects.remove(type.name());
        if (removed != null) {
            listeners.forEach(listener -> listener.onStatusEffectApplied(type, false));
            return true;
        }
        return false;
    }
    
    /**
     * Получить все активные эффекты
     */
    public List<StatusEffect> getActiveEffects() {
        return new ArrayList<>(activeEffects.values());
    }
    
    /**
     * Проверить наличие конкретного эффекта
     */
    public boolean hasEffect(StatusType type) {
        return activeEffects.containsKey(type.name());
    }
    
    /**
     * Получить модификатор движения
     */
    public double getMovementModifier() {
        return activeEffects.values().stream()
                .mapToDouble(StatusEffect::getMovementModifier)
                .reduce(1.0, (a, b) -> a * b);
    }
    
    /**
     * Получить модификатор атаки
     */
    public double getAttackModifier() {
        return activeEffects.values().stream()
                .mapToDouble(StatusEffect::getAttackModifier)
                .reduce(1.0, (a, b) -> a * b);
    }
    
    /**
     * Получить общий урон в секунду от всех эффектов
     */
    public int getTotalDamagePerSecond() {
        return activeEffects.values().stream()
                .mapToInt(effect -> effect.getType().getDamagePerSecond())
                .sum();
    }
    
    /**
     * Добавить слушатель эффектов
     */
    public void addListener(StatusEffectListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Удалить слушатель эффектов
     */
    public void removeListener(StatusEffectListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Обновить все статусные эффекты
     */
    @Override
    public void onUpdate(double deltaTime) {
        List<String> expiredEffects = new ArrayList<>();
        
        for (String effectId : activeEffects.keySet()) {
            StatusEffect effect = activeEffects.get(effectId);
            effect.update(deltaTime);
            
            // Наносим периодический урон
            if (effect.shouldTick()) {
                applyPeriodicDamage(effect);
            }
            
            // Проверяем истечение эффекта
            if (effect.isExpired()) {
                expiredEffects.add(effectId);
                listeners.forEach(listener -> 
                    listener.onStatusEffectApplied(effect.getType(), false));
            }
        }
        
        // Удаляем истекшие эффекты
        expiredEffects.forEach(activeEffects::remove);
    }
    
    /**
     * Нанести периодический урон от эффекта
     */
    private void applyPeriodicDamage(StatusEffect effect) {
        if (effect.getType().getDamagePerSecond() > 0) {
            CreatureComponent lifeComponent = getEntity().getComponent(CreatureComponent.class);
            if (lifeComponent != null) {
                lifeComponent.deductHealth(effect.getType().getDamagePerSecond(), 
                    1, 0, 1, 0);
                
                // Создаем эффект урона
                createDamageVisual(effect);
            }
        }
    }
    
    /**
     * Создать визуальный индикатор статуса
     */
    private void createVisualEffect(StatusEffect effect) {
        // Создаем частицы над головой сущности
        javafx.scene.paint.Color color = effect.getType().getColor();
        
        // Здесь можно добавить создание системы частиц FXGL
        // Для примера - создаем простой текстовый индикатор
        createStatusIndicator(effect.getType().getName(), color);
    }
    
    /**
     * Создать визуальный эффект урона
     */
    private void createDamageVisual(StatusEffect effect) {
        // Создаем всплывающие цифры урона
        // Можно переиспользовать существующую систему DamageNumbers
    }
    
    /**
     * Создать индикатор статуса над сущностью
     */
    private void createStatusIndicator(String text, javafx.scene.paint.Color color) {
        // Создаем текстовый индикатор над сущностью
        // Позиционируем над головой сущности
        // Анимация появления и исчезновения
    }
    
    /**
     * Очистить все эффекты
     */
    public void clearAllEffects() {
        activeEffects.clear();
        listeners.forEach(listener -> 
            listener.onStatusEffectApplied(null, false));
    }
    
    @Override
    public void onAdded() {
        // Инициализация при добавлении к сущности
    }
    
    @Override
    public void onRemoved() {
        // Очистка при удалении компонента
        clearAllEffects();
        listeners.clear();
    }
}
