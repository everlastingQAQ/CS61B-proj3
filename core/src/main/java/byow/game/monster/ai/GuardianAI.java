package byow.game.monster.ai;

import byow.game.monster.Monster;
import byow.game.monster.MonsterState;
import byow.game.pathfinder.Pathfinder;
import byow.game.player.Player;
import byow.game.tile.TETile;

/** 守护固定位置，并在玩家靠近时追击的怪物 AI。 */
public class GuardianAI implements MonsterAI {

    /** 玩家进入追击状态的距离。 */
    private static final int ALERT_DISTANCE = 10;

    /** 玩家离开追击状态的距离。 */
    private static final int DISENGAGE_DISTANCE = 20;

    /** 怪物离开守护位置的最大距离。 */
    private static final int LEASH_DISTANCE = 20;

    /**
     * 根据玩家和守护位置的距离切换状态，并返回下一步。
     *
     * @param world 当前世界
     * @param monster 当前怪物
     * @param player 当前玩家
     * @return 怪物的下一步；没有可达位置时返回 null
     */
    @Override
    public Pathfinder.Position decideNextStep(
        TETile[][] world,
        Monster monster,
        Player player
    ) {
        // 玩家到守护点的距离决定怪物是否需要警戒。
        int playerDistance = Pathfinder.distance(
            world,
            monster.homeX(),
            monster.homeY(),
            player.x(),
            player.y()
        );

        // 怪物到守护点的距离用于限制追击范围。
        int homeDistance = Pathfinder.distance(
            world,
            monster.x(),
            monster.y(),
            monster.homeX(),
            monster.homeY()
        );

        // 守护点不可达时，怪物无法执行守护行为。
        if (homeDistance == -1) {
            return null;
        }

        // 记录更新前的状态，用于判断是否刚刚开始追击。
        boolean wasChasing = monster.state() == MonsterState.CHASING;
        updateState(monster, playerDistance, homeDistance);

        // 追击状态锁定玩家位置两回合，再更新目标。
        if (monster.state() == MonsterState.CHASING) {
            return CommittedChase.nextStep(
                world,
                monster,
                player.x(),
                player.y(),
                !wasChasing
            );
        }

        // 非追击状态返回守护点；已经到达时停止移动。
        if (homeDistance == 0) {
            return null;
        }
        return Pathfinder.nextStep(
            world,
            monster.x(),
            monster.y(),
            monster.homeX(),
            monster.homeY()
        );
    }

    /** 根据警戒范围和活动范围更新怪物状态。 */
    private static void updateState(
        Monster monster,
        int playerDistance,
        int homeDistance
    ) {
        if (monster.state() == MonsterState.CHASING) {
            // 玩家离开警戒区或怪物到达活动边界时结束追击。
            if (playerDistance == -1
                || playerDistance >= DISENGAGE_DISTANCE
                || homeDistance >= LEASH_DISTANCE) {
                monster.setState(MonsterState.IDLE);
                monster.clearCommittedTarget();
            }
            return;
        }

        // 玩家进入警戒区，并且怪物仍在活动范围内时开始追击。
        if (playerDistance != -1
            && playerDistance <= ALERT_DISTANCE
            && homeDistance < LEASH_DISTANCE) {
            monster.setState(MonsterState.CHASING);
        }
    }

}
