package byow.game.worldGenerator;

import byow.game.random.GameRandom;
import byow.game.tile.TETile;
import byow.game.tile.TileRules;
import byow.game.tile.Tileset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DecorationGeneratorTest {

    @Test
    void decorationIsDeterministicAndKeepsGroundWalkable() {
        TETile[][] first = openWorld(41, 31);
        TETile[][] second = openWorld(41, 31);

        DecorationGenerator.decorate(first, new GameRandom(42));
        DecorationGenerator.decorate(second, new GameRandom(42));

        int decoratedCount = 0;
        for (int x = 0; x < first.length; x++) {
            for (int y = 0; y < first[0].length; y++) {
                assertEquals(first[x][y].description(), second[x][y].description());
                if (x == 0 || y == 0 || x == first.length - 1
                    || y == first[0].length - 1) {
                    assertEquals(Tileset.WALL, first[x][y]);
                    continue;
                }
                assertTrue(TileRules.isWalkable(first[x][y]));
                if (!first[x][y].equals(Tileset.FLOOR)) {
                    decoratedCount++;
                }
            }
        }
        assertTrue(decoratedCount > 100);
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
