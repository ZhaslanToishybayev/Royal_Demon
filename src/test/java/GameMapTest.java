import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import uwu.openjfx.MainApp;
import uwu.openjfx.map.GameMap;
import uwu.openjfx.map.Room;
import uwu.openjfx.input.TeleportToBossRoom;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


class GameMapTest {

    private final int rooms = 40;

    @BeforeEach
    public void init() {
        MainApp.setIsTesting(true);
    }

    @RepeatedTest(500)
    void testBossRoomAtLeast6RoomsAway() {
        var gameMap = new GameMap(rooms);
        gameMap.generateRooms();

        assertTrue(gameMap.getBossRoom().getDistFromInitRoom() > 6);
    }

    @RepeatedTest(500)
    void testBossRoomReachable() {
        var gameMap = new GameMap(rooms);
        gameMap.generateRooms();

        Room initialRoom = gameMap.getInitialRoom();
        Room bossRoom = gameMap.getBossRoom();

        boolean foundBossRoom = false;

        Queue<Room> queue = new ArrayDeque<>();
        Set<Room> visited = new HashSet<>();
        queue.add(initialRoom);
        visited.add(initialRoom);

        while (!queue.isEmpty()) {
            Room room = queue.poll();
            if (room == bossRoom) {
                foundBossRoom = true;
                break;
            }

            for (Room adjRoom : room.getAdjacentRooms()) {
                if (adjRoom != null && !visited.contains(adjRoom)) {
                    queue.add(adjRoom);
                    visited.add(adjRoom);
                }
            }
        }

        assertTrue(foundBossRoom);
    }

    @RepeatedTest(500)
    void testInitialRoomHas4AdjRooms() {
        var gameMap = new GameMap(rooms);
        gameMap.generateRooms();
        assertEquals(4, gameMap.getInitialRoom().getNumAdjRooms());
        assertNotNull(gameMap.getInitialRoom().getNorthRoom());
        assertNotNull(gameMap.getInitialRoom().getEastRoom());
        assertNotNull(gameMap.getInitialRoom().getSouthRoom());
        assertNotNull(gameMap.getInitialRoom().getWestRoom());
    }

    // Ray 5
    @RepeatedTest(500)
    void testAtLeast2ChallengeRooms() {
        var gameMap = new GameMap(rooms);
        gameMap.generateRooms();
        int challengeRoomCount = 0;
        for (Room room: gameMap.getRooms().values()) {
            if (room.getRoomType().equals("challengeRoom")) {
                ++challengeRoomCount;
            }
        }
        assert challengeRoomCount >= 2;
    }

    // Ray 6
    @Test
    void testCannotGoToBossRoomBeforeVisiting2ChallengeRooms() {
        var gameMap = new GameMap(rooms);
        gameMap.generateRooms();
        TeleportToBossRoom teleportToBossRoom = new TeleportToBossRoom("");
        assert !teleportToBossRoom.canProceed(gameMap);

        int toVisit = 2;
        for (Room room: gameMap.getRooms().values()) {
            if (room.getRoomType().equals("challengeRoom")) {
                room.setVisited(true);
                --toVisit;
                if (toVisit <= 0) {
                    break;
                }
            }
        }

        assert teleportToBossRoom.canProceed(gameMap);

    }
}