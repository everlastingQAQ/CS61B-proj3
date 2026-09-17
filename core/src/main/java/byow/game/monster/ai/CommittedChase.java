package byow.game.monster.ai;

import byow.game.monster.Monster;
import byow.game.pathfinder.Pathfinder;
import byow.game.tile.TETile;

/** 使用固定回合目标锁定实现所有追击状态的共同惯性。 */
public final class CommittedChase {
    private static final int TARGET_LOCK_TURNS = 2;

    /**
     * 必要时锁定候选目标，并返回前往已锁定目标的普通 BFS 下一步。
     *
     * @param refreshTarget 是否立即用候选目标覆盖旧目标
     */
    public static Pathfinder.Position nextStep(
        TETile[][] world,
        Monster monster,
        int candidateTargetX,
        int candidateTargetY,
        boolean refreshTarget
    ) {
        if (refreshTarget || !monster.hasCommittedTarget()) {
            monster.commitTarget(
                candidateTargetX,
                candidateTargetY,
                TARGET_LOCK_TURNS
            );
        }

        Pathfinder.Position next = Pathfinder.nextStep(
            world,
            monster.x(),
            monster.y(),
            monster.committedTargetX(),
            monster.committedTargetY()
        );
        monster.consumeCommitmentTurn();
        return next;
    }

    private CommittedChase() {}
}
