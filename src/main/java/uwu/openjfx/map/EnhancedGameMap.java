package uwu.openjfx.map;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.IDComponent;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.util.Pair;
import uwu.openjfx.MainApp;
import uwu.openjfx.RoyalType;
import uwu.openjfx.behaviors.CanOnlyInteractOnce;
import uwu.openjfx.components.PlayerComponent;
import uwu.openjfx.components.TrapComponent;
import uwu.openjfx.i18n.LocalizationManager;
import uwu.openjfx.utils.GameLogger;
import uwu.openjfx.map.ProgressiveMapGenerator.MapTheme;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.almasb.fxgl.dsl.FXGL.geto;
import static com.almasb.fxgl.dsl.FXGL.setLevelFromMap;
import static com.almasb.fxgl.dsl.FXGL.random;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.almasb.fxgl.dsl.FXGLForKtKt.set;

/**
 * Улучшенная GameMap с интеграцией прогрессивной генерации и полной локализацией
 */
public class EnhancedGameMap extends GameMap {

    private ProgressiveMapGenerator progressiveGenerator;
    private Map<Coordinate, RoomDescription> roomDescriptions;
    private Map<Coordinate, String> localizedRoomNames;
    private MapTheme currentMapTheme;
    private int totalRoomsGenerated;
    private long mapCreationTime;

    // Описание комнаты для локализации
    public static class RoomDescription {
        private String nameKey;
        private String descriptionKey;
        private String themeName;
        private String localizedTitle;
        private String localizedDescription;

        public RoomDescription(String nameKey, String descriptionKey, String themeName) {
            this.nameKey = nameKey;
            this.descriptionKey = descriptionKey;
            this.themeName = themeName;
            updateLocalization();
        }

        public void updateLocalization() {
            LocalizationManager lm = LocalizationManager.getInstance();
            this.localizedTitle = lm.getString(nameKey, generateDefaultTitle());
            this.localizedDescription = lm.getString(descriptionKey, generateDefaultDescription());
        }

        private String generateDefaultTitle() {
            return "Mysterious Room";
        }

        private String generateDefaultDescription() {
            return "A mysterious room filled with ancient secrets.";
        }

        // Getters
        public String getNameKey() { return nameKey; }
        public String getDescriptionKey() { return descriptionKey; }
        public String getThemeName() { return themeName; }
        public String getLocalizedTitle() { return localizedTitle; }
        public String getLocalizedDescription() { return localizedDescription; }
    }

    public EnhancedGameMap(int numOfRooms) {
        super(numOfRooms);
        this.progressiveGenerator = new ProgressiveMapGenerator();
        this.roomDescriptions = new HashMap<>();
        this.localizedRoomNames = new HashMap<>();
        this.currentMapTheme = MapTheme.MIXED;
        this.totalRoomsGenerated = 0;
        this.mapCreationTime = System.currentTimeMillis();

        initializeEnhancedFeatures();
    }

    public EnhancedGameMap(int numOfRooms, long seed) {
        super(numOfRooms);
        this.progressiveGenerator = new ProgressiveMapGenerator(seed);
        this.roomDescriptions = new HashMap<>();
        this.localizedRoomNames = new HashMap<>();
        this.currentMapTheme = MapTheme.MIXED;
        this.totalRoomsGenerated = 0;
        this.mapCreationTime = System.currentTimeMillis();

        initializeEnhancedFeatures();
    }

    /**
     * Инициализация улучшенных функций карты
     */
    private void initializeEnhancedFeatures() {
        // Выбор темы на основе текущего уровня игрока
        updatePlayerProgressFromGameState();
        progressiveGenerator.selectRandomTheme();
        this.currentMapTheme = progressiveGenerator.getCurrentTheme();

        GameLogger.system("EnhancedGameMap initialized with theme: " + currentMapTheme.getDisplayName());
    }

    /**
     * Обновление прогресса игрока из состояния игры
     */
    private void updatePlayerProgressFromGameState() {
        try {
            PlayerComponent player = PlayerComponent.getCurrentWeapon() != null ?
                PlayerComponent.class.cast(geto("player")) : null;

            int playerLevel = 1;
            int monstersKilled = PlayerComponent.getMonstersKilled();
            int gold = PlayerComponent.getGold();

            // Расчет количества завершенных комнат на основе прогресса
            int roomsCompleted = Math.max(0, monstersKilled / 3); // Предполагаем ~3 врага на комнату

            progressiveGenerator.updatePlayerProgress(playerLevel, roomsCompleted, monstersKilled);

            GameLogger.debug("Player progress updated: Level=" + playerLevel +
                           ", Rooms=" + roomsCompleted + ", Enemies=" + monstersKilled);

        } catch (Exception e) {
            GameLogger.warn("Failed to update player progress: " + e.getMessage());
            // Используем значения по умолчанию
            progressiveGenerator.updatePlayerProgress(1, 0, 0);
        }
    }

