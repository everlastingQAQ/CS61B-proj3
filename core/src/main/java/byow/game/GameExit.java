package byow.game;

import byow.game.pathfinder.Pathfinder;
import byow.game.player.Player;
import byow.game.tile.TETile;
import byow.game.tile.TileRules;
import byow.game.tile.Tileset;

/** 游戏出口。出口在激活前不可通行，收集足够遗物后变为开放状态。 */
public final class GameExit {
    private static final int[][] DIRECTIONS = {
        {0, 1},
        {1, 0},
        {0, -1},
        {-1, 0}
    };

    private final int x;
    private final int y;
    private boolean active;

    public GameExit(int x, int y, boolean active) {
        this.x = x;
        this.y = y;
        this.active = active;
    }

    /**
     * 在远离玩家的可达区域旁选择一面墙作为出口。
     * 出口占用墙格，因此关闭时不会切断原本连通的道路。
     */
    public static GameExit place(TETile[][] world, Player player) {
        int[][] distances = Pathfinder.distancesFrom(
            world,
            player.x(),
            player.y()
        );

        Pathfinder.Position position = null;
        int bestDistance = -1;
        for (int x = 1; x < world.length - 1; x++) {
            for (int y = 1; y < world[0].length - 1; y++) {
                if (distances[x][y] < bestDistance || !TileRules.isWalkable(world[x][y])) {
                    continue;
                }
                for (int[] direction : DIRECTIONS) {
                    int doorX = x + direction[0];
                    int doorY = y + direction[1];
                    if (!isInterior(world, doorX, doorY)
                        || !world[doorX][doorY].equals(Tileset.WALL)) {
                        continue;
                    }
                    position = new Pathfinder.Position(doorX, doorY);
                    bestDistance = distances[x][y];
                    break;
                }
            }
        }

        if (position == null) {
            throw new IllegalStateException("No wall is available for the game exit");
        }

        GameExit exit = new GameExit(position.x(), position.y(), false);
        exit.syncTile(world);
        return exit;
    }

    private static boolean isInterior(TETile[][] world, int x, int y) {
        return x > 0 && x < world.length - 1 && y > 0 && y < world[0].length - 1;
    }

    public void activate(TETile[][] world) {
        active = true;
        syncTile(world);
    }

    /** 让地图地砖与保存的出口状态保持一致。 */
    public void syncTile(TETile[][] world) {
        world[x][y] = active ? Tileset.OPEN_DOOR : Tileset.CLOSED_DOOR;
    }

    public boolean contains(int targetX, int targetY) {
        return x == targetX && y == targetY;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public boolean active() {
        return active;
    }

    /** 返回当前状态对应的出口外观，关闭时也拥有独立可识别的字符。 */
    public TETile appearance() {
        return active ? Tileset.OPEN_DOOR : Tileset.CLOSED_DOOR;
    }
}
