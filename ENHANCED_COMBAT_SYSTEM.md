# Улучшенная боевая система для Royal Demons

## Core Combat Mechanics

### 1. Комбо-система

```java
public class ComboSystem {
    private List<AttackMove> currentCombo = new ArrayList<>();
    private int comboTimer = 0;
    private int maxComboTime = 120; // frames
    
    public enum AttackMove {
        LIGHT_ATTACK, HEAVY_ATTACK, MAGIC_CAST, DODGE, PARRY
    }
    
    public void addAttack(AttackMove move) {
        currentCombo.add(move);
        comboTimer = maxComboTime;
        checkComboCompletion();
    }
    
    private void checkComboCompletion() {
        // Проверка известных комбо
        if (matchesPattern(LIGHT_ATTACK, LIGHT_ATTACK, HEAVY_ATTACK)) {
            executeCombo("Triple Strike", 
                DamageType.PHYSICAL, 
                2.5f, // multiplier
                Effects.KNOCKBACK);
        }
    }
}
```

### 2. Система элементальных синергий

```java
public class ElementalSystem {
    public enum Element {
        FIRE, ICE, LIGHTNING, POISON, HOLY, SHADOW
    }
    
    public static class ElementalCombo {
        private Element primary;
        private Element secondary;
        private Effect result;
        
        public static ElementalCombo create(Element elem1, Element elem2) {
            // FIRE + ICE = Steam (blind effect)
            // LIGHTNING + POISON = Chain reaction
            // HOLY + SHADOW = Void explosion
        }
    }
}
```

### 3. Динамическая сложность врагов

```java
public class AdaptiveEnemyAI {
    private PlayerBehaviorAnalyzer analyzer;
    private DifficultyAdjuster adjuster;
    
    public void analyzePlayerPattern(PlayerAction action) {
        analyzer.recordAction(action);
        if (analyzer.getSampleSize() > 50) {
            adjustEnemyBehavior();
        }
    }
    
    private void adjustEnemyBehavior() {
        PlayerStyle style = analyzer.determinePlayStyle();
        if (style == PlayerStyle.AGGRESSIVE) {
            // Враги становятся более оборонительными
            increaseBlockChance();
            addCounterAttacks();
        }
    }
}
```

## Улучшенная система оружия

### Процедурная генерация оружия

```java
public class ProceduralWeaponGenerator {
    private Random random = new Random();
    private List<WeaponPrefix> prefixes = loadPrefixes();
    private List<WeaponSuffix> suffixes = loadSuffixes();
    
    public Weapon generateWeapon(int playerLevel, WeaponType baseType) {
        Weapon weapon = new Weapon(baseType);
        
        // Базовые статистики зависят от уровня
        int baseDamage = calculateBaseDamage(playerLevel, baseType);
        weapon.setBaseDamage(baseDamage);
        
        // Добавляем префиксы (1-2)
        int prefixCount = random.nextInt(2) + 1;
        for (int i = 0; i < prefixCount; i++) {
            WeaponPrefix prefix = selectRandomPrefix();
            weapon.addPrefix(prefix);
        }
        
        // Добавляем суффикс (0-1)
        if (random.nextFloat() < 0.7f) {
            WeaponSuffix suffix = selectRandomSuffix();
            weapon.addSuffix(suffix);
        }
        
        // Редкость влияет на количество аффиксов
        Rarity rarity = calculateRarity(playerLevel);
        weapon.setRarity(rarity);
        
        return weapon;
    }
    
    private int calculateBaseDamage(int level, WeaponType type) {
        return (int)(type.getBaseDamage() * (1 + level * 0.15));
    }
}
```

### Уникальные свойства оружия

```java
public class WeaponProperties {
    // Примеры уникальных свойств
    public static final Property LIFE_STEAL = new Property(
        "Life Steal", "Восстанавливает здоровье при ударе", 0.05f);
    
    public static final Property CHAIN_LIGHTNING = new Property(
        "Chain Lightning", "Удар молнии перескакивает на 3 цели", 0.2f);
    
    public static final Property TIME_WARP = new Property(
        "Time Warp", "Шанс замедлить время при критическом ударе", 0.1f);
    
    public static final Property SOUL_COLLECTOR = new Property(
        "Soul Collector", "Поглощает души для усиления", 1.0f);
}
```

## Расширенная система способностей

### Система душ

```java
public class SoulSystem {
    private Map<SoulType, Integer> collectedSouls = new HashMap<>();
    private List<SoulAbility> unlockedAbilities = new ArrayList<>();
    
    public enum SoulType {
        WARRIOR, MAGE, ROGUE, PRIEST, DEMON, ANGEL
    }
    
    public void absorbSoul(Enemy enemy) {
        SoulType type = determineSoulType(enemy);
        collectedSouls.merge(type, 1, Integer::sum);
        
        // Проверка разблокировки новых способностей
        checkAbilityUnlocks();
    }
    
    private void checkAbilityUnlocks() {
        for (SoulAbility ability : SoulAbility.getAll()) {
            if (!unlockedAbilities.contains(ability) && 
                ability.canUnlock(collectedSouls)) {
                unlockAbility(ability);
            }
        }
    }
}
```