    @Override
    public void generateRooms() {
        super.generateRooms();
        enhanceGeneratedRooms();
        generateRoomDescriptions();
        this.totalRoomsGenerated = getRooms().size();

        GameLogger.system("Enhanced map generation completed: " + totalRoomsGenerated + " rooms with theme " + currentMapTheme.getDisplayName());
    }

    /**
     * Улучшение сгенерированных комнат с прогрессивными элементами
     */
    private void enhanceGeneratedRooms() {
        for (Room room : getRooms().values()) {
            if (!room.getRoomType().equals("initialRoom") &&
                !room.getRoomType().equals("bossRoom") &&
                !room.getRoomType().equals("challengeRoom")) {

                // Замена стандартных комнат на прогрессивные типы
                ProgressiveMapGenerator.ProgressiveRoomType newType = progressiveGenerator.generateRoomType();
                enhanceRoomWithType(room, newType);
            }
        }
    }

    /**
     * Улучшение комнаты с новым типом
     */
    private void enhanceRoomWithType(Room room, ProgressiveMapGenerator.ProgressiveRoomType type) {
        // Установка нового типа комнаты
        room.setRoomType(type.getMapName());

        // Генерация локализованного названия
        String roomName = progressiveGenerator.generateRoomName(type);
        localizedRoomNames.put(room.getCoordinate(), roomName);

        // Создание описания комнаты
        String descriptionKey = "room.description." + type.getId();
        String nameKey = "room.name." + type.getId();
        RoomDescription description = new RoomDescription(nameKey, descriptionKey, currentMapTheme.getId());
        roomDescriptions.put(room.getCoordinate(), description);

        // Применение тематических изменений к комнате
        applyThematicChanges(room, type);
    }

    /**
     * Применение тематических изменений к комнате
     */
    private void applyThematicChanges(Room room, ProgressiveMapGenerator.ProgressiveRoomType type) {
        switch (currentMapTheme) {
            case RUSSIAN:
                applyRussianTheme(room, type);
                break;
            case EUROPEAN:
                applyEuropeanTheme(room, type);
                break;
            case DUNGEON:
                applyDungeonTheme(room, type);
                break;
            default:
                applyMixedTheme(room, type);
        }
    }

    private void applyRussianTheme(Room room, ProgressiveMapGenerator.ProgressiveRoomType type) {
        // Русская тематика: избы, церкви, монастыри
        switch (type) {
            case LIBRARY:
                // Изба-читальня или монастырская библиотека
                break;
            case SHRINE:
                // Церковь или часовня
                break;
            case SHOP:
                // Ярмарочная лавка
                break;
            case TREASURE:
                // Сундук казака или боярская сокровищница
                break;
        }
    }

    private void applyEuropeanTheme(Room room, ProgressiveMapGenerator.ProgressiveRoomType type) {
        // Европейская тематика: замки, рыцарские залы, гильдии
        switch (type) {
            case LIBRARY:
                // Университетская библиотека
                break;
            case SHRINE:
                // Собор или часовня
                break;
            case SHOP:
                // Гильдия магов или торговая лавка
                break;
            case TREASURE:
                // Сокровищница замка
                break;
        }
    }

    private void applyDungeonTheme(Room room, ProgressiveMapGenerator.ProgressiveRoomType type) {
        // Темная тематика: подземелья, пещеры, забытые руины
        switch (type) {
            case LIBRARY:
                // Архив забытых знаний
                break;
            case SHRINE:
                // Зловещий алтарь
                break;
            case SHOP:
                // Черный рынок
                break;
            case TREASURE:
                // Пещера с сокровищами
                break;
        }
    }

    private void applyMixedTheme(Room room, ProgressiveMapGenerator.ProgressiveRoomType type) {
        // Смешанная тематика - случайные элементы
        // Применяем случайные элементы из разных тем
    }

    /**
     * Генерация описаний для всех комнат
     */
    private void generateRoomDescriptions() {
        for (Room room : getRooms().values()) {
            Coordinate coord = room.getCoordinate();

            if (!roomDescriptions.containsKey(coord)) {
                String roomType = room.getRoomType();
                String descriptionKey = "room.description." + roomType;
                String nameKey = "room.name." + roomType;

                RoomDescription description = new RoomDescription(nameKey, descriptionKey, currentMapTheme.getId());
                roomDescriptions.put(coord, description);
            }
        }
    }

