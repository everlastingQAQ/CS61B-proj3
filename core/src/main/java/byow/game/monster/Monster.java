package byow.game.monster;

import byow.game.player.Player;
import byow.game.random.GameRandom;
import byow.game.tile.TETile;
import byow.game.tile.TileRules;

public class Monster {
    private int x;
    private int y;

    /** 怪物移动前的横坐标。 */
    private int previousX;

    /** 怪物移动前的纵坐标。 */
    private int previousY;

    /** 怪物守护位置的横坐标。 */
    private int homeX;

    /** 怪物守护位置的纵坐标。 */
    private int homeY;

    /** 巡逻路线是否已经初始化。 */
    private boolean patrolRouteInitialized;

    /** 第一个巡逻端点。 */
    private int patrolAX;
    private int patrolAY;

    /** 第二个巡逻端点。 */
    private int patrolBX;
    private int patrolBY;

    /** 当前巡逻目标，0 表示 A，1 表示 B。 */
    private int patrolTargetIndex;

    /** 当前承诺追踪目标的横坐标。 */
    private int committedTargetX;

    /** 当前承诺追踪目标的纵坐标。 */
    private int committedTargetY;

    /** 继续追踪当前目标的剩余回合数。 */
    private int commitmentTurns;

    private final MonsterType type;
    private MonsterState state;

    public Monster(TETile[][] world, GameRandom random, Player player, MonsterType type) {
        int originX = random.nextInt(1, world.length - 1);
        int originY = random.nextInt(1, world[0].length - 1);

        // 判断当前位置是否玩家可通行,如果不可以再次随机一个
        while (!isOriginPositon(world, player, originX, originY)) {
            originX = random.nextInt(1, world.length - 1);
            originY = random.nextInt(1, world[0].length - 1);
        }

        this.x = originX;
        this.y = originY;
        this.previousX = originX;
        this.previousY = originY;
        this.homeX = originX;
        this.homeY = originY;
        this.type = type;
        this.state = type.initialState();
    }

    public Monster(int x, int y, MonsterType type) {
        this.x = x;
        this.y = y;
        this.previousX = x;
        this.previousY = y;
        this.homeX = x;
        this.homeY = y;
        this.type = type;
        this.state = type.initialState();
    }

    private boolean isWalkable(TETile[][] world, int width, int height) {
        if (width < 1 || width >= world.length - 1 || height < 1 || height >= world[0].length - 1) {
            return false;
        }
        return TileRules.isWalkable(world[width][height]);
    }

    private boolean isOriginPositon(TETile[][] world, Player player, int width, int height) {
        // 不能生成在不能行走的道路
        if (!isWalkable(world, width, height)) {
            return false;
        }

        // 不能生成在player的位置
        if (player.x() == width && player.y() == height) {
            return false;
        }

        // 不能初始位置离玩家太近
        int distance = Math.abs(width - player.x()) + Math.abs(height - player.y());
        if (distance < 10) {
            return false;
        }

        return true;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    /** 返回怪物移动前的横坐标。 */
    public int previousX() {
        return previousX;
    }

    /** 返回怪物移动前的纵坐标。 */
    public int previousY() {
        return previousY;
    }

    /** 返回怪物守护位置的横坐标。 */
    public int homeX() {
        return homeX;
    }

    /** 返回怪物守护位置的纵坐标。 */
    public int homeY() {
        return homeY;
    }

    /** 设置怪物需要守护的位置。 */
    public void setHome(int x, int y) {
        this.homeX = x;
        this.homeY = y;
    }

    /** 返回巡逻路线是否已经初始化。 */
    public boolean hasPatrolRoute() {
        return patrolRouteInitialized;
    }

    /** 设置两个巡逻端点，并从第一个端点开始巡逻。 */
    public void setPatrolRoute(int ax, int ay, int bx, int by) {
        this.patrolAX = ax;
        this.patrolAY = ay;
        this.patrolBX = bx;
        this.patrolBY = by;
        this.patrolTargetIndex = 0;
        this.patrolRouteInitialized = true;
    }

    /** 返回当前巡逻目标的横坐标。 */
    public int patrolTargetX() {
        return patrolTargetIndex == 0 ? patrolAX : patrolBX;
    }

    /** 返回当前巡逻目标的纵坐标。 */
    public int patrolTargetY() {
        return patrolTargetIndex == 0 ? patrolAY : patrolBY;
    }

    /** 将巡逻目标切换到另一个端点。 */
    public void advancePatrolTarget() {
        patrolTargetIndex = 1 - patrolTargetIndex;
    }

    /** 返回怪物是否仍在追踪已承诺的目标。 */
    public boolean hasCommittedTarget() {
        return commitmentTurns > 0;
    }

    /** 设置追踪目标和持续回合数。 */
    public void commitTarget(int x, int y, int turns) {
        this.committedTargetX = x;
        this.committedTargetY = y;
        this.commitmentTurns = turns;
    }

    /** 返回当前承诺目标的横坐标。 */
    public int committedTargetX() {
        return committedTargetX;
    }

    /** 返回当前承诺目标的纵坐标。 */
    public int committedTargetY() {
        return committedTargetY;
    }

    /** 消耗一个目标承诺回合。 */
    public void consumeCommitmentTurn() {
        if (commitmentTurns > 0) {
            commitmentTurns--;
        }
    }

    /** 移动怪物，并保存移动前的位置。 */
    public void moveTo(int x, int y) {
        // 保存旧位置，供下一回合判断是否立即掉头。
        this.previousX = this.x;
        this.previousY = this.y;

        // 更新怪物当前位置。
        this.x = x;
        this.y = y;
    }

    // 得到距离目标的距离
    public int distanceTo(int targetX, int targetY) {
        return Math.abs(x - targetX) + Math.abs(y - targetY);
    }

    public MonsterType type() {
        return type;
    }

    public MonsterState state() {
        return state;
    }

    // 设置怪物状态
    public void setState(MonsterState state) {
        this.state = state;
    }

}
