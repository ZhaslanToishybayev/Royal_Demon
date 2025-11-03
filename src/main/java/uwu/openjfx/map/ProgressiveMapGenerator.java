package uwu.openjfx.map;

import com.almasb.fxgl.dsl.FXGL;
import uwu.openjfx.MainApp;
import uwu.openjfx.components.PlayerComponent;
import uwu.openjfx.i18n.LocalizationManager;
import uwu.openjfx.utils.GameLogger;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Улучшенная прогрессивная генерация карт с адаптивной сложностью
 * и тематическими элементами на основе прогресса игрока
 */
public class ProgressiveMapGenerator {

    // Параметры генерации
    private long generationSeed;
    private Random random;
    private int playerLevel;
    private int roomsCompleted;
    private int enemiesDefeated;
    private double difficultyMultiplier;

    // Темы генерации
    public enum MapTheme {
        RUSSIAN("russian", "Русская тематика", 0.8, 1.2),
        EUROPEAN("european", "Европейская тематика", 1.0, 1.0),
        DUNGEON("dungeon", "Темное подземелье", 1.2, 1.1),
        MIXED("mixed", "Смешанная тематика", 1.0, 1.0);

        private final String id;
        private final String displayName;
        private final double enemyDifficultyMultiplier;
        private final double treasureMultiplier;

        MapTheme(String id, String displayName, double enemyDifficultyMultiplier, double treasureMultiplier) {
            this.id = id;
            this.displayName = displayName;
            this.enemyDifficultyMultiplier = enemyDifficultyMultiplier;
            this.treasureMultiplier = treasureMultiplier;
        }

        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public double getEnemyDifficultyMultiplier() { return enemyDifficultyMultiplier; }
        public double getTreasureMultiplier() { return treasureMultiplier; }
    }

    // Типы комнат с прогрессивной сложностью
    public enum ProgressiveRoomType {
        INITIAL("initial", "initialRoom", 1.0),
        NORMAL("normal", "defaultRoom", 1.0),
        CHALLENGE("challenge", "challengeRoom", 1.5),
        TREASURE("treasure", "treasureRoom", 1.2),
        BOSS("boss", "bossRoom", 2.0),
        SHOP("shop", "shopRoom", 1.0),
        LIBRARY("library", "libraryRoom", 1.3),
        SHRINE("shrine", "shrineRoom", 1.1);

        private final String id;
        private final String mapName;
        private final double difficultyMultiplier;

        ProgressiveRoomType(String id, String mapName, double difficultyMultiplier) {
            this.id = id;
            this.mapName = mapName;
            this.difficultyMultiplier = difficultyMultiplier;
        }

        public String getId() { return id; }
        public String getMapName() { return mapName; }
        public double getDifficultyMultiplier() { return difficultyMultiplier; }
    }

    private MapTheme currentTheme;
    private Map<String, ProgressiveRoomType> roomTypeDistribution;

    public ProgressiveMapGenerator() {
        this.generationSeed = System.currentTimeMillis();
        this.random = new Random(generationSeed);
        this.currentTheme = MapTheme.MIXED;
        this.playerLevel = 1;
        this.roomsCompleted = 0;
        this.enemiesDefeated = 0;
        this.difficultyMultiplier = 1.0;

        initializeRoomDistribution();
    }

    public ProgressiveMapGenerator(long seed) {
        this();
        this.generationSeed = seed;
        this.random.setSeed(seed);
    }

    /**
     * Инициализация распределения типов комнат на основе прогресса
     */
    private void initializeRoomDistribution() {
        roomTypeDistribution = new HashMap<>();
        updateRoomDistribution();
    }

