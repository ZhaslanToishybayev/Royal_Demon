package uwu.openjfx.map;

import com.almasb.fxgl.dsl.FXGL;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import uwu.openjfx.MainApp;
import uwu.openjfx.utils.GameLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Room {
    private Random random = MainApp.getRandom();
    private Coordinate coordinate;

    // num of Neighbors == num of doors
    private int numAdjRooms;

    // references to neighbor rooms
    private Room northRoom;
    private Room southRoom;
    private Room westRoom;
    private Room eastRoom;

    private Boolean visited = false;
    // set different room type based on numb of adjacent rooms
    private String roomType = "to_be_determined";
    private Map<Integer, Map<String, Integer>> entitiesData;
    private Map<Integer, Map<String, Integer>> droppedItemsData;
    private Map<Integer, Map<String, Integer>> chestsData;


    public Room(Coordinate coordinate) {
        entitiesData = new HashMap<>();
        droppedItemsData = new HashMap<>();
        chestsData = new HashMap<>();
        this.coordinate = new Coordinate(coordinate.getX(), coordinate.getY());
        if (!MainApp.isIsTesting()) {
            List<String> roomTypeList = FXGL.geto("roomTypeList");
            if (roomTypeList != null && !roomTypeList.isEmpty()) {
                roomType = roomTypeList.get(random.nextInt(roomTypeList.size()));
            } else {
                roomType = "small_room_1"; // Use small_room_1 as fallback if list is empty
            }
        }

        if (MainApp.isIsTesting()) {
            random = new Random();
        }
    }

    public Room(Coordinate coordinate, int numAdjRooms) {
        this(coordinate);
        this.numAdjRooms = numAdjRooms;
    }

    public Boolean visited() {
        return visited;
    }

    public void setVisited(Boolean visited) {
        this.visited = visited;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getEntityData(int id, String propertyName) {
        Map<String, Integer> entityData = entitiesData.get(id);
        if (entityData == null) {
            return 0;  // Default value if no data exists
        }
        Integer value = entityData.get(propertyName);
        return value != null ? value : 0;
    }

    public void setEntityData(int id, String propertyName, int val) {
        if (entitiesData.get(id) == null) {
            entitiesData.put(id, new HashMap<>());
        }
        entitiesData.get(id).put(propertyName, val);
    }

    public int getDroppedItemData(int id, String propertyName) {
        return droppedItemsData.get(id).get(propertyName);
    }

    public void setDroppedItemData(int id, String propertyName, int val) {
        if (droppedItemsData.get(id) == null) {
            droppedItemsData.put(id, new HashMap<>());
        }
        droppedItemsData.get(id).put(propertyName, val);
    }

    public int getChestsData(int id, String propertyName) {
        return chestsData.get(id).get(propertyName);
    }

    public void setChestData(int id, String propertyName, int val) {
        if (chestsData.get(id) == null) {
            chestsData.put(id, new HashMap<>());
        }
        chestsData.get(id).put(propertyName, val);
    }

    public Boolean enemiesCleared() {
        // May not work if we have ally creatures
        for (Map<String, Integer> data : entitiesData.values()) {
            if (data.get("isAlive") != null && data.get("isAlive") == 1) {
                return false;
            }
        }
        GameLogger.gameplay("Enemies cleared!");
        return true;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Room exposes shared neighbors for navigation.")
    public Coordinate getCoordinate() {
        return coordinate;
    }

    public List<Coordinate> getAdjacentCoordinates() {
        ArrayList<Coordinate> adjacentCoordinates = new ArrayList<>();
        adjacentCoordinates.add(new Coordinate(coordinate.getX(), coordinate.getY() + 1));
        adjacentCoordinates.add(new Coordinate(coordinate.getX() + 1, coordinate.getY()));
        adjacentCoordinates.add(new Coordinate(coordinate.getX(), coordinate.getY() - 1));
        adjacentCoordinates.add(new Coordinate(coordinate.getX() - 1, coordinate.getY()));
        return adjacentCoordinates;
    }

    public int getDistFromInitRoom() {
        return Math.abs(coordinate.getX()) + Math.abs(coordinate.getY());
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Room exposes shared neighbors for navigation.")
    public Room getNorthRoom() {
        return northRoom;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Room graph shares neighbor instances.")
    public void setNorthRoom(Room northRoom) {
        this.northRoom = northRoom;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Room exposes shared neighbors for navigation.")
    public Room getSouthRoom() {
        return southRoom;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Room graph shares neighbor instances.")
    public void setSouthRoom(Room southRoom) {
        this.southRoom = southRoom;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Room exposes shared neighbors for navigation.")
    public Room getWestRoom() {
        return westRoom;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Room graph shares neighbor instances.")
    public void setWestRoom(Room westRoom) {
        this.westRoom = westRoom;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Room exposes shared neighbors for navigation.")
    public Room getEastRoom() {
        return eastRoom;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Room graph shares neighbor instances.")
    public void setEastRoom(Room eastRoom) {
        this.eastRoom = eastRoom;
    }

    public int getNumAdjRooms() {
        return numAdjRooms;
    }

    public void setNumAdjRooms(int numAdjRooms) {
        this.numAdjRooms = numAdjRooms;
    }

    public List<Room> getAdjacentRooms() {
        ArrayList<Room> adjacentRooms = new ArrayList<>();
        adjacentRooms.add(getNorthRoom());
        adjacentRooms.add(getEastRoom());
        adjacentRooms.add(getSouthRoom());
        adjacentRooms.add(getWestRoom());
        return adjacentRooms;
    }

}
