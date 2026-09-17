package byow.game.monster;

import byow.game.monster.ai.HunterAI;
import byow.game.player.Player;
import byow.game.save.MonsterData;
import byow.game.tile.TETile;
import byow.game.tile.Tileset;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonsterBehaviorTest {

    @Test
    void hunterKeepsTargetForTwoTurns() {
        TETile[][] world = openWorld(7, 5);
        Monster hunter = new Monster(1, 1, 2, MonsterType.HUNTER);
        HunterAI ai = new HunterAI();

        ai.decideNextStep(world, hunter, new Player(world, 5, 2));
        assertEquals(5, hunter.committedTargetX());
        assertEquals(1, hunter.commitmentTurns());

        ai.decideNextStep(world, hunter, new Player(world, 4, 2));
        assertEquals(5, hunter.committedTargetX());
        assertEquals(0, hunter.commitmentTurns());

        ai.decideNextStep(world, hunter, new Player(world, 3, 2));
        assertEquals(3, hunter.committedTargetX());
        assertEquals(1, hunter.commitmentTurns());
    }

    @Test
    void lowerIdWinsWhenTwoMonstersChooseSameDestination() {
        TETile[][] world = walledWorld(7, 5);
        setFloor(world, 1, 2, 2, 2, 2, 1, 3, 2, 4, 2);

        Monster first = new Monster(1, 1, 2, MonsterType.HUNTER);
        Monster second = new Monster(2, 2, 1, MonsterType.HUNTER);
        Player player = new Player(world, 4, 2);

        new MonsterController().takeTurn(world, List.of(first, second), player);

        assertEquals(2, first.x());
        assertEquals(2, first.y());
        assertEquals(2, second.x());
        assertEquals(1, second.y());
    }

    @Test
    void monsterCannotEnterPositionOccupiedAtTurnStart() {
        TETile[][] world = walledWorld(7, 5);
        setFloor(world, 1, 2, 2, 2, 3, 2, 4, 2);

        Monster follower = new Monster(1, 1, 2, MonsterType.HUNTER);
        Monster leader = new Monster(2, 2, 2, MonsterType.HUNTER);
        Player player = new Player(world, 4, 2);

        new MonsterController().takeTurn(world, List.of(follower, leader), player);

        assertEquals(1, follower.x());
        assertEquals(3, leader.x());
    }

    @Test
    void monsterDataRoundTripPreservesBehaviorState() {
        Monster original = new Monster(
            9,
            4,
            3,
            MonsterType.PATROLLER,
            MonsterState.PATROLLING,
            2,
            2,
            true,
            1,
            1,
            8,
            6,
            1,
            7,
            5,
            2
        );

        MonsterData data = MonsterData.from(original);
        Monster restored = data.toMonster();

        assertEquals(data, MonsterData.from(restored));
    }

    @Test
    void movingOntoPlayerCreatesContactWithoutOverlap() {
        TETile[][] world = openWorld(5, 5);
        Monster hunter = new Monster(1, 1, 2, MonsterType.HUNTER);
        Player player = new Player(world, 2, 2);

        MonsterTurnResult result = new MonsterController()
            .takeTurn(world, List.of(hunter), player);

        assertEquals(1, hunter.x());
        assertEquals(2, hunter.y());
        assertEquals(List.of(hunter), result.contacts());
    }

    private static TETile[][] openWorld(int width, int height) {
        TETile[][] world = walledWorld(width, height);
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                world[x][y] = Tileset.FLOOR;
            }
        }
        return world;
    }

    private static TETile[][] walledWorld(int width, int height) {
        TETile[][] world = new TETile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world[x][y] = Tileset.WALL;
            }
        }
        return world;
    }

    private static void setFloor(TETile[][] world, int... coordinates) {
        assertTrue(coordinates.length % 2 == 0);
        for (int i = 0; i < coordinates.length; i += 2) {
            world[coordinates[i]][coordinates[i + 1]] = Tileset.FLOOR;
        }
    }
}