    /**
     * Обновление распределения комнат в зависимости от прогресса игрока
     */
    public void updateRoomDistribution() {
        roomTypeDistribution.clear();

        // Базовые веса для разных типов комнат
        double normalWeight = 50.0;
        double challengeWeight = 10.0 + (playerLevel * 2.0);
        double treasureWeight = 5.0 + (roomsCompleted * 1.0);
        double shopWeight = 8.0;
        double libraryWeight = 5.0 + (enemiesDefeated * 0.1);
        double shrineWeight = 3.0;

        // Адаптация весов на основе сложности
        if (difficultyMultiplier > 1.5) {
            challengeWeight *= 1.5;
            treasureWeight *= 0.8;
        } else if (difficultyMultiplier < 0.8) {
            challengeWeight *= 0.7;
            treasureWeight *= 1.3;
            shrineWeight *= 1.5;
        }

        // Тематическая адаптация
        switch (currentTheme) {
            case RUSSIAN:
                libraryWeight *= 1.3; // Больше библиотек (избы, летописи)
                shrineWeight *= 1.5; // Больше святынь (церкви, часовни)
                break;
            case EUROPEAN:
                shopWeight *= 1.2; // Больше магазинов
                libraryWeight *= 1.1;
                break;
            case DUNGEON:
                challengeWeight *= 1.4;
                normalWeight *= 0.8;
                break;
        }

        // Заполнение распределения
        addRoomTypeToDistribution(ProgressiveRoomType.NORMAL, normalWeight);
        addRoomTypeToDistribution(ProgressiveRoomType.CHALLENGE, challengeWeight);
        addRoomTypeToDistribution(ProgressiveRoomType.TREASURE, treasureWeight);
        addRoomTypeToDistribution(ProgressiveRoomType.SHOP, shopWeight);
        addRoomTypeToDistribution(ProgressiveRoomType.LIBRARY, libraryWeight);
        addRoomTypeToDistribution(ProgressiveRoomType.SHRINE, shrineWeight);

        GameLogger.debug("Room distribution updated for level " + playerLevel +
                        " with difficulty " + difficultyMultiplier);
    }

    /**
     * Добавление типа комнаты в распределение с учетом веса
     */
    private void addRoomTypeToDistribution(ProgressiveRoomType type, double weight) {
        int count = (int) Math.max(1, weight);
        for (int i = 0; i < count; i++) {
            roomTypeDistribution.put(type.getId() + "_" + i, type);
        }
    }

    /**
     * Генерация типа комнаты на основе прогресса и распределения
     */
    public ProgressiveRoomType generateRoomType() {
        if (roomTypeDistribution.isEmpty()) {
            updateRoomDistribution();
        }

        List<String> keys = new ArrayList<>(roomTypeDistribution.keySet());
        String randomKey = keys.get(random.nextInt(keys.size()));
        return roomTypeDistribution.get(randomKey);
    }

    /**
     * Обновление параметров генерации на основе прогресса игрока
     */
    public void updatePlayerProgress(int playerLevel, int roomsCompleted, int enemiesDefeated) {
        this.playerLevel = Math.max(1, playerLevel);
        this.roomsCompleted = Math.max(0, roomsCompleted);
        this.enemiesDefeated = Math.max(0, enemiesDefeated);

        // Расчет множителя сложности
        double baseDifficulty = 1.0;
        double levelDifficulty = 1.0 + (playerLevel * 0.1);
        double roomsDifficulty = 1.0 + (roomsCompleted * 0.05);
        double enemiesDifficulty = 1.0 + (enemiesDefeated * 0.001);

        this.difficultyMultiplier = baseDifficulty * levelDifficulty * roomsDifficulty * enemiesDifficulty;

        // Обновление распределения комнат
        updateRoomDistribution();

        GameLogger.gameplay("Progress updated: Level " + playerLevel +
                          ", Rooms: " + roomsCompleted +
                          ", Enemies: " + enemiesDefeated +
                          ", Difficulty: " + String.format("%.2f", difficultyMultiplier));
    }

    /**
     * Установка темы генерации
     */
    public void setTheme(MapTheme theme) {
        this.currentTheme = theme;
        updateRoomDistribution();
        GameLogger.gameplay("Map theme changed to: " + theme.getDisplayName());
    }

