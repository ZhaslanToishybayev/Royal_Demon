package uwu.openjfx.save;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс данных для сохранения состояния игры
 */
public class GameSaveData {
    @JsonProperty("version")
    private String version = "1.0";

    @JsonProperty("saveTime")
    private LocalDateTime saveTime;

    @JsonProperty("playTime")
    private long playTime; // в секундах

    @JsonProperty("playerLevel")
    private int playerLevel = 1;

    @JsonProperty("playerExperience")
    private int playerExperience = 0;

    @JsonProperty("playerHealth")
    private int playerHealth;

    @JsonProperty("playerMaxHealth")
    private int playerMaxHealth = 100;

    @JsonProperty("playerPosition")
    private Position playerPosition;

    @JsonProperty("currentRoom")
    private String currentRoomId;

    @JsonProperty("visitedRooms")
    private Map<String, RoomState> visitedRooms = new HashMap<>();

    @JsonProperty("inventory")
    private InventoryState inventory = new InventoryState();

    @JsonProperty("weaponsUnlocked")
    private Map<String, Boolean> weaponsUnlocked = new HashMap<>();

    @JsonProperty("achievements")
    private Map<String, Boolean> achievements = new HashMap<>();

    @JsonProperty("difficulty")
    private String difficulty = "normal";

    @JsonProperty("gold")
    private int gold = 0;

    @JsonProperty("gameFlags")
    private Map<String, Object> gameFlags = new HashMap<>();

    // Конструкторы
    public GameSaveData() {
    }

    // Getters и Setters
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDateTime getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(LocalDateTime saveTime) {
        this.saveTime = saveTime;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(int playerLevel) {
        this.playerLevel = playerLevel;
    }

    public int getPlayerExperience() {
        return playerExperience;
    }

    public void setPlayerExperience(int playerExperience) {
        this.playerExperience = playerExperience;
    }

    public int getPlayerHealth() {
        return playerHealth;
    }

    public void setPlayerHealth(int playerHealth) {
        this.playerHealth = playerHealth;
    }

    public int getPlayerMaxHealth() {
        return playerMaxHealth;
    }

    public void setPlayerMaxHealth(int playerMaxHealth) {
        this.playerMaxHealth = playerMaxHealth;
    }

    public Position getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(Position playerPosition) {
        this.playerPosition = playerPosition;
    }

    public String getCurrentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(String currentRoomId) {
        this.currentRoomId = currentRoomId;
    }

    public Map<String, RoomState> getVisitedRooms() {
        return visitedRooms;
    }

    public void setVisitedRooms(Map<String, RoomState> visitedRooms) {
        this.visitedRooms = visitedRooms;
    }

    public InventoryState getInventory() {
        return inventory;
    }

    public void setInventory(InventoryState inventory) {
        this.inventory = inventory;
    }

    public Map<String, Boolean> getWeaponsUnlocked() {
        return weaponsUnlocked;
    }

    public void setWeaponsUnlocked(Map<String, Boolean> weaponsUnlocked) {
        this.weaponsUnlocked = weaponsUnlocked;
    }

    public Map<String, Boolean> getAchievements() {
        return achievements;
    }

    public void setAchievements(Map<String, Boolean> achievements) {
        this.achievements = achievements;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public Map<String, Object> getGameFlags() {
        return gameFlags;
    }

    public void setGameFlags(Map<String, Object> gameFlags) {
        this.gameFlags = gameFlags;
    }

    // Вложенные классы
    public static class Position {
        @JsonProperty("x")
        private double x;

        @JsonProperty("y")
        private double y;

        public Position() {
        }

        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }

    public static class RoomState {
        @JsonProperty("cleared")
        private boolean cleared = false;

        @JsonProperty("visited")
        private boolean visited = false;

        @JsonProperty("enemiesDefeated")
        private Map<String, Boolean> enemiesDefeated = new HashMap<>();

        @JsonProperty("itemsCollected")
        private Map<String, Boolean> itemsCollected = new HashMap<>();

        // Getters и Setters
        public boolean isCleared() {
            return cleared;
        }

        public void setCleared(boolean cleared) {
            this.cleared = cleared;
        }

        public boolean isVisited() {
            return visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }

        public Map<String, Boolean> getEnemiesDefeated() {
            return enemiesDefeated;
        }

        public void setEnemiesDefeated(Map<String, Boolean> enemiesDefeated) {
            this.enemiesDefeated = enemiesDefeated;
        }

        public Map<String, Boolean> getItemsCollected() {
            return itemsCollected;
        }

        public void setItemsCollected(Map<String, Boolean> itemsCollected) {
            this.itemsCollected = itemsCollected;
        }
    }

    public static class InventoryState {
        @JsonProperty("items")
        private Map<String, Integer> items = new HashMap<>();

        @JsonProperty("currentWeapon")
        private String currentWeapon;

        @JsonProperty("potions")
        private Map<String, Integer> potions = new HashMap<>();

        // Getters и Setters
        public Map<String, Integer> getItems() {
            return items;
        }

        public void setItems(Map<String, Integer> items) {
            this.items = items;
        }

        public String getCurrentWeapon() {
            return currentWeapon;
        }

        public void setCurrentWeapon(String currentWeapon) {
            this.currentWeapon = currentWeapon;
        }

        public Map<String, Integer> getPotions() {
            return potions;
        }

        public void setPotions(Map<String, Integer> potions) {
            this.potions = potions;
        }
    }
}