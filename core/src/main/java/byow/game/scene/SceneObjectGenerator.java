package byow.game.scene;

import byow.game.GameExit;
import byow.game.item.Item;
import byow.game.player.Player;
import byow.game.random.GameRandom;
import byow.game.tile.TETile;
import byow.game.tile.TileRules;

import java.util.ArrayList;
import java.util.List;

/** 在不覆盖玩家、遗物或出口的位置生成场景物件。 */
public final class SceneObjectGenerator {
    private static final int TRAP_COUNT = 3;
    private static final int MIN_PORTAL_DISTANCE = 25;

    public static List<SceneObject> generate(
        TETile[][] world,
        Player player,
        List<Item> items,
        GameExit exit,
        GameRandom random
    ) {
        List<Position> available = collectAvailable(world, player, items, exit);
        int required = TRAP_COUNT + 4;
        if (available.size() < required) {
            throw new IllegalStateException("Not enough space for scene objects");
        }

        List<SceneObject> result = new ArrayList<>();
        for (int i = 0; i < TRAP_COUNT; i++) {
            Position position = takeRandom(available, random);
            result.add(new SceneObject(position.x(), position.y(), SceneObjectType.SPIKE_TRAP));
        }

        Position fountain = takeRandom(available, random);
        result.add(new SceneObject(
            fountain.x(), fountain.y(), SceneObjectType.HEALING_FOUNTAIN
        ));

        Position firstPortal = takeRandom(available, random);
        Position secondPortal = takeDistant(available, firstPortal, random);
        result.add(SceneObject.portal(
            firstPortal.x(), firstPortal.y(), secondPortal.x(), secondPortal.y()
        ));
        result.add(SceneObject.portal(
            secondPortal.x(), secondPortal.y(), firstPortal.x(), firstPortal.y()
        ));

        Position chest = takeRandom(available, random);
        result.add(new SceneObject(
            chest.x(), chest.y(), SceneObjectType.BLESSING_CHEST
        ));
        return result;
    }

    private static List<Position> collectAvailable(
        TETile[][] world,
        Player player,
        List<Item> items,
        GameExit exit
    ) {
        List<Position> positions = new ArrayList<>();
        for (int x = 1; x < world.length - 1; x++) {
            for (int y = 1; y < world[0].length - 1; y++) {
                if (!TileRules.canHostItem(world[x][y])
                    || player.x() == x && player.y() == y
                    || exit.contains(x, y)
                    || hasItemAt(items, x, y)) {
                    continue;
                }
                positions.add(new Position(x, y));
            }
        }
        return positions;
    }

    private static Position takeRandom(List<Position> positions, GameRandom random) {
        return positions.remove(random.nextInt(positions.size()));
    }

    private static Position takeDistant(
        List<Position> positions,
        Position origin,
        GameRandom random
    ) {
        List<Position> distant = new ArrayList<>();
        for (Position candidate : positions) {
            int distance = Math.abs(candidate.x() - origin.x())
                + Math.abs(candidate.y() - origin.y());
            if (distance >= MIN_PORTAL_DISTANCE) {
                distant.add(candidate);
            }
        }
        Position selected = distant.isEmpty()
            ? positions.get(random.nextInt(positions.size()))
            : distant.get(random.nextInt(distant.size()));
        positions.remove(selected);
        return selected;
    }

    private static boolean hasItemAt(List<Item> items, int x, int y) {
        for (Item item : items) {
            if (item.x() == x && item.y() == y) {
                return true;
            }
        }
        return false;
    }

    private record Position(int x, int y) {}

    private SceneObjectGenerator() {}
}
