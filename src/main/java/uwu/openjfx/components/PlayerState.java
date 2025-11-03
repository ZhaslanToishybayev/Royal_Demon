package uwu.openjfx.components;

import uwu.openjfx.weapons.Weapon;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Thread-safe player state management.
 * Replaces static fields in PlayerComponent to prevent memory leaks and race conditions.
 */
public class PlayerState {
    // Weapon and inventory
    private Weapon currentWeapon;
    private final ConcurrentMap<Class<? extends Weapon>, Weapon> weaponInventory;

    // Combat stats
    private double attackPower = 1;
    private int piercePow = 1;
    private boolean isAttackPowerBuffed = false;
    private int attackPowerBuffDuration = 300;
    private boolean isChanneling = false;

    // Movement
    private double speed = 180;

    // Player state
    private boolean isPressingMovementKeys = false;
    private boolean prepAttack = false;
    private boolean startAttack = false;
    private boolean ultimateActivated = false;
    private boolean ultimateCD = false;

    // Character progression
    private String playerName;
    private String gameDifficulty;
    private int monstersKilled = 0;
    private double damageDealt = 0;

    // Inventory items
    private int gold = 0;
    private int healthPotAmount = 0;
    private int ragePotAmount = 0;

    public PlayerState() {
        this.weaponInventory = new ConcurrentHashMap<>();
    }

    // Weapon methods
    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public void setCurrentWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
    }

    public void addWeaponToInventory(Weapon weapon) {
        if (weapon == null) {
            return;
        }
        weaponInventory.putIfAbsent(weapon.getClass(), weapon);
    }

    public List<Weapon> getWeaponInventoryList() {
        return new ArrayList<>(weaponInventory.values());
    }

    public boolean hasWeapon(Class<? extends Weapon> weaponClass) {
        return weaponInventory.containsKey(weaponClass);
    }

    // Combat methods
    public double getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(double attackPower) {
        this.attackPower = attackPower;
    }

    public int getPiercePow() {
        return piercePow;
    }

    public void setPiercePow(int pierce) {
        this.piercePow = pierce;
    }

    public boolean isAttackPowerBuffed() {
        return isAttackPowerBuffed;
    }

    public void setIsAttackPowerBuffed(boolean buffed) {
        isAttackPowerBuffed = buffed;
    }

    public int getAttackPowerBuffDuration() {
        return attackPowerBuffDuration;
    }

    public void setAttackPowerBuffDuration(int duration) {
        this.attackPowerBuffDuration = duration;
    }

    public boolean isChanneling() {
        return isChanneling;
    }

    public void setChanneling(boolean channeling) {
        isChanneling = channeling;
    }

    // Movement methods
    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isPressingMovementKeys() {
        return isPressingMovementKeys;
    }

    public void setPressingMovementKeys(boolean pressing) {
        isPressingMovementKeys = pressing;
    }

    public boolean isPrepAttack() {
        return prepAttack;
    }

    public void setPrepAttack(boolean prep) {
        prepAttack = prep;
    }

    public boolean isStartAttack() {
        return startAttack;
    }

    public void setStartAttack(boolean start) {
        startAttack = start;
    }

    public boolean isUltimateActivated() {
        return ultimateActivated;
    }

    public void setUltimateActivated(boolean activated) {
        ultimateActivated = activated;
    }

    public boolean isUltimateCD() {
        return ultimateCD;
    }

    public void setUltimateCD(boolean cd) {
        ultimateCD = cd;
    }

    // Player info methods
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public String getGameDifficulty() {
        return gameDifficulty;
    }

    public void setGameDifficulty(String difficulty) {
        this.gameDifficulty = difficulty;
    }

    public int getMonstersKilled() {
        return monstersKilled;
    }

    public void addToMonstersKilled() {
        this.monstersKilled++;
    }

    public double getDamageDealt() {
        return damageDealt;
    }

    public void addToDamageDealt(double damage) {
        this.damageDealt += damage;
    }

    // Inventory methods
    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void addGold(int gold) {
        if (gold > 0) {
            this.gold += gold;
        }
    }

    public boolean spendGold(int cost) {
        if (this.gold >= cost) {
            this.gold -= cost;
            return true;
        }
        return false;
    }

    public int getHealthPotAmount() {
        return healthPotAmount;
    }

    public void setHealthPotAmount(int amount) {
        this.healthPotAmount = amount;
    }

    public void addHealthPot(int amount) {
        if (amount > 0) {
            this.healthPotAmount += amount;
        }
    }

    public void useHealthPot() {
        if (healthPotAmount > 0) {
            healthPotAmount--;
        }
    }

    public int getRagePotAmount() {
        return ragePotAmount;
    }

    public void setRagePotAmount(int amount) {
        this.ragePotAmount = amount;
    }

    public void addRagePot(int amount) {
        if (amount > 0) {
            this.ragePotAmount += amount;
        }
    }

    public void useRagePot() {
        if (ragePotAmount > 0) {
            ragePotAmount--;
        }
    }

    // Reset method
    public void resetAllGameData() {
        currentWeapon = null;
        weaponInventory.clear();
        attackPower = 1;
        piercePow = 1;
        isAttackPowerBuffed = false;
        attackPowerBuffDuration = 300;
        isChanneling = false;
        speed = 180;
        isPressingMovementKeys = false;
        prepAttack = false;
        startAttack = false;
        ultimateActivated = false;
        ultimateCD = false;
        playerName = null;
        gameDifficulty = null;
        monstersKilled = 0;
        damageDealt = 0;
        gold = 0;
        healthPotAmount = 0;
        ragePotAmount = 0;
    }
}
