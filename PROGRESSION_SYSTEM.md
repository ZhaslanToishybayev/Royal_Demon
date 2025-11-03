# Система прогрессивного развития для Royal Demons

## Многомерная система прогрессии

### 1. Базовая система уровней и опыта

```java
public class PlayerProgression {
    private int level;
    private int experience;
    private int skillPoints;
    private Map<Attribute, Integer> attributes = new HashMap<>();
    
    public enum Attribute {
        STRENGTH, DEXTERITY, INTELLIGENCE, VITALITY, WISDOM, LUCK
    }
    
    public void addExperience(int exp) {
        experience += exp;
        while (canLevelUp()) {
            levelUp();
        }
    }
    
    private void levelUp() {
        level++;
        skillPoints += 2;
        
        // Автоматическое увеличение атрибутов
        attributes.merge(Attribute.STRENGTH, 2, Integer::sum);
        attributes.merge(Attribute.VITALITY, 2, Integer::sum);
        
        // Восстановление здоровья при повышении уровня
        player.healToFull();
        
        // Визуальные эффекты
        VisualEffects.playLevelUpAnimation(player.getPosition());
        
        // Уведомление о новых возможностях
        showLevelUpRewards();
    }
    
    private boolean canLevelUp() {
        int requiredExp = calculateRequiredExperience(level);
        return experience >= requiredExp;
    }
    
    private int calculateRequiredExperience(int level) {
        // Экспоненциальная кривая с плато на высоких уровнях
        return (int)(100 * Math.pow(1.15, level) + level * 50);
    }
}
```

### 2. Система навыков оружия

```java
public class WeaponSkillSystem {
    private Map<WeaponType, WeaponSkill> weaponSkills = new HashMap<>();
    
    public class WeaponSkill {
        private WeaponType type;
        private int level;
        private int experience;
        private List<SkillUnlock> unlocks = new ArrayList<>();
        
        public void addExperience(int exp) {
            experience += exp;
            while (canLevelUp()) {
                levelUp();
            }
        }
        
        private void levelUp() {
            level++;
            checkUnlocks();
            
            // Бонусы к оружию
            updateWeaponBonuses();
        }
        
        private void checkUnlocks() {
            for (SkillUnlock unlock : unlocks) {
                if (!unlock.isUnlocked() && unlock.getRequiredLevel() <= level) {
                    unlock.unlock();
                    NotificationSystem.showSkillUnlock(unlock);
                }
            }
        }
    }
    
    // Примеры разблокировок навыков
    public void setupSkillUnlocks() {
        WeaponSkill swordSkill = weaponSkills.get(WeaponType.SWORD);
        swordSkill.addUnlock(new SkillUnlock(5, "Spin Attack", 
            "Вращательное атака поражает всех вокруг"));
        swordSkill.addUnlock(new SkillUnlock(10, "Counter Strike", 
            "Шанс контратаки при блокировании"));
        swordSkill.addUnlock(new SkillUnlock(15, "Blade Dance", 
            "Серия быстрых атак с повышенным критом"));
    }
}
```

### 3. Система магических дисциплин

```java
public class MagicDisciplineSystem {
    private Map<Discipline, DisciplineLevel> disciplines = new HashMap<>();
    
    public enum Discipline {
        ELEMENTALISM, NECROMANCY, HOLY_MAGIC, CHRONOMANCY, ILLUSION
    }
    
    public class DisciplineLevel {
        private Discipline discipline;
        private int level;
        private List<Spell> knownSpells = new ArrayList<>();
        private Map<Spell, Integer> spellMastery = new HashMap<>();
        
        public void learnSpell(Spell spell) {
            if (canLearnSpell(spell)) {
                knownSpells.add(spell);
                spellMastery.put(spell, 1); // Базовое владение
                NotificationSystem.showSpellLearned(spell);
            }
        }
        
        public void practiceSpell(Spell spell) {
            if (spellMastery.containsKey(spell)) {
                int currentMastery = spellMastery.get(spell);
                if (currentMastery < 100 && Math.random() < 0.1f) {
                    spellMastery.put(spell, currentMastery + 1);
                    
                    // Проверка повышения мастерства
                    if (currentMastery % 20 == 0) {
                        upgradeSpellEfficiency(spell);
                    }
                }
            }
        }
        
        private void upgradeSpellEfficiency(Spell spell) {
            // Увеличиваем эффективность заклинания
            spell.setDamageMultiplier(spell.getDamageMultiplier() * 1.1f);
            spell.setManaCost(spell.getManaCost() * 0.95f);
        }
    }
}
```

### 4. Система душ и способностей

