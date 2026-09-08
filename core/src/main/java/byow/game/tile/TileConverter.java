package byow.game.tile;

/**
 * 用于 TileType 和 TETile 之间的转换。
 *
 * TileType 用于保存游戏数据。
 * TETile 用于渲染显示。
 *
 * @author everlasting
 */
public class TileConverter {


    /**
     * 将 TETile 世界转换为 TileType 世界。
     *
     * @param world 当前游戏世界
     * @return 用于保存的世界数据
     */
    public static TileType[][] toTileType(TETile[][] world) {

        TileType[][] result = new TileType[world.length][world[0].length];

        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                result[x][y] = toTileType(world[x][y]);
            }
        }

        return result;
    }


    /**
     * 将 TileType 世界转换为 TETile 世界。
     *
     * @param world 保存的数据
     * @return 用于渲染的世界
     */
    public static TETile[][] toTETile(TileType[][] world) {

        TETile[][] result = new TETile[world.length][world[0].length];

        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                result[x][y] = toTETile(world[x][y]);
            }
        }

        return result;
    }

    /**
     * 单个 TETile 转 TileType。
     */
    private static TileType toTileType(TETile tile) {

        return switch (tile.description()) {

            case "you" -> TileType.FLOOR;//TODO 分离人物和地图
            case "wall" -> TileType.WALL;
            case "floor" -> TileType.FLOOR;
            case "nothing" -> TileType.NOTHING;
            case "grass" -> TileType.GRASS;
            case "water" -> TileType.WATER;
            case "flower" -> TileType.FLOWER;
            case "locked door" -> TileType.CLOSED_DOOR;
            case "unlocked door" -> TileType.OPEN_DOOR;
            case "sand" -> TileType.SAND;
            case "mountain" -> TileType.MOUNTAIN;
            case "tree" -> TileType.TREE;
            case "rock" -> TileType.ROCK;
            case "bridge" -> TileType.BRIDGE;

            default ->
                throw new IllegalArgumentException(
                    "Unknown tile: " + tile.description()
                );
        };
    }

    /**
     * 单个 TileType 转 TETile。
     */
    private static TETile toTETile(TileType type) {

        return switch (type) {
            case AVATAR -> Tileset.AVATAR;
            case WALL -> Tileset.WALL;
            case FLOOR -> Tileset.FLOOR;
            case NOTHING -> Tileset.NOTHING;
            case GRASS -> Tileset.GRASS;
            case WATER -> Tileset.WATER;
            case FLOWER -> Tileset.FLOWER;
            case CLOSED_DOOR -> Tileset.CLOSED_DOOR;
            case OPEN_DOOR -> Tileset.OPEN_DOOR;
            case SAND -> Tileset.SAND;
            case MOUNTAIN -> Tileset.MOUNTAIN;
            case TREE -> Tileset.TREE;
            case ROCK -> Tileset.ROCK;
            case BRIDGE -> Tileset.BRIDGE;
        };
    }

    private TileConverter() {
    }
}
