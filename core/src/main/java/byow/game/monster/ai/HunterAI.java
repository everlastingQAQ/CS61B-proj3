package byow.game.monster.ai;

import byow.game.monster.Monster;
import byow.game.pathfinder.Pathfinder;
import byow.game.player.Player;
import byow.game.tile.TETile;

/** 锁定玩家位置，并通过 BFS 持续追踪两回合的怪物 AI。 */
public class HunterAI implements MonsterAI {

    /**
     * 返回怪物向当前承诺目标移动的下一步。
     *
     * @param world 当前世界
     * @param monster 当前怪物
     * @param player 当前玩家
     * @return 怪物的下一步；没有可达路径时返回 null
     */
    @Override
    public Pathfinder.Position decideNextStep(
        TETile[][] world,
        Monster monster,
        Player player
    ) {
        return CommittedChase.nextStep(
            world,
            monster,
            player.x(),
            player.y(),
            false
        );
    }
}
