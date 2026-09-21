package byow.game.tile;

public final class TileRules {

    private TileRules() {
    }

    public static boolean isWalkable(TETile tile) {
        return TileType.fromDescription(tile.description()).isWalkable();
    }

    public static boolean isTransparent(TETile tile) {
        return TileType.fromDescription(tile.description()).isTransparent();
    }

    /** 物品可以生成在普通地面及其纯视觉装饰变体上。 */
    public static boolean canHostItem(TETile tile) {
        TileType type = TileType.fromDescription(tile.description());
        return type == TileType.FLOOR
            || type == TileType.GRASS
            || type == TileType.FLOWER
            || type == TileType.SAND
            || type == TileType.MOSS
            || type == TileType.CRACKED_FLOOR
            || type == TileType.MUD
            || type == TileType.PEBBLES;
    }
}
