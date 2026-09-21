package byow.game;

import byow.game.player.Player;
import byow.game.tile.TETile;
import byow.game.tile.TileRules;
import byow.game.tile.Tileset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameExitTest {

    @Test
    void exitStartsClosedAtFarthestReachablePosition() {
        TETile[][] world = corridorWorld();
        Player player = new Player(world, 1, 2);

        GameExit exit = GameExit.place(world, player);

        assertEquals(5, exit.x());
        assertEquals(3, exit.y());
        assertFalse(exit.active());
        assertEquals(Tileset.CLOSED_DOOR, world[exit.x()][exit.y()]);
        assertEquals(Tileset.CLOSED_DOOR, exit.appearance());
        assertEquals('X', exit.appearance().character());
        assertFalse(TileRules.isWalkable(world[exit.x()][exit.y()]));
    }

    @Test
    void activatedExitBecomesWalkable() {
        TETile[][] world = corridorWorld();
        GameExit exit = new GameExit(5, 1, false);
        exit.syncTile(world);

        exit.activate(world);

        assertTrue(exit.active());
        assertEquals(Tileset.OPEN_DOOR, world[5][1]);
        assertEquals(Tileset.OPEN_DOOR, exit.appearance());
        assertTrue(TileRules.isWalkable(world[5][1]));
        assertTrue(exit.contains(5, 1));
    }

    private static TETile[][] corridorWorld() {
        TETile[][] world = new TETile[7][5];
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                world[x][y] = y == 2 && x > 0 && x < world.length - 1
                    ? Tileset.FLOOR
                    : Tileset.WALL;
            }
        }
        return world;
    }
}
