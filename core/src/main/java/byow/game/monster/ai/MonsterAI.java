package byow.game.monster.ai;

import byow.game.monster.Monster;
import byow.game.pathfinder.Pathfinder;
import byow.game.player.Player;
import byow.game.tile.TETile;

public interface MonsterAI {
    Pathfinder.Position decideNextStep(
        TETile[][] world,
        Monster monster,
        Player player
    );
}
