package byow.game.monster.ai;

import byow.game.monster.Monster;
import byow.game.pathfinder.Pathfinder;
import byow.game.player.Player;
import byow.game.tile.TETile;

/** 追踪玩家前方位置的怪物 AI。 */
public class AmbusherAI implements MonsterAI {

    /** 向玩家移动方向预测的最大距离。 */
    private static final int PREDICTION_DISTANCE = 3;

    /**
     * 计算玩家前方的可行走目标，并返回怪物的下一步。
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
        // 没有移动方向时，预测目标默认是玩家当前位置。
        int targetX = player.x();
        int targetY = player.y();

        // 沿玩家最近一次成功移动的方向逐格预测。
        for (int i = 0; i < PREDICTION_DISTANCE; i++) {
            int nextX = targetX + player.lastMoveDx();
            int nextY = targetY + player.lastMoveDy();

            // 遇到墙壁或地图边界时，使用最后一个可行走位置。
            if (!Pathfinder.isWalkable(world, nextX, nextY)) {
                break;
            }
            targetX = nextX;
            targetY = nextY;
        }

        // 预测机制保持不变，但预测位置会被锁定两回合。
        return CommittedChase.nextStep(
            world,
            monster,
            targetX,
            targetY,
            false
        );
    }

}
