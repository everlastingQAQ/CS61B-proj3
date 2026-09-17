package byow.game.monster;

import byow.game.monster.ai.*;
import byow.game.pathfinder.Pathfinder;
import byow.game.player.Player;
import byow.game.tile.TETile;

public class MonsterController {

    public void takeTurn(
        TETile[][] world,
        Monster monster,
        Player player
    ) {
        MonsterAI ai = createAI(monster.type());

        Pathfinder.Position next =
            ai.decideNextStep(world, monster, player);

        if (next == null) {
            return;
        }

        monster.moveTo(next.x(), next.y());
    }

    private MonsterAI createAI(MonsterType type) {
        return switch (type) {
            case HUNTER -> new HunterAI();
            case AMBUSHER -> new AmbusherAI();
            case GUARDIAN -> new GuardianAI();
            case PATROLLER -> new PatrollerAI();
            case COWARD -> new CowardAI();
        };
    }
}
