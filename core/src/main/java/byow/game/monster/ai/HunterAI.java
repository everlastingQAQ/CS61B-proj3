package byow.game.monster.ai;

import byow.game.monster.Monster;
import byow.game.pathfinder.Pathfinder;
import byow.game.player.Player;
import byow.game.tile.TETile;

/** 锁定玩家位置，并通过 BFS 持续追踪两回合的怪物 AI。 */
public class HunterAI implements MonsterAI {

    /** 每次锁定玩家位置后持续追踪的回合数。 */
    private static final int COMMITMENT_TURNS = 2;

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
        // 旧目标到期后，记录玩家当前的位置作为新的追踪目标。
        if (!monster.hasCommittedTarget()) {
            monster.commitTarget(
                player.x(),
                player.y(),
                COMMITMENT_TURNS
            );
        }

        // 使用允许掉头的普通 BFS，避免目标承诺和禁止掉头相互叠加。
        Pathfinder.Position next = Pathfinder.nextStep(
            world,
            monster.x(),
            monster.y(),
            monster.committedTargetX(),
            monster.committedTargetY()
        );

        // 无论本回合是否找到路径，都消耗一个目标承诺回合。
        monster.consumeCommitmentTurn();
        return next;
    }
}
