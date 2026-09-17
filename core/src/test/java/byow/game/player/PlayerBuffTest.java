package byow.game.player;

import byow.game.tile.TETile;
import byow.game.tile.Tileset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 验证不依赖 Engine 的玩家 Buff 基础状态。 */
class PlayerBuffTest {

    @Test
    void healStopsAtMaximumHealth() {
        Player player = new Player(openWorld(), 2, 2, 0, 0, 1, 3);

        player.heal(1);
        assertEquals(2, player.currentHp());

        player.heal(10);
        assertEquals(3, player.currentHp());
    }

    @Test
    void shieldChargeCanBeConsumedOnce() {
        Player player = new Player(openWorld(), 2, 2);

        player.grantShield(1);

        assertEquals(1, player.shieldCharges());
        assertTrue(player.consumeShield());
        assertFalse(player.consumeShield());
    }

    @Test
    void damageStopsAtZeroHealth() {
        Player player = new Player(openWorld(), 2, 2, 0, 0, 2, 3);

        player.takeDamage(1);
        assertEquals(1, player.currentHp());
        assertTrue(player.isAlive());

        player.takeDamage(10);
        assertEquals(0, player.currentHp());
        assertFalse(player.isAlive());
    }

    @Test
    void savedShieldChargesCanBeRestored() {
        Player player = new Player(openWorld(), 2, 2, 0, 0, 3, 3, 2);

        assertEquals(2, player.shieldCharges());
    }

    /** 创建供玩家状态测试使用的简单可行走世界。 */
    private static TETile[][] openWorld() {
        TETile[][] world = new TETile[5][5];
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                boolean border = x == 0 || y == 0
                    || x == world.length - 1 || y == world[0].length - 1;
                world[x][y] = border ? Tileset.WALL : Tileset.FLOOR;
            }
        }
        return world;
    }
}
