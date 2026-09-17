package byow.game.monster.ai;

import byow.game.monster.Monster;
import byow.game.pathfinder.Pathfinder;
import byow.game.player.Player;
import byow.game.tile.TETile;

/** 在两个远距离端点之间往返移动的怪物 AI。 */
public class PatrollerAI implements MonsterAI {

    /**
     * 初始化或切换巡逻目标，并返回前往目标的下一步。
     *
     * @param world 当前世界
     * @param monster 当前怪物
     * @param player 当前玩家，本行为不直接追踪玩家
     * @return 怪物的下一步；没有巡逻路径时返回 null
     */
    @Override
    public Pathfinder.Position decideNextStep(
        TETile[][] world,
        Monster monster,
        Player player
    ) {
        // 第一次行动时，根据当前位置生成两个相距较远的端点。
        if (!monster.hasPatrolRoute()) {
            initializeRoute(world, monster);
        }

        int targetX = monster.patrolTargetX();
        int targetY = monster.patrolTargetY();

        // 到达当前端点后，立即把另一个端点设为新目标。
        if (monster.x() == targetX && monster.y() == targetY) {
            monster.advancePatrolTarget();
            targetX = monster.patrolTargetX();
            targetY = monster.patrolTargetY();
        }

        // 两个端点相同时没有可执行的巡逻移动。
        if (monster.x() == targetX && monster.y() == targetY) {
            return null;
        }

        // 沿最短路径前往当前端点，并避免立即掉头。
        return Pathfinder.nextStep(
            world,
            monster.x(),
            monster.y(),
            targetX,
            targetY,
            monster.previousX(),
            monster.previousY()
        );
    }

    /** 从怪物所在区域计算两个远距离巡逻端点。 */
    private static void initializeRoute(TETile[][] world, Monster monster) {
        // 第一次搜索找到离出生位置较远的端点 A。
        Pathfinder.Position first = Pathfinder.farthestPosition(
            world,
            monster.x(),
            monster.y()
        );

        // 从端点 A 再搜索一次，得到与它相距较远的端点 B。
        Pathfinder.Position second = Pathfinder.farthestPosition(
            world,
            first.x(),
            first.y()
        );

        monster.setPatrolRoute(
            first.x(),
            first.y(),
            second.x(),
            second.y()
        );
    }
}