    /**
     * Выбор темы на основе прогресса или случайным образом
     */
    public void selectRandomTheme() {
        MapTheme[] themes = MapTheme.values();

        // Для первых уровней больше вероятности выбора стандартных тем
        if (playerLevel <= 3) {
            MapTheme[] earlyThemes = {MapTheme.EUROPEAN, MapTheme.RUSSIAN, MapTheme.MIXED};
            this.currentTheme = earlyThemes[random.nextInt(earlyThemes.length)];
        } else {
            this.currentTheme = themes[random.nextInt(themes.length)];
        }

        updateRoomDistribution();
    }

    /**
     * Генерация имени комнаты на основе темы и типа
     */
    public String generateRoomName(ProgressiveRoomType type) {
        String prefix = "";
        String suffix = "";

        switch (currentTheme) {
            case RUSSIAN:
                prefix = getRandomRussianPrefix();
                suffix = getRandomRussianSuffix(type);
                break;
            case EUROPEAN:
                prefix = getRandomEuropeanPrefix();
                suffix = getRandomEuropeanSuffix(type);
                break;
            case DUNGEON:
                prefix = getRandomDungeonPrefix();
                suffix = getRandomDungeonSuffix(type);
                break;
            default:
                prefix = getRandomMixedPrefix();
                suffix = getRandomMixedSuffix(type);
        }

        return prefix + " " + suffix;
    }

    // Русские префиксы для названий комнат
    private String getRandomRussianPrefix() {
        String[] prefixes = {"Древняя", "Забытая", "Таинственная", "Холодная", "Темная",
                            "Святая", "Царская", "Крестьянская", "Монашеская", "Рыцарская"};
        return prefixes[random.nextInt(prefixes.length)];
    }

    private String getRandomRussianSuffix(ProgressiveRoomType type) {
        switch (type) {
            case LIBRARY: return "летопись";
            case SHRINE: return random.nextBoolean() ? "часовня" : "церковь";
            case TREASURE: return "сокровищница";
            case SHOP: return "лавка";
            case CHALLENGE: return "испытание";
            default: return "комната";
        }
    }

    // Европейские префиксы
    private String getRandomEuropeanPrefix() {
        String[] prefixes = {"Ancient", "Forgotten", "Mysterious", "Cold", "Dark",
                            "Holy", "Royal", "Merchant", "Knight", "Scholar"};
        return prefixes[random.nextInt(prefixes.length)];
    }

    private String getRandomEuropeanSuffix(ProgressiveRoomType type) {
        switch (type) {
            case LIBRARY: return "Library";
            case SHRINE: return random.nextBoolean() ? "Chapel" : "Shrine";
            case TREASURE: return "Treasury";
            case SHOP: return "Shop";
            case CHALLENGE: return "Challenge";
            default: return "Room";
        }
    }

    // Подземельные префиксы
    private String getRandomDungeonPrefix() {
        String[] prefixes = {"Dreadful", "Cursed", "Shadowy", "Ancient", "Forgotten",
                            "Bloody", "Dark", "Deep", "Hollow", "Lost"};
        return prefixes[random.nextInt(prefixes.length)];
    }

    private String getRandomDungeonSuffix(ProgressiveRoomType type) {
        switch (type) {
            case LIBRARY: return "Archive";
            case SHRINE: return "Altar";
            case TREASURE: return "Hoard";
            case SHOP: return "Market";
            case CHALLENGE: return "Trial";
            default: return "Chamber";
        }
    }

    // Смешанные префиксы
    private String getRandomMixedPrefix() {
        String[] prefixes = {"Mysterious", "Ancient", "Forgotten", "Hidden", "Secret",
                            "Lost", "Old", "Strange", "Unknown", "Silent"};
        return prefixes[random.nextInt(prefixes.length)];
    }

    private String getRandomMixedSuffix(ProgressiveRoomType type) {
        switch (type) {
            case LIBRARY: return random.nextBoolean() ? "Library" : "Archive";
            case SHRINE: return random.nextBoolean() ? "Shrine" : "Altar";
            case TREASURE: return random.nextBoolean() ? "Treasury" : "Hoard";
            case SHOP: return random.nextBoolean() ? "Shop" : "Market";
            case CHALLENGE: return random.nextBoolean() ? "Challenge" : "Trial";
            default: return random.nextBoolean() ? "Room" : "Chamber";
        }
    }

