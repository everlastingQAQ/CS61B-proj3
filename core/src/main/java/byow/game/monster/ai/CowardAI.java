package byow.game.monster.ai;

import byow.game.monster.Monster;
import byow.game.monster.MonsterState;
import byow.game.pathfinder.Pathfinder;
import byow.game.player.Player;
import byow.game.tile.TETile;

/** 远距离追踪玩家、近距离逃跑的怪物 AI。 */
public class CowardAI implements MonsterAI {

    /** 进入逃跑状态的距离。 */
    private static final int FLEE_DISTANCE = 4;

    /** 恢复追击状态的距离。 */
    private static final int CHASE_DISTANCE = 7;

    /** 四个可移动方向。 */
    private static final int[][] DIRECTIONS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    /**
     * 根据与玩家的距离切换状态，并返回下一步。
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
        boolean wasChasing = monster.state() == MonsterState.CHASING;

        // 使用真实路径距离判断状态，避免墙壁干扰距离判断。
        int distance = Pathfinder.distance(
            world,
            monster.x(),
            monster.y(),
            player.x(),
            player.y()
        );

        // 玩家不可达时，怪物留在当前位置。
        if (distance == -1) {
            return null;
        }

        // 两个阈值之间保持原状态，避免反复切换。
        if (distance <= FLEE_DISTANCE) {
            monster.setState(MonsterState.FLEEING);
            if (wasChasing) {
                monster.clearCommittedTarget();
            }
        } else if (distance >= CHASE_DISTANCE) {
            monster.setState(MonsterState.CHASING);
        }

        // 根据当前状态选择追击或逃跑行为。
        if (monster.state() == MonsterState.FLEEING) {
            return flee(world, monster, player);
        }
        return CommittedChase.nextStep(
            world,
            monster,
            player.x(),
            player.y(),
            !wasChasing
        );
    }

    /** 选择与玩家路径距离最远的相邻位置。 */
    private static Pathfinder.Position flee(
        TETile[][] world,
        Monster monster,
        Player player
    ) {
        Pathfinder.Position best = null;
        int bestDistance = -1;

        // 比较四个相邻位置，选择实际路径上离玩家最远的位置。
        for (int[] direction : DIRECTIONS) {
            int nextX = monster.x() + direction[0];
            int nextY = monster.y() + direction[1];

            // 忽略墙壁和地图边界。
            if (!Pathfinder.isWalkable(world, nextX, nextY)) {
                continue;
            }

            // BFS 距离越大，该位置越适合作为逃跑方向。
            int distance = Pathfinder.distance(world, nextX, nextY, player.x(), player.y());
            if (distance > bestDistance) {
                best = new Pathfinder.Position(nextX, nextY);
                bestDistance = distance;
            }
        }

        return best;
    }
}
