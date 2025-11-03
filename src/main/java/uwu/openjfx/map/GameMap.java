package uwu.openjfx.map;


import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.IDComponent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.util.Pair;
import uwu.openjfx.MainApp;
import uwu.openjfx.RoyalType;
import uwu.openjfx.behaviors.CanOnlyInteractOnce;
import uwu.openjfx.components.TrapComponent;
import uwu.openjfx.utils.GameLogger;
import uwu.openjfx.i18n.LocalizationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.geto;
import static com.almasb.fxgl.dsl.FXGL.setLevelFromMap;
import static com.almasb.fxgl.dsl.FXGL.random;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.almasb.fxgl.dsl.FXGLForKtKt.set;

public class GameMap {
    private Random random = MainApp.getRandom();

    private int numOfRooms;
    private int finalBossDist;

    private Map<Coordinate, Room> rooms = new HashMap<>();
    private Room initialRoom;
    private Room bossRoom;
    private int maxX = 1;
    private int minX = -1;
    private int maxY = 1;
    private int minY = -1;

    private List<Pair<Pair<Integer, Integer>, String>> directions = new ArrayList<>();

    public GameMap(int numOfRooms) {
        directions.add(new Pair(new Pair(0, 1), "north"));
        directions.add(new Pair(new Pair(1, 0), "east"));
        directions.add(new Pair(new Pair(0, -1), "south"));
        directions.add(new Pair(new Pair(-1, 0), "west"));

        this.numOfRooms = numOfRooms;
        finalBossDist = 8; // Увеличиваем до 8 чтобы обеспечить расстояние > 6

        if (MainApp.isIsTesting()) {
            random = new Random();
        }
    }

    public void setRandomSeed(long seed) {
        random.setSeed(seed);
    }

    public Room getRoom(Coordinate coordinate) {
        return rooms.get(coordinate);
    }

    public int getNumOfRooms() {
        return numOfRooms;
    }