    /**
     * Расчет сложности врагов для комнаты
     */
    public double calculateEnemyDifficulty(ProgressiveRoomType roomType) {
        double baseDifficulty = difficultyMultiplier;
        double roomMultiplier = roomType.getDifficultyMultiplier();
        double themeMultiplier = currentTheme.getEnemyDifficultyMultiplier();

        return baseDifficulty * roomMultiplier * themeMultiplier;
    }

    /**
     * Расчет множителя сокровищ для комнаты
     */
    public double calculateTreasureMultiplier(ProgressiveRoomType roomType) {
        double baseMultiplier = 1.0;
        double roomMultiplier = roomType == ProgressiveRoomType.TREASURE ? 2.0 : 1.0;
        double themeMultiplier = currentTheme.getTreasureMultiplier();
        double progressMultiplier = 1.0 + (roomsCompleted * 0.1);

        return baseMultiplier * roomMultiplier * themeMultiplier * progressMultiplier;
    }

    // Getters
    public long getGenerationSeed() { return generationSeed; }
    public MapTheme getCurrentTheme() { return currentTheme; }
    public int getPlayerLevel() { return playerLevel; }
    public int getRoomsCompleted() { return roomsCompleted; }
    public int getEnemiesDefeated() { return enemiesDefeated; }
    public double getDifficultyMultiplier() { return difficultyMultiplier; }

    /**
     * Получение локализованного описания комнаты
     */
    public String getLocalizedRoomDescription(ProgressiveRoomType type) {
        String key = "room.description." + type.getId();
        return LocalizationManager.getInstance().getString(key, generateDefaultDescription(type));
    }

    /**
     * Генерация описания по умолчанию, если локализация отсутствует
     */
    private String generateDefaultDescription(ProgressiveRoomType type) {
        switch (type) {
            case INITIAL:
                return "Вы входите в начальную комнату подземелья.";
            case NORMAL:
                return "Обычная комната подземелья, наполненная тайнами.";
            case CHALLENGE:
                return "Эта комната испытает ваши навыки в бою.";
            case TREASURE:
                return "Сокровищница! Здесь могут быть ценные предметы.";
            case BOSS:
                return "Финальная комната. Здесь вас ждет грозный противник.";
            case SHOP:
                return "Торговая лавка, где можно купить или продать предметы.";
            case LIBRARY:
                return "Древняя библиотека с мудростью веков.";
            case SHRINE:
                return "Святое место, где можно получить благословение.";
            default:
                return "Загадочная комната подземелья.";
        }
    }

    /**
     * Генерация описания на основе темы и типа
     */
    public String generateThematicDescription(ProgressiveRoomType type) {
        String roomName = generateRoomName(type);
        String baseDescription = getLocalizedRoomDescription(type);

        return roomName + "\n" + baseDescription;
    }

    /**
     * Сброс генератора к начальным параметрам
     */
    public void reset() {
        this.generationSeed = System.currentTimeMillis();
        this.random = new Random(generationSeed);
        this.currentTheme = MapTheme.MIXED;
        this.playerLevel = 1;
        this.roomsCompleted = 0;
        this.enemiesDefeated = 0;
        this.difficultyMultiplier = 1.0;

        initializeRoomDistribution();
        GameLogger.system("ProgressiveMapGenerator reset to default state");
    }

    /**
     * Получение статистики генерации
     */
    public Map<String, Object> getGenerationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("seed", generationSeed);
        stats.put("theme", currentTheme.getId());
        stats.put("themeDisplayName", currentTheme.getDisplayName());
        stats.put("playerLevel", playerLevel);
        stats.put("roomsCompleted", roomsCompleted);
        stats.put("enemiesDefeated", enemiesDefeated);
        stats.put("difficultyMultiplier", difficultyMultiplier);
        stats.put("roomDistributionSize", roomTypeDistribution.size());

        return stats;
    }
}