```java
public class SoulAbilitySystem {
    private Map<SoulType, Integer> absorbedSouls = new HashMap<>();
    private List<SoulAbility> activeAbilities = new ArrayList<>();
    private SoulTree soulTree = new SoulTree();
    
    public enum SoulType {
        WARRIOR_SOUL, MAGE_SOUL, ROGUE_SOUL, PRIEST_SOUL, 
        DEMON_SOUL, ANGEL_SOUL, ANCIENT_SOUL, PRIMAL_SOUL
    }
    
    public void absorbSoul(Enemy enemy) {
        SoulType soulType = determineSoulType(enemy);
        int soulPower = calculateSoulPower(enemy);
        
        absorbedSouls.merge(soulType, soulPower, Integer::sum);
        
        // Проверка разблокировки новых способностей
        checkSoulTreeUnlocks();
        
        // Визуальный эффект поглощения души
        VisualEffects.playSoulAbsorption(enemy.getPosition(), soulType);
    }
    
    private void checkSoulTreeUnlocks() {
        for (SoulNode node : soulTree.getAvailableNodes()) {
            if (canUnlockNode(node)) {
                unlockNode(node);
            }
        }
    }
    
    private boolean canUnlockNode(SoulNode node) {
        Map<SoulType, Integer> requirements = node.getSoulRequirements();
        
        for (Map.Entry<SoulType, Integer> requirement : requirements.entrySet()) {
            int currentSouls = absorbedSouls.getOrDefault(requirement.getKey(), 0);
            if (currentSouls < requirement.getValue()) {
                return false;
            }
        }
        return true;
    }
    
    private void unlockNode(SoulNode node) {
        node.unlock();
        activeAbilities.add(node.getAbility());
        NotificationSystem.showSoulAbilityUnlocked(node.getAbility());
    }
}
```

## Дерево талантов

### Гибкая система талантов

```java
public class TalentTreeSystem {
    private Map<TalentNode, TalentState> talents = new HashMap<>();
    private int availableTalentPoints;
    private int resetTokens = 3; // Количество бесплатных сбросов
    
    public enum TalentState {
        LOCKED, AVAILABLE, UNLOCKED, MAXED
    }
    
    public class TalentNode {
        private String id;
        private String name;
        private String description;
        private int maxRank;
        private int currentRank = 0;
        private List<TalentNode> prerequisites = new ArrayList<>();
        private List<TalentEffect> effects = new ArrayList<>();
        
        public boolean canUnlock() {
            // Проверка предварительных талантов
            for (TalentNode prereq : prerequisites) {
                if (talents.get(prereq) != TalentState.UNLOCKED) {
                    return false;
                }
            }
            return true;
        }
        
        public void unlock() {
            if (canUnlock() && currentRank < maxRank && availableTalentPoints > 0) {
                currentRank++;
                availableTalentPoints--;
                
                // Применение эффектов таланта
                applyEffects();
                
                // Обновление состояния
                updateTalentState();
                
                // Проверка разблокировки следующих талантов
                checkDependentTalents();
            }
        }
        
        private void applyEffects() {
            for (TalentEffect effect : effects) {
                effect.apply(currentRank);
            }
        }
    }
    
    // Примеры талантов
    public void setupTalentTree() {
        // Ветвь Воина
        TalentNode increasedDamage = new TalentNode()
            .name("Increased Damage")
            .description("+5% к урону оружием ближнего боя")
            .maxRank(5)
            .addEffect(new DamageEffect(0.05f));
            
        TalentNode criticalStrike = new TalentNode()
            .name("Critical Strike")
            .description("+3% шанс критического удара")
            .maxRank(5)
            .addPrerequisite(increasedDamage)
            .addEffect(new CriticalChanceEffect(0.03f));
            
        // Ветвь Мага
        TalentNode manaEfficiency = new TalentNode()
            .name("Mana Efficiency")
            .description("-10% стоимость маны для заклинаний")
            .maxRank(3)
            .addEffect(new ManaCostReductionEffect(0.1f));
    }
}
```

## Система ремесел и создания предметов

### Продвинутый крафтинг

```java
public class CraftingSystem {
    private Map<Recipe, Integer> learnedRecipes = new HashMap<>();
    private Map<Material, Integer> materials = new HashMap<>();
    private int craftingLevel = 1;
    private int craftingExperience = 0;
    
    public class Recipe {
        private String name;
        private Map<Material, Integer> requiredMaterials;
        private List<Item> possibleResults;
        private int difficulty;
        private CraftingCategory category;
        
        public Item craft() {
            if (canCraft()) {
                consumeMaterials();
                
                // Качество создания зависит от уровня крафта
                float qualityRoll = calculateQuality();
                Item result = selectResult(qualityRoll);
                
                // Опыт крафта
                addCraftingExperience(difficulty * 10);
                
                return result;
            }
            return null;
        }
        
        private float calculateQuality() {
            float baseChance = 0.3f + (craftingLevel * 0.05f);
            float randomRoll = Math.random();
            
            if (randomRoll < baseChance * 0.1f) return 1.5f; // Exceptional
            if (randomRoll < baseChance * 0.3f) return 1.25f; // Great
            if (randomRoll < baseChance) return 1.0f; // Normal
            return 0.75f; // Poor
        }
    }
    
    // Примеры рецептов
    public void setupBasicRecipes() {
        Recipe healthPotion = new Recipe()
            .name("Health Potion")
            .addMaterial(Material.HERB, 3)
            .addMaterial(Material.WATER, 2)
            .addResult(Item.HEALTH_POTION)
            .setDifficulty(1)
            .setCategory(CraftingCategory.ALCHEMY);
            
        Recipe enchantedSword = new Recipe()
            .name("Enchanted Iron Sword")
            .addMaterial(Material.IRON_INGOT, 5)
            .addMaterial(Material.MAGIC_DUST, 2)
            .addResult(Item.ENCHANTED_SWORD)
            .setDifficulty(15)
            .setCategory(CraftingCategory.SMITHING);
    }
}
```