    @Override
    public void loadRoom(Room newRoom, String playerSpawnPosition) {
        super.loadRoom(newRoom, playerSpawnPosition);

        // Обновление локализации при загрузке комнаты
        updateRoomLocalization(newRoom);

        // Логирование посещения комнаты
        logRoomVisit(newRoom);

        // Обновление прогресса генерации
        updateProgressAfterRoomLoad(newRoom);
    }

    /**
     * Обновление локализации комнаты
     */
    private void updateRoomLocalization(Room room) {
        Coordinate coord = room.getCoordinate();

        // Обновление существующих описаний
        if (roomDescriptions.containsKey(coord)) {
            roomDescriptions.get(coord).updateLocalization();
        }

        // Отправка события об обновлении локализации
        GameLogger.gameplay("Room localization updated: " + getLocalizedRoomName(room));
    }

    /**
     * Логирование посещения комнаты с локализацией
     */
    private void logRoomVisit(Room room) {
        String roomName = getLocalizedRoomName(room);
        String visitMessage = LocalizationManager.getInstance().getString(
            "message.room.visit",
            "Вы вошли в комнату: " + roomName
        );

        GameLogger.gameplay(visitMessage);
    }

    /**
     * Обновление прогресса после загрузки комнаты
     */
    private void updateProgressAfterRoomLoad(Room room) {
        if (!room.visited()) {
            // Увеличиваем счетчик завершенных комнат
            progressiveGenerator.updatePlayerProgress(
                progressiveGenerator.getPlayerLevel(),
                progressiveGenerator.getRoomsCompleted() + 1,
                progressiveGenerator.getEnemiesDefeated()
            );

            GameLogger.debug("Progress updated: rooms completed = " + (progressiveGenerator.getRoomsCompleted() + 1));
        }
    }

    /**
     * Получение локализованного названия комнаты
     */
    public String getLocalizedRoomName(Room room) {
        Coordinate coord = room.getCoordinate();

        if (localizedRoomNames.containsKey(coord)) {
            return localizedRoomNames.get(coord);
        }

        // Возвращаем локализованное название по типу комнаты
        String roomType = room.getRoomType();
        String key = "room.name." + roomType;
        return LocalizationManager.getInstance().getString(key, generateDefaultRoomName(roomType));
    }

    /**
     * Генерация названия комнаты по умолчанию
     */
    private String generateDefaultRoomName(String roomType) {
        switch (roomType) {
            case "initialRoom": return "Начальная комната";
            case "bossRoom": return "Комната босса";
            case "challengeRoom": return "Комната испытания";
            case "treasureRoom": return "Сокровищница";
            case "shopRoom": return "Магазин";
            case "libraryRoom": return "Библиотека";
            case "shrineRoom": return "Святилище";
            default: return "Загадочная комната";
        }
    }

    /**
     * Получение локализованного описания комнаты
     */
    public String getLocalizedRoomDescription(Room room) {
        Coordinate coord = room.getCoordinate();

        if (roomDescriptions.containsKey(coord)) {
            return roomDescriptions.get(coord).getLocalizedDescription();
        }

        // Возвращаем описание по умолчанию
        String roomType = room.getRoomType();
        String key = "room.description." + roomType;
        return LocalizationManager.getInstance().getString(key, generateDefaultRoomDescription(roomType));
    }

    /**
     * Генерация описания комнаты по умолчанию
     */
    private String generateDefaultRoomDescription(String roomType) {
        switch (roomType) {
            case "initialRoom": return "Вы начинаете свое приключение в этой комнате.";
            case "bossRoom": return "Здесь вас ждет грозный противник.";
            case "challengeRoom": return "Эта комната проверит ваши навыки.";
            case "treasureRoom": return "Здесь могут спрятаться ценные сокровища.";
            case "shopRoom": return "Торговец предлагает свои товары.";
            case "libraryRoom": return "Древние знания хранятся на этих полках.";
            case "shrineRoom": return "Святое место для отдыха и размышлений.";
            default: return "Таинственная комната, полная загадок.";
        }
    }

    /**
     * Получение информации о комнате для UI
     */
    public Map<String, Object> getRoomInfo(Room room) {
        Map<String, Object> info = new HashMap<>();

        info.put("name", getLocalizedRoomName(room));
        info.put("description", getLocalizedRoomDescription(room));
        info.put("type", room.getRoomType());
        info.put("coordinate", room.getCoordinate());
        info.put("visited", room.visited());
        info.put("theme", currentMapTheme.getDisplayName());
        info.put("difficulty", calculateRoomDifficulty(room));

        return info;
    }

    /**
     * Расчет сложности комнаты
     */
    private double calculateRoomDifficulty(Room room) {
        ProgressiveMapGenerator.ProgressiveRoomType type = determineRoomType(room);
        return progressiveGenerator.calculateEnemyDifficulty(type);
    }

    /**
     * Определение прогрессивного типа комнаты
     */
    private ProgressiveMapGenerator.ProgressiveRoomType determineRoomType(Room room) {
        String roomType = room.getRoomType();

        for (ProgressiveMapGenerator.ProgressiveRoomType type : ProgressiveMapGenerator.ProgressiveRoomType.values()) {
            if (type.getMapName().equals(roomType)) {
                return type;
            }
        }

        return ProgressiveMapGenerator.ProgressiveRoomType.NORMAL;
    }

    /**
     * Получение статистики карты
     */
    public Map<String, Object> getEnhancedMapStats() {
        Map<String, Object> stats = new HashMap<>();

        // Базовая статистика
        stats.put("totalRooms", totalRoomsGenerated);
        stats.put("theme", currentMapTheme.getId());
        stats.put("themeDisplayName", currentMapTheme.getDisplayName());
        stats.put("creationTime", mapCreationTime);

        // Прогрессивная статистика
        stats.putAll(progressiveGenerator.getGenerationStats());

        // Статистика по комнатам
        Map<String, Integer> roomTypeStats = new HashMap<>();
        for (Room room : getRooms().values()) {
            String type = room.getRoomType();
            roomTypeStats.put(type, roomTypeStats.getOrDefault(type, 0) + 1);
        }
        stats.put("roomTypeDistribution", roomTypeStats);

        // Статистика по посещениям
        int visitedCount = 0;
        for (Room room : getRooms().values()) {
            if (room.visited()) {
                visitedCount++;
            }
        }
        stats.put("visitedRooms", visitedCount);
        stats.put("unvisitedRooms", totalRoomsGenerated - visitedCount);

        return stats;
    }

    /**
     * Обновление всех локализованных строк (например, при смене языка)
     */
    public void updateAllLocalizations() {
        // Обновление описаний комнат
        for (RoomDescription description : roomDescriptions.values()) {
            description.updateLocalization();
        }

        // Перегенерация названий комнат
        regenerateRoomNames();

        GameLogger.system("All room localizations updated");
    }

    /**
     * Перегенерация названий комнат
     */
    private void regenerateRoomNames() {
        for (Map.Entry<Coordinate, Room> entry : getRooms().entrySet()) {
            Coordinate coord = entry.getKey();
            Room room = entry.getValue();

            if (!room.getRoomType().equals("initialRoom") &&
                !room.getRoomType().equals("bossRoom")) {

                ProgressiveMapGenerator.ProgressiveRoomType type = determineRoomType(room);
                String roomName = progressiveGenerator.generateRoomName(type);
                localizedRoomNames.put(coord, roomName);
            }
        }
    }

    /**
     * Получение локализованного названия направления
     */
    public String getLocalizedDirection(String direction) {
        String key = "direction." + direction.toLowerCase();
        return LocalizationManager.getInstance().getString(key, direction);
    }

    // Getters
    public ProgressiveMapGenerator getProgressiveGenerator() { return progressiveGenerator; }
    public MapTheme getCurrentMapTheme() { return currentMapTheme; }
    public int getTotalRoomsGenerated() { return totalRoomsGenerated; }
    public long getMapCreationTime() { return mapCreationTime; }

    /**
     * Получение описания комнаты по координатам
     */
    public RoomDescription getRoomDescription(Coordinate coord) {
        return roomDescriptions.get(coord);
    }

    /**
     * Проверка, есть ли описание для комнаты
     */
    public boolean hasRoomDescription(Coordinate coord) {
        return roomDescriptions.containsKey(coord);
    }

    /**
     * Добавление нового описания комнаты
     */
    public void addRoomDescription(Coordinate coord, RoomDescription description) {
        roomDescriptions.put(coord, description);
    }

    /**
     * Получение всех локализованных названий комнат
     */
    public Map<Coordinate, String> getAllLocalizedRoomNames() {
        return new HashMap<>(localizedRoomNames);
    }
}