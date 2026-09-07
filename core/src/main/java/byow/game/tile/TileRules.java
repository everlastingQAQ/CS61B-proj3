package byow.game.tile;

import static byow.game.tile.Tileset.*;

public final class TileRules {

    private TileRules() {
    }

    public static boolean isWalkable(TETile tile) {
        return tile.equals(FLOOR)
            || tile.equals(GRASS)
            || tile.equals(FLOWER)
            || tile.equals(OPEN_DOOR)
            || tile.equals(SAND)
            || tile.equals(BRIDGE);
    }

    public static boolean isVisible(TETile tile) {
        return !tile.equals(WALL) && !tile.equals(CLOSED_DOOR);
    }
}