    public void setNumOfRooms(int numOfRooms) {
        this.numOfRooms = numOfRooms;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Rooms are managed centrally; consumers must access shared instances.")
    public Room getInitialRoom() {
        return initialRoom;
    }

    public Map<Coordinate, Room> getRooms() {
        return Collections.unmodifiableMap(rooms);
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Rooms are managed centrally; consumers must access shared instances.")
    public Room getBossRoom() {
        return bossRoom;
    }

    public void generateRooms() {
        int numRoomsGenerated = 1;
        int maxDistFromInitRoom = 0;

        // generate first room
        initialRoom = new Room(new Coordinate(0, 0), 4);
        rooms.put(initialRoom.getCoordinate(), initialRoom);
        GameLogger.system("MAP DEBUG: Generated initial room at coordinate (0, 0)");

        List<Coordinate> roomsToCreate = new ArrayList<>();

        // Добавляем начальные комнаты по списку доступных направлений
        for (Pair<Pair<Integer, Integer>, String> dir : directions) {
            Coordinate coord = new Coordinate(dir.getKey().getKey(), dir.getKey().getValue());
            if (!rooms.containsKey(coord)) {
                Room room = new Room(coord);
                rooms.put(coord, room);
                GameLogger.system("MAP DEBUG: Generated " + dir.getValue() + " room at coordinate (" + coord.getX() + ", " + coord.getY() + ")");
                generateAdjacentRooms(room, roomsToCreate);
                numRoomsGenerated++;
                if (room.getDistFromInitRoom() > maxDistFromInitRoom) {
                    maxDistFromInitRoom = room.getDistFromInitRoom();
                    bossRoom = room;
                }
            }
        }

        while (numRoomsGenerated < numOfRooms && maxDistFromInitRoom < finalBossDist) {
            if (roomsToCreate.isEmpty()) {
                for (Room room : rooms.values()) {
                    for (Coordinate coordinate : room.getAdjacentCoordinates()) {
                        if (getRoom(coordinate) == null) {
                            roomsToCreate.add(coordinate);
                        }
                    }
                    if (roomsToCreate.size() > 2) {
                        break;
                    }
                }
            }

            if (roomsToCreate.isEmpty()) {
                break; // No more rooms to create
            }
            Coordinate coordinate = roomsToCreate.remove(random.nextInt(roomsToCreate.size()));
            if (getRoom(coordinate) == null) { // if the coordinate does not have a room yet
                Room newRoom = new Room(coordinate);
                rooms.put(coordinate, newRoom);
                GameLogger.system("MAP DEBUG: Generated additional room at coordinate (" + coordinate.getX() + ", " + coordinate.getY() + "), total rooms: " + numRoomsGenerated);

                maxX = Math.max(maxX, coordinate.getX());
                minX = Math.min(minX, coordinate.getX());
                maxY = Math.max(maxY, coordinate.getY());
                minY = Math.min(minY, coordinate.getY());

                ++numRoomsGenerated;
                if (newRoom.getDistFromInitRoom() > maxDistFromInitRoom) {
                    maxDistFromInitRoom = newRoom.getDistFromInitRoom();
                    bossRoom = newRoom;
                }
                generateAdjacentRooms(newRoom, roomsToCreate);
            }
        }

        // connect the rooms
        GameLogger.system("MAP DEBUG: Starting room connection phase for " + rooms.size() + " rooms");
        for (Room room : rooms.values()) {
            connectRoomWithAdjacentRooms(room);
        }
        GameLogger.system("MAP DEBUG: Room connection phase completed. Final boss room at: " + (bossRoom != null ? bossRoom.getCoordinate().toString() : "null"));


        initialRoom.setRoomType("initialRoom");
        if (bossRoom != null) {
            bossRoom.setRoomType("bossRoom");
        } else {
            // Если bossRoom не установлен, выбираем комнату с максимальным расстоянием
            Room farthestRoom = initialRoom;
            for (Room room : rooms.values()) {
                if (room.getDistFromInitRoom() > farthestRoom.getDistFromInitRoom()) {
                    farthestRoom = room;
                }
            }
            bossRoom = farthestRoom;
            bossRoom.setRoomType("bossRoom");
        }

        if (!MainApp.isIsTesting()) {
            random.setSeed(System.nanoTime());
        }

        int challengeRooms = 2 + random.nextInt(2);
        Object[] roomList = rooms.values().toArray();
        for (int i = 0; i < challengeRooms; ++i) {
            // randomly pick a room
            Room room = null;
            int attempts = 0;
            while (true) {
                if (roomList.length == 0) {
                    break; // No rooms available
                }
                room = (Room) roomList[random.nextInt(roomList.length)];
                // make sure it's not initial room or boss room or challenge room
                if (!(room.getRoomType().equals("initialRoom")
                        || room.getRoomType().equals("bossRoom")
                        || room.getRoomType().equals("challengeRoom"))) {
                    break;
                }
                attempts++;
                if (attempts > 100) {
                    break; // Prevent infinite loop
                }
            }
            // set to challenge room
            if (room != null && room.getRoomType().equals("to_be_determined")) {
                room.setRoomType("challengeRoom");
            }
        }

    }

    private void generate4RoomsAroundInitialRoom(List<Coordinate> roomsToCreate) {
        for (Pair<Pair<Integer, Integer>, String> dir : directions) {
            Room room = new Room(new Coordinate(dir.getKey().getKey(), dir.getKey().getValue()));
            rooms.put(room.getCoordinate(), room);
            generateAdjacentRooms(room, roomsToCreate);
        }
    }

    private void generateAdjacentRooms(Room room, List<Coordinate> roomsToCreate) {
        // check how many adjacent rooms already exist
        List<Coordinate> adjacentCoordinates = room.getAdjacentCoordinates();
        List<Coordinate> availableAdjacentCoordinates = new ArrayList<>();
        for (Coordinate coordinate : adjacentCoordinates) {
            if (getRoom(coordinate) == null) {
                availableAdjacentCoordinates.add(coordinate);
            }
        }

        if (availableAdjacentCoordinates.size() == 0) {
            room.setNumAdjRooms(4);
        } else {
            int numNewRoomsToCreate = random.nextInt(availableAdjacentCoordinates.size() + 1);
            room.setNumAdjRooms(4 - availableAdjacentCoordinates.size() + numNewRoomsToCreate);

            // add new coordinates to roomsToCreate
            for (int i = 0; i < numNewRoomsToCreate && !availableAdjacentCoordinates.isEmpty(); ++i) {
                int randomIndex = random.nextInt(availableAdjacentCoordinates.size());
                Coordinate newCoordinate = availableAdjacentCoordinates.get(randomIndex);
                roomsToCreate.add(newCoordinate);
                availableAdjacentCoordinates.remove(randomIndex);
            }
        }
    }

    public void connectRoomWithAdjacentRooms(Room room) {
        Coordinate coordinate;
        Room adjacentRoom;
        int connectionsMade = 0;

        for (Pair<Pair<Integer, Integer>, String> dir : directions) {
            coordinate = room.getCoordinate();
            // Create a new Coordinate object for each direction to avoid reuse issues
            Coordinate adjacentCoordinate = new Coordinate(
                coordinate.getX() + dir.getKey().getKey(),
                coordinate.getY() + dir.getKey().getValue()
            );
            adjacentRoom = rooms.get(adjacentCoordinate);
            if (adjacentRoom != null) {
                connectionsMade++;
                GameLogger.system("MAP DEBUG: Connecting room at (" + coordinate.getX() + "," + coordinate.getY() + ") " + dir.getValue() +
                                " to room at (" + adjacentCoordinate.getX() + "," + adjacentCoordinate.getY() + ")");
                switch (dir.getValue()) {
                case "north":
                    room.setNorthRoom(adjacentRoom);
                    adjacentRoom.setSouthRoom(room);
                    break;
                case "east":
                    room.setEastRoom(adjacentRoom);
                    adjacentRoom.setWestRoom(room);
                    break;
                case "south":
                    room.setSouthRoom(adjacentRoom);
                    adjacentRoom.setNorthRoom(room);
                    break;
                case "west":
                    room.setWestRoom(adjacentRoom);
                    adjacentRoom.setEastRoom(room);
                    break;
                default:
                }
            }
        }

        if (connectionsMade > 0) {
            GameLogger.system("MAP DEBUG: Room at (" + room.getCoordinate().getX() + "," + room.getCoordinate().getY() +
                            ") has " + connectionsMade + " connections");
        }
    }

    public void loadRoom(Room newRoom, String playerSpawnPosition) {
        String roomType = newRoom.getRoomType();
        // Use small_room_1 as fallback for defaultRoom
        if ("defaultRoom".equals(roomType)) {
            roomType = "small_room_1";
        }
        Level curLevel = setLevelFromMap("tmx/" + roomType + ".tmx");
        for (Entity entity : curLevel.getEntities()) {

            if (entity.isType(RoyalType.ENEMY)) {
                if (random(0, 100) < 10) { // 10% chance to spawn an elite minion
                    spawn("eliteMinion", entity.getPosition());
                    entity.removeFromWorld();
                } else {
                    IDComponent idComponent = entity.getComponent(IDComponent.class);
                    if (!newRoom.visited()) {
                        newRoom.setEntityData(idComponent.getId(), "isAlive", 1);
                    } else {
                        if (newRoom.getEntityData(idComponent.getId(), "isAlive") == 0) {
                            entity.removeFromWorld();
                        }
                    }
                }
            }

            if (entity.isType(RoyalType.TRAP) || entity.isType(RoyalType.TRAP_TRIGGER)) {
                IDComponent idComponent = entity.getComponent(IDComponent.class);
                if (!newRoom.visited()) {
                    newRoom.setEntityData(idComponent.getId(), "triggered", 0);
                } else {
                    if (newRoom.getEntityData(idComponent.getId(), "triggered") == 1) {
                        entity.getComponent(TrapComponent.class).trigger();
                    }
                }
            }

            if (entity.isType(RoyalType.DROPPEDITEM)) {
                IDComponent idComponent = entity.getComponent(IDComponent.class);
                if (!newRoom.visited()) {
                    newRoom.setDroppedItemData(idComponent.getId(), "picked", 0);
                } else {
                    if (newRoom.getDroppedItemData(idComponent.getId(), "picked") == 1) {
                        entity.removeFromWorld();
                    }
                }
            }

            if (entity.isType(RoyalType.CHEST)) {
                IDComponent idComponent = entity.getComponent(IDComponent.class);
                if (!newRoom.visited()) {
                    newRoom.setChestData(idComponent.getId(), "hasInteracted", 0);
                } else {
                    if (newRoom.getChestsData(idComponent.getId(), "hasInteracted") == 1) {
                        CanOnlyInteractOnce chestComponent = entity.getObject("chestComponent");
                        chestComponent.disable();
                    }
                }
            }

            if (entity.isType(RoyalType.NPC)) {
                IDComponent idComponent = entity.getComponent(IDComponent.class);
                if (!newRoom.visited()) {
                    newRoom.setChestData(idComponent.getId(), "hasInteracted", 0);
                } else {
                    if (newRoom.getChestsData(idComponent.getId(), "hasInteracted") == 1) {
                        if (entity.getBoolean("isChallengeNPC")) {
                            GameLogger.debug("It is a ChallengeNPCComponent!!");
                            ((CanOnlyInteractOnce) entity.getObject("Interactable")).disable();
                        }
                    }
                }
            }

            Entity player = geto("player");
            if (player != null && entity.isType(RoyalType.POINT) && entity.getProperties()
                    .getString("position").equals(playerSpawnPosition)) {
                player.getComponent(PhysicsComponent.class).overwritePosition(
                        new Point2D(entity.getX(), entity.getY()));
                player.setZIndex(1000);
            }
        }

        if (!newRoom.visited()) {
            newRoom.setVisited(true);
        }

        set("curRoom", newRoom);
        set("curLevel", curLevel);
        GameLogger.debug("New room: " + newRoom.getCoordinate());
    }

    public int getWidth() {
        return maxX - minX;
    }

    public int getHeight() {
        return maxY - minY;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    /**
     * Получение локализованного названия направления
     */
    public String getLocalizedDirection(String direction) {
        LocalizationManager lm = LocalizationManager.getInstance();
        String key = "direction." + direction.toLowerCase();
        return lm.getString(key, direction);
    }

    /**
     * Получение локализованного названия комнаты
     */
    public String getLocalizedRoomName(Room room) {
        LocalizationManager lm = LocalizationManager.getInstance();
        String roomType = room.getRoomType();
        String key = "room.name." + roomType;
        return lm.getString(key, generateDefaultRoomName(roomType));
    }

    /**
     * Генерация названия комнаты по умолчанию
     */
    private String generateDefaultRoomName(String roomType) {
        switch (roomType) {
            case "initialRoom": return "Entrance Room";
            case "bossRoom": return "Boss Room";
            case "challengeRoom": return "Challenge Room";
            case "treasureRoom": return "Treasure Room";
            case "shopRoom": return "Shop";
            case "libraryRoom": return "Library";
            case "shrineRoom": return "Shrine";
            default: return "Mysterious Room";
        }
    }

    /**
     * Получение локализованного описания комнаты
     */
    public String getLocalizedRoomDescription(Room room) {
        LocalizationManager lm = LocalizationManager.getInstance();
        String roomType = room.getRoomType();
        String key = "room.description." + roomType;
        return lm.getString(key, generateDefaultRoomDescription(roomType));
    }

    /**
     * Генерация описания комнаты по умолчанию
     */
    private String generateDefaultRoomDescription(String roomType) {
        switch (roomType) {
            case "initialRoom": return "You begin your adventure in this room.";
            case "bossRoom": return "A fearsome boss awaits you here.";
            case "challengeRoom": return "This room will test your skills.";
            case "treasureRoom": return "Treasures may be hidden here.";
            case "shopRoom": return "A merchant offers their wares.";
            case "libraryRoom": return "Ancient knowledge rests on these shelves.";
            case "shrineRoom": return "A holy place for rest and reflection.";
            default: return "A mysterious room full of secrets.";
        }
    }
}