### Ультимейт способности

```java
public class UltimateAbility {
    private String name;
    private int cooldown;
    private int duration;
    private List<Effect> effects;
    private ResourceCost cost;
    
    public void activate(Player player) {
        if (canActivate(player)) {
            consumeResources(player);
            startCooldown();
            applyEffects(player);
            
            // Визуальные эффекты
            VisualEffectsSystem.getInstance().playUltimateAnimation(this);
        }
    }
    
    // Примеры ультимейтов
    public static UltimateAbility createBerserkerRage() {
        return new UltimateAbility()
            .name("Berserker Rage")
            .cooldown(180) // 3 минуты
            .duration(30)  // 30 секунд
            .addEffect(Effects.DAMAGE_BOOST, 2.0f)
            .addEffect(Effects.SPEED_BOOST, 1.5f)
            .addEffect(Effects.DEFENSE_REDUCTION, 0.5f);
    }
}
```

## Улучшенная система уклонения и защиты

### Active Defense System

```java
public class ActiveDefenseSystem {
    private boolean isParrying = false;
    private int parryWindow = 8; // frames
    private int dodgeCooldown = 0;
    
    public boolean attemptParry(Attack incomingAttack) {
        if (isParrying && isInParryWindow()) {
            executePerfectParry(incomingAttack);
            return true;
        }
        return false;
    }
    
    private void executePerfectParry(Attack attack) {
        // Возвращаем часть урона атакующему
        attack.getAttacker().takeDamage(attack.getDamage() * 0.3f);
        
        // Замедляем время для контратаки
        TimeSystem.getInstance().slowTime(0.3f, 60); // 30% speed for 1 second
        
        // Визуальные эффекты
        VisualEffects.createParrySpark(attack.getPosition());
    }
    
    public boolean attemptDodge(Vector2 direction) {
        if (dodgeCooldown <= 0) {
            executeDodge(direction);
            dodgeCooldown = 30; // 0.5 seconds at 60 FPS
            return true;
        }
        return false;
    }
    
    private void executeDodge(Vector2 direction) {
        // Неуязвимость во время уклонения
        player.setInvulnerable(true);
        
        // Быстрое перемещение
        Vector2 dodgeVelocity = direction.normalize().multiply(800);
        player.setVelocity(dodgeVelocity);
        
        // Визуальный след
        VisualEffects.createDodgeTrail(player.getPosition());
        
        // Завершение уклонения
        Timer.schedule(() -> {
            player.setInvulnerable(false);
            player.setVelocity(Vector2.ZERO);
        }, 15); // 0.25 seconds
    }
}
```

## Система контр-атак и реакций

```java
public class CounterAttackSystem {
    private List<CounterMove> counterMoves = new ArrayList<>();
    
    public void registerCounterTrigger(AttackType attackType, CounterResponse response) {
        counterMoves.add(new CounterMove(attackType, response));
    }
    
    public boolean checkForCounter(Attack incomingAttack) {
        for (CounterMove counter : counterMoves) {
            if (counter.getTriggerType() == incomingAttack.getType() && 
                counter.canExecute()) {
                counter.execute();
                return true;
            }
        }
        return false;
    }
    
    // Примеры контр-атак
    public void setupDefaultCounters() {
        // Контр на магическую атаку - магический щит
        registerCounterTrigger(AttackType.MAGIC, new MagicShieldResponse());
        
        // Контр на атаку ближнего боя - уклонение и удар
        registerCounterTrigger(AttackType.MELEE, new DodgeAndStrikeResponse());
        
        // Контр на дальнюю атаку - отражение
        registerCounterTrigger(AttackType.RANGED, new ReflectProjectileResponse());
    }
}
```

## Интеграция с визуальной системой

### Динамическая визуальная обратная связь

```java
public class CombatVisualFeedback {
    public void showDamageIndicator(DamageInfo damage, Vector2 position) {
        // Цвет зависит от типа урона
        Color damageColor = getDamageTypeColor(damage.getType());
        
        // Размер зависит от количества урона
        float fontSize = Math.min(48, 16 + damage.getAmount() * 0.1f);
        
        // Анимация зависит от критичности
        AnimationType animation = damage.isCritical() ? 
            AnimationType.CRITICAL_HIT : AnimationType.NORMAL_HIT;
            
        // Создаем и играем анимацию
        DamageNumber damageNumber = new DamageNumber(damage.getAmount(), damageColor, fontSize);
        damageNumber.animate(position, animation);
    }
    
    public void createWeaponTrail(Weapon weapon, Vector2 start, Vector2 end) {
        // След от оружия зависит от его типа и элемента
        TrailEffect trail = new TrailEffect()
            .setStartPoint(start)
            .setEndPoint(end)
            .setColor(weapon.getElementalColor())
            .setDuration(0.3f)
            .setWidth(weapon.getTrailWidth());
            
        VisualEffectsSystem.getInstance().addEffect(trail);
    }
}
```

Эта улучшенная боевая система делает бои более динамичными, тактическими и визуально впечатляющими, сохраняя при этом глубину и сложность для опытных игроков.