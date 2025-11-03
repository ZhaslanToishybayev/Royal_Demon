package uwu.openjfx.utils;

import uwu.openjfx.components.StatusEffectComponent;

/**
 * Фабрика для создания предустановленных статусных эффектов
 */
public class StatusEffectFactory {
    
    /**
     * Создать эффект яда
     */
    public static StatusEffectComponent.StatusEffect createPoison(double duration) {
        return new StatusEffectComponent.StatusEffect(
            StatusEffectComponent.StatusType.POISON,
            duration,
            2, // 2 урона в секунду
            0.8, // замедление на 20%
            1.0  // без модификатора атаки
        );
    }
    
    /**
     * Создать эффект ожога
     */
    public static StatusEffectComponent.StatusEffect createBurn(double duration) {
        return new StatusEffectComponent.StatusEffect(
            StatusEffectComponent.StatusType.BURN,
            duration,
            3, // 3 урона в секунду
            1.0, // без модификатора движения
            0.9  // урон снижен на 10%
        );
    }
    
    /**
     * Создать эффект заморозки
     */
    public static StatusEffectComponent.StatusEffect createFreeze(double duration) {
        return new StatusEffectComponent.StatusEffect(
            StatusEffectComponent.StatusType.FREEZE,
            duration,
            1, // 1 урон в секунду
            0.3, // замедление на 70%
            0.7  // урон снижен на 30%
        );
    }
    
    /**
     * Создать эффект оглушения
     */
    public static StatusEffectComponent.StatusEffect createStun(double duration) {
        return new StatusEffectComponent.StatusEffect(
            StatusEffectComponent.StatusType.STUN,
            duration,
            0, // нет урона
            0.0, // полная остановка
            0.0  // нет атаки
        );
    }
    
    /**
     * Создать случайный статусный эффект
     */
    public static StatusEffectComponent.StatusEffect createRandomEffect() {
        java.util.Random random = new java.util.Random();
        int choice = random.nextInt(4);
        double duration = 3.0 + random.nextDouble() * 4.0; // 3-7 секунд
        
        switch (choice) {
            case 0:
                return createPoison(duration);
            case 1:
                return createBurn(duration);
            case 2:
                return createFreeze(duration);
            default:
                return createStun(duration);
        }
    }
    
    /**
     * Создать эффект на основе типа оружия
     */
    public static StatusEffectComponent.StatusEffect createFromWeaponType(String weaponType) {
        double duration = 4.0; // Базовая длительность
        
        switch (weaponType.toLowerCase()) {
            case "poison":
            case "venom":
                return createPoison(duration);
            case "fire":
            case "flame":
                return createBurn(duration * 1.2);
            case "ice":
            case "frost":
                return createFreeze(duration * 1.1);
            case "thunder":
            case "stun":
                return createStun(duration * 0.8);
            default:
                return null;
        }
    }
}