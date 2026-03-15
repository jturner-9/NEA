import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProceduralLevelGenerator {

    private static final int TILE_SIZE = 32;
    private static final int MAP_WIDTH_TILES = 64;
    private static final int MAP_HEIGHT_TILES = 64;
    private static final int ROOM_COUNT = 10;
    private static final int MIN_ROOM_SIZE = 6;
    private static final int MAX_ROOM_SIZE = 12;

    private final Random random = new Random();

    public GeneratedLevel generate() {
        boolean[][] floorTiles = new boolean[MAP_WIDTH_TILES][MAP_HEIGHT_TILES];
        List<Rectangle> rooms = buildRooms(floorTiles);
        connectRooms(floorTiles, rooms);

        Point playerSpawn = randomPointInRoom(rooms.get(random.nextInt(rooms.size())));
        List<Point> enemySpawns = new ArrayList<>();

        for (Rectangle room : rooms) {
            Point enemySpawn = randomPointInRoom(room);
            if (enemySpawn.equals(playerSpawn)) {
                enemySpawn = new Point(room.x + room.width / 2, room.y + room.height / 2);
            }
            if (!enemySpawn.equals(playerSpawn)) {
                enemySpawns.add(enemySpawn);
            }
        }

        return new GeneratedLevel(floorTiles, playerSpawn, enemySpawns);
    }

    private List<Rectangle> buildRooms(boolean[][] floorTiles) {
        List<Rectangle> rooms = new ArrayList<>();
        int attempts = 0;

        while (rooms.size() < ROOM_COUNT && attempts < 200) {
            attempts++;
            Rectangle room = randomRoom();
            if (overlapsAny(room, rooms)) {
                continue;
            }
            carveRoom(floorTiles, room);
            rooms.add(room);
        }

        if (rooms.isEmpty()) {
            Rectangle fallback = new Rectangle(2, 2, 10, 10);
            carveRoom(floorTiles, fallback);
            rooms.add(fallback);
        }

        return rooms;
    }

    private Rectangle randomRoom() {
        int width = MIN_ROOM_SIZE + random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1);
        int height = MIN_ROOM_SIZE + random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1);
        int x = 1 + random.nextInt(MAP_WIDTH_TILES - width - 2);
        int y = 1 + random.nextInt(MAP_HEIGHT_TILES - height - 2);
        return new Rectangle(x, y, width, height);
    }

    private boolean overlapsAny(Rectangle room, List<Rectangle> rooms) {
        Rectangle paddedRoom = new Rectangle(room.x - 1, room.y - 1, room.width + 2, room.height + 2);
        for (Rectangle existingRoom : rooms) {
            if (paddedRoom.intersects(existingRoom)) {
                return true;
            }
        }
        return false;
    }

    private void carveRoom(boolean[][] floorTiles, Rectangle room) {
        for (int x = room.x; x < room.x + room.width; x++) {
            for (int y = room.y; y < room.y + room.height; y++) {
                floorTiles[x][y] = true;
            }
        }
    }

    private void connectRooms(boolean[][] floorTiles, List<Rectangle> rooms) {
        for (int i = 1; i < rooms.size(); i++) {
            Point from = centerOf(rooms.get(i - 1));
            Point to = centerOf(rooms.get(i));
            carveCorridor(floorTiles, from, to);
        }
    }

    private Point centerOf(Rectangle room) {
        return new Point(room.x + room.width / 2, room.y + room.height / 2);
    }

    private void carveCorridor(boolean[][] floorTiles, Point start, Point end) {
        int x = start.x;
        int y = start.y;

        while (x != end.x) {
            floorTiles[x][y] = true;
            x += Integer.compare(end.x, x);
        }
        while (y != end.y) {
            floorTiles[x][y] = true;
            y += Integer.compare(end.y, y);
        }
        floorTiles[x][y] = true;
    }

    private Point randomPointInRoom(Rectangle room) {
        int x = room.x + random.nextInt(room.width);
        int y = room.y + random.nextInt(room.height);
        return new Point(x, y);
    }

    public static class GeneratedLevel {
        private final boolean[][] floorTiles;
        private final Point playerSpawnTile;
        private final List<Point> enemySpawnTiles;

        public GeneratedLevel(boolean[][] floorTiles, Point playerSpawnTile, List<Point> enemySpawnTiles) {
            this.floorTiles = floorTiles;
            this.playerSpawnTile = playerSpawnTile;
            this.enemySpawnTiles = enemySpawnTiles;
        }

        public boolean[][] getFloorTiles() {
            return floorTiles;
        }

        public Point getPlayerSpawnTile() {
            return playerSpawnTile;
        }

        public List<Point> getEnemySpawnTiles() {
            return enemySpawnTiles;
        }

        public int getTileSize() {
            return TILE_SIZE;
        }

        public int getWidthTiles() {
            return MAP_WIDTH_TILES;
        }

        public int getHeightTiles() {
            return MAP_HEIGHT_TILES;
        }
    }
}
