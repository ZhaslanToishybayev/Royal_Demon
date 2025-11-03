package uwu.openjfx.utils;

import com.almasb.fxgl.entity.Entity;
import uwu.openjfx.components.StatusEffectComponent;
import uwu.openjfx.components.CreatureComponent;
import uwu.openjfx.utils.GameLogger;

/**
 * Утилитарный класс для работы с статусными эффектами
 */
public class StatusEffectUtils {
    
    /**
     * Применить случайный статусный эффект к цели
     */
    public static void applyRandomStatus(Entity target, double duration) {
        StatusEffectComponent statusComponent = target.getComponent(StatusEffectComponent.class);
        if (statusComponent != null) {
            StatusEffectComponent.StatusEffect effect = StatusEffectFactory.createRandomEffect();
            statusComponent.applyStatus(effect);
        }
    }
    
    /**
     * Применить ядовитый эффект
     */
    public static void applyPoison(Entity target, double duration) {
        applyStatusEffect(target, StatusEffectFactory.createPoison(duration));
    }
    
    /**
     * Применить эффект ожога
     */
    public static void applyBurn(Entity target, double duration) {
        applyStatusEffect(target, StatusEffectFactory.createBurn(duration));
    }
    
    /**
     * Применить эффект заморозки
     */
    public static void applyFreeze(Entity target, double duration) {
        applyStatusEffect(target, StatusEffectFactory.createFreeze(duration));
    }
    
    /**
     * Применить эффект оглушения
     */
    public static void applyStun(Entity target, double duration) {
        applyStatusEffect(target, StatusEffectFactory.createStun(duration));
    }
    
    /**
     * Применить эффект на основе типа оружия
     */
    public static void applyFromWeapon(Entity target, String weaponType, double duration) {
        StatusEffectComponent.StatusEffect effect = StatusEffectFactory.createFromWeaponType(weaponType);
        if (effect != null) {
            applyStatusEffect(target, effect);
        }
    }
    
    /**
     * Безопасное применение статусного эффекта
     */
    private static void applyStatusEffect(Entity target, StatusEffectComponent.StatusEffect effect) {
        StatusEffectComponent statusComponent = target.getComponent(StatusEffectComponent.class);
        if (statusComponent != null) {
            statusComponent.applyStatus(effect);
            
            // Логируем применение эффекта
            GameLogger.gameplay("Applied " + effect.getType().getName() + " to entity");
        }
    }
    
    /**
     * Проверить имеет ли сущность конкретный статус
     */
    public static boolean hasStatus(Entity target, StatusEffectComponent.StatusType type) {
        StatusEffectComponent statusComponent = target.getComponent(StatusEffectComponent.class);
        return statusComponent != null && statusComponent.hasEffect(type);
    }
    
    /**
     * Получить список всех активных эффектов
     */
    public static java.util.List<StatusEffectComponent.StatusEffect> getActiveEffects(Entity target) {
        StatusEffectComponent statusComponent = target.getComponent(StatusEffectComponent.class);
        return statusComponent != null ? statusComponent.getActiveEffects() : 
            new java.util.ArrayList<>();
    }
    
    /**
     * Удалить все статусные эффекты
     */
    public static void clearAllEffects(Entity target) {
        StatusEffectComponent statusComponent = target.getComponent(StatusEffectComponent.class);
        if (statusComponent != null) {
            statusComponent.clearAllEffects();
        }
    }
    
    /**
     * Применить периодический урон от всех эффектов
     */
    public static void processPeriodicDamage(Entity target) {
        StatusEffectComponent statusComponent = target.getComponent(StatusEffectComponent.class);
        if (statusComponent != null) {
            int totalDamage = statusComponent.getTotalDamagePerSecond();
            if (totalDamage > 0) {
                CreatureComponent lifeComponent = target.getComponent(CreatureComponent.class);
                if (lifeComponent != null) {
                    lifeComponent.deductHealth(totalDamage, 0, 0, 1, 0);
                }
            }
        }
    }
}
