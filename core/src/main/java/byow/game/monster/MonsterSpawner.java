package byow.game.monster;

import byow.game.item.Item;
import byow.game.player.Player;
import byow.game.random.GameRandom;
import byow.game.tile.TETile;
import byow.game.tile.TileRules;

import java.util.ArrayList;
import java.util.List;

/** 为新激活的怪物选择合法且不会重叠的出生位置。 */
public final class MonsterSpawner {
    private static final int MIN_PLAYER_DISTANCE = 10;

    /**
     * 在可行走位置创建指定类型的怪物。
     * 优先选择与玩家曼哈顿距离至少为 10 的位置；地图空间不足时退回到任意合法位置。
     *
     * @param id 新怪物的稳定 ID
     * @param world 当前世界
     * @param random 当前游戏随机数生成器
     * @param player 当前玩家
     * @param monsters 已经存在的怪物
     * @param items 尚未拾取的遗物
     * @param type 新怪物类型
     * @return 位于合法出生位置的新怪物
     */
    public static Monster spawn(
        long id,
        TETile[][] world,
        GameRandom random,
        Player player,
        List<Monster> monsters,
        List<Item> items,
        MonsterType type
    ) {
        List<Position> distantPositions = collectPositions(
            world,
            player,
            monsters,
            items,
            true
        );

        // 小地图可能不存在足够远的位置，此时只保留可行走和不重叠要求。
        List<Position> candidates = distantPositions.isEmpty()
            ? collectPositions(world, player, monsters, items, false)
            : distantPositions;

        if (candidates.isEmpty()) {
            throw new IllegalStateException("No available monster spawn position");
        }

        Position origin = candidates.get(random.nextInt(candidates.size()));
        return new Monster(id, origin.x(), origin.y(), type);
    }

    /** 收集满足距离要求的全部合法出生位置。 */
    private static List<Position> collectPositions(
        TETile[][] world,
        Player player,
        List<Monster> monsters,
        List<Item> items,
        boolean requireDistance
    ) {
        List<Position> positions = new ArrayList<>();

        for (int x = 1; x < world.length - 1; x++) {
            for (int y = 1; y < world[0].length - 1; y++) {
                if (!TileRules.isWalkable(world[x][y])) {
                    continue;
                }
                if (player.x() == x && player.y() == y) {
                    continue;
                }
                if (isOccupied(monsters, x, y)) {
                    continue;
                }
                if (hasItemAt(items, x, y)) {
                    continue;
                }
                if (requireDistance
                    && Math.abs(x - player.x()) + Math.abs(y - player.y())
                    < MIN_PLAYER_DISTANCE) {
                    continue;
                }
                positions.add(new Position(x, y));
            }
        }

        return positions;
    }

    /** 判断指定位置是否已经被怪物占据。 */
    private static boolean isOccupied(List<Monster> monsters, int x, int y) {
        for (Monster monster : monsters) {
            if (monster.x() == x && monster.y() == y) {
                return true;
            }
        }
        return false;
    }

    /** 判断指定位置是否保留给尚未拾取的遗物。 */
    private static boolean hasItemAt(List<Item> items, int x, int y) {
        for (Item item : items) {
            if (item.x() == x && item.y() == y) {
                return true;
            }
        }
        return false;
    }

    /** 保存生成候选位置。 */
    private record Position(int x, int y) {}

    private MonsterSpawner() {}
}