## Система достижений и наград

### Геймификация прогрессии

```java
public class AchievementSystem {
    private Map<Achievement, AchievementStatus> achievements = new HashMap<>();
    private List<Achievement> completedAchievements = new ArrayList<>();
    private int achievementPoints = 0;
    
    public class Achievement {
        private String id;
        private String name;
        private String description;
        private AchievementType type;
        private List<AchievementCondition> conditions = new ArrayList<>();
        private List<Reward> rewards = new ArrayList<>();
        private boolean hidden = false;
        
        public boolean checkCompletion() {
            for (AchievementCondition condition : conditions) {
                if (!condition.isSatisfied()) {
                    return false;
                }
            }
            return true;
        }
        
        public void complete() {
            if (!completedAchievements.contains(this)) {
                completedAchievements.add(this);
                
                // Выдача наград
                for (Reward reward : rewards) {
                    reward.grant();
                }
                
                // Очки достижений
                achievementPoints += getPoints();
                
                // Уведомление
                NotificationSystem.showAchievementCompleted(this);
                
                // Проверка зависимых достижений
                checkDependentAchievements();
            }
        }
    }
    
    // Примеры достижений
    public void setupAchievements() {
        Achievement firstKill = new Achievement()
            .id("first_kill")
            .name("First Blood")
            .description("Убейте первого врага")
            .addCondition(new KillCountCondition(1))
            .addReward(new ExperienceReward(50))
            .addReward(new TitleReward("Novice"));
            
        Achievement weaponMaster = new Achievement()
            .id("weapon_master")
            .name("Weapon Master")
            .description("Достигните максимального уровня с каждым типом оружия")
            .addCondition(new WeaponMasteryCondition(WeaponType.SWORD, 10))
            .addCondition(new WeaponMasteryCondition(WeaponType.BOW, 10))
            .addCondition(new WeaponMasteryCondition(WeaponType.STAFF, 10))
            .addReward(new UniqueWeaponReward("Master's Blade"))
            .addReward(new AchievementPointsReward(100));
    }
}
```

## Метапрогрессия между прохождениями

### Система наследия

```java
public class MetaProgressionSystem {
    private int totalRuns = 0;
    private int deepestLevel = 0;
    private List<UnlockableContent> permanentUnlocks = new ArrayList<>();
    private Map<String, Integer> persistentStats = new HashMap<>();
    
    public void onRunCompleted(RunResult result) {
        totalRuns++;
        deepestLevel = Math.max(deepestLevel, result.getDeepestLevel());
        
        // Постоянные улучшения
        addPersistentCurrency(result.getCurrencyEarned());
        
        // Разблокировка контента
        checkContentUnlocks();
        
        // Адаптация сложности
        adjustDifficulty(result);
    }
    
    private void checkContentUnlocks() {
        // Новые классы персонажей
        if (totalRuns >= 5 && !isUnlocked("class_necromancer")) {
            unlockContent(new ClassUnlock("Necromancer"));
        }
        
        // Новые биомы
        if (deepestLevel >= 10 && !isUnlocked("biome_crystal_caves")) {
            unlockContent(new BiomeUnlock("Crystal Caves"));
        }
        
        // Новые режимы игры
        if (totalRuns >= 3 && !isUnlocked("mode_daily_challenge")) {
            unlockContent(new GameModeUnlock("Daily Challenge"));
        }
    }
    
    public void applyMetaUpgrades(Player player) {
        // Постоянные бонусы к характеристикам
        player.addBonusHealth(persistentStats.get("bonus_health"));
        player.addBonusDamage(persistentStats.get("bonus_damage"));
        
        // Качество жизни
        if (isUnlocked("extra_starting_item")) {
            player.addStartingItem(selectRandomItem());
        }
    }
}
```

Эта комплексная система прогрессии обеспечивает глубину, разнообразие и долгосрочную мотивацию для игроков, сохраняя ощущение постоянного развития и достижений.