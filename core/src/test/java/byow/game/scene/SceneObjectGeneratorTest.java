package byow.game.scene;

import byow.game.GameExit;
import byow.game.player.Player;
import byow.game.random.GameRandom;
import byow.game.tile.TETile;
import byow.game.tile.TileRules;
import byow.game.tile.Tileset;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SceneObjectGeneratorTest {
    @Test
    void generatesUniqueWalkableObjectsAndLinkedPortals() {
        TETile[][] world = openWorld(50, 35);
        Player player = new Player(world, 2, 2);
        GameExit exit = GameExit.place(world, player);

        List<SceneObject> objects = SceneObjectGenerator.generate(
            world, player, List.of(), exit, new GameRandom(1234)
        );

        assertEquals(7, objects.size());
        assertEquals(3, count(objects, SceneObjectType.SPIKE_TRAP));
        assertEquals(1, count(objects, SceneObjectType.HEALING_FOUNTAIN));
        assertEquals(2, count(objects, SceneObjectType.PORTAL));
        assertEquals(1, count(objects, SceneObjectType.BLESSING_CHEST));

        Set<String> positions = new HashSet<>();
        for (SceneObject object : objects) {
            assertTrue(TileRules.isWalkable(world[object.x()][object.y()]));
            assertTrue(positions.add(object.x() + ":" + object.y()));
        }

        List<SceneObject> portals = objects.stream()
            .filter(object -> object.type() == SceneObjectType.PORTAL)
            .toList();
        assertEquals(portals.get(1).x(), portals.get(0).targetX());
        assertEquals(portals.get(1).y(), portals.get(0).targetY());
        assertEquals(portals.get(0).x(), portals.get(1).targetX());
        assertEquals(portals.get(0).y(), portals.get(1).targetY());
    }

    private static long count(List<SceneObject> objects, SceneObjectType type) {
        return objects.stream().filter(object -> object.type() == type).count();
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
