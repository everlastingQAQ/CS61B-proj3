package byow.game.visioncauculate;

import byow.game.player.Player;
import byow.game.tile.TETile;
import byow.game.tile.Tileset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisioncauculateTest {

    @Test
    void revealsDiagonalWallCornerAtVisionBoundary() {
        TETile[][] world = openWorld(7, 7);
        world[4][4] = Tileset.WALL;
        world[5][5] = Tileset.WALL;

        boolean[][] visible = Visioncauculate.calculate(
            world,
            new Player(world, 2, 2),
            2
        );

        assertTrue(visible[3][3]);
        assertTrue(visible[4][4]);
        assertFalse(visible[5][5]);
    }

    private static TETile[][] openWorld(int width, int height) {
        TETile[][] world = new TETile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean border = x == 0 || y == 0
                    || x == width - 1 || y == height - 1;
                world[x][y] = border ? Tileset.WALL : Tileset.FLOOR;
            }
        }
        return world;
    }
}
