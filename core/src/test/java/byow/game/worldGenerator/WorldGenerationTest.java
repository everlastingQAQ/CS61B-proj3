package byow.game.worldGenerator;

import byow.game.Engine;
import byow.game.GameState;
import byow.game.GameTestSupport;
import byow.game.pathfinder.Pathfinder;
import byow.game.save.SaveManager;
import byow.game.tile.TileConverter;
import byow.game.tile.TileRules;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class WorldGenerationTest {
    @TempDir Path directory;

    @Test
    void oneHundredSeedsHaveConnectedTerrainAndReachableRelics() {
        for (int seed = 0; seed < 100; seed++) {
            Engine engine = new Engine(new SaveManager(directory));
            GameTestSupport.keys(engine, "n" + seed + "s");
            assertEquals(GameState.PLAYING, engine.state(), "seed=" + seed);
            int[][] distances = Pathfinder.distancesFrom(engine.world(), engine.player().x(), engine.player().y());
            for (int x = 0; x < engine.world().length; x++) {
                for (int y = 0; y < engine.world()[0].length; y++) {
                    if (TileRules.isWalkable(engine.world()[x][y])) {
                        assertTrue(distances[x][y] >= 0, "seed=" + seed + " at " + x + "," + y);
                    }
                }
            }
            assertEquals(5, engine.items().size());
            assertEquals(5, engine.items().stream().map(item -> item.type()).distinct().count());
            for (var item : engine.items()) assertTrue(distances[item.x()][item.y()] >= 0);
        }
    }

    @Test
    void twentySeedsReproduceSameTerrainAndPlayerSpawn() {
        for (int seed = 0; seed < 20; seed++) {
            Engine first = new Engine(new SaveManager(directory));
            Engine second = new Engine(new SaveManager(directory));
            GameTestSupport.keys(first, "n" + seed + "s");
            GameTestSupport.keys(second, "n" + seed + "s");
            assertArrayEquals(TileConverter.toTileType(first.world()), TileConverter.toTileType(second.world()));
            assertEquals(first.player().x(), second.player().x());
            assertEquals(first.player().y(), second.player().y());
        }
    }
}
