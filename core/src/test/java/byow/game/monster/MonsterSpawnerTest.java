package byow.game.monster;

import byow.game.item.Item;
import byow.game.item.ItemType;
import byow.game.player.Player;
import byow.game.random.GameRandom;
import byow.game.tile.TETile;
import byow.game.tile.Tileset;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 验证新怪物的类型、出生距离和占位规则。 */
class MonsterSpawnerTest {

    @Test
    void spawnedMonsterKeepsRequestedTypeAndAvoidsOccupiedPositions() {
        TETile[][] world = openWorld(20, 20);
        Player player = new Player(world, 2, 2);
        Monster existing = new Monster(1, 15, 15, MonsterType.HUNTER);

        Monster spawned = MonsterSpawner.spawn(
            2,
            world,
            new GameRandom(42),
            player,
            List.of(existing),
            List.of(),
            MonsterType.GUARDIAN
        );

        assertEquals(2, spawned.id());
        assertEquals(MonsterType.GUARDIAN, spawned.type());
        assertTrue(spawned.distanceTo(player.x(), player.y()) >= 10);
        assertFalse(spawned.x() == existing.x() && spawned.y() == existing.y());
    }

    @Test
    void spawnedMonsterAvoidsRemainingItems() {
        TETile[][] world = openWorld(5, 5);
        Player player = new Player(world, 2, 2);
        List<Item> items = List.of(
            new Item(1, 2, ItemType.CRYSTAL),
            new Item(1, 3, ItemType.COIN),
            new Item(2, 1, ItemType.KEY),
            new Item(2, 3, ItemType.ORB),
            new Item(3, 1, ItemType.RUNE),
            new Item(3, 2, ItemType.CRYSTAL),
            new Item(3, 3, ItemType.COIN)
        );

        Monster spawned = MonsterSpawner.spawn(
            1,
            world,
            new GameRandom(42),
            player,
            List.of(),
            items,
            MonsterType.HUNTER
        );

        assertEquals(1, spawned.x());
        assertEquals(1, spawned.y());
    }

    /** 创建外圈为墙、内部全部可行走的测试世界。 */
    private static TETile[][] openWorld(int width, int height) {
        TETile[][] world = new TETile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean border = x == 0 || y == 0 || x == width - 1 || y == height - 1;
                world[x][y] = border ? Tileset.WALL : Tileset.FLOOR;
            }
        }
        return world;
    }
}
