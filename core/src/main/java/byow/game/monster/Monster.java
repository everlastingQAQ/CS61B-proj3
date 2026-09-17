package byow.game.monster;

import byow.game.player.Player;
import byow.game.random.GameRandom;
import byow.game.tile.TETile;
import byow.game.tile.TileRules;

/** 保存单只怪物的位置、类型、行为状态和跨回合数据。 */
public class Monster {
    private final long id;
    private int x;
    private int y;

    private int homeX;
    private int homeY;

    private boolean patrolRouteInitialized;
    private int patrolAX;
    private int patrolAY;
    private int patrolBX;
    private int patrolBY;
    private int patrolTargetIndex;

    private int committedTargetX;
    private int committedTargetY;
    private int commitmentTurns;

    private final MonsterType type;
    private MonsterState state;

    /** 在随机可行走位置创建怪物。 */
    public Monster(
        long id,
        TETile[][] world,
        GameRandom random,
        Player player,
        MonsterType type
    ) {
        int originX = random.nextInt(1, world.length - 1);
        int originY = random.nextInt(1, world[0].length - 1);

        while (!isOriginPosition(world, player, originX, originY)) {
            originX = random.nextInt(1, world.length - 1);
            originY = random.nextInt(1, world[0].length - 1);
        }

        this.id = id;
        this.x = originX;
        this.y = originY;
        this.homeX = originX;
        this.homeY = originY;
        this.type = type;
        this.state = type.initialState();
    }

    /** 在指定位置创建具有初始行为状态的怪物。 */
    public Monster(long id, int x, int y, MonsterType type) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.homeX = x;
        this.homeY = y;
        this.type = type;
        this.state = type.initialState();
    }

    /** 从存档恢复怪物的全部行为状态。 */
    public Monster(
        long id,
        int x,
        int y,
        MonsterType type,
        MonsterState state,
        int homeX,
        int homeY,
        boolean patrolRouteInitialized,
        int patrolAX,
        int patrolAY,
        int patrolBX,
        int patrolBY,
        int patrolTargetIndex,
        int committedTargetX,
        int committedTargetY,
        int commitmentTurns
    ) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.type = type;
        this.state = state;
        this.homeX = homeX;
        this.homeY = homeY;
        this.patrolRouteInitialized = patrolRouteInitialized;
        this.patrolAX = patrolAX;
        this.patrolAY = patrolAY;
        this.patrolBX = patrolBX;
        this.patrolBY = patrolBY;
        this.patrolTargetIndex = patrolTargetIndex;
        this.committedTargetX = committedTargetX;
        this.committedTargetY = committedTargetY;
        this.commitmentTurns = commitmentTurns;
    }

    private static boolean isWalkable(TETile[][] world, int x, int y) {
        if (x < 1 || x >= world.length - 1 || y < 1 || y >= world[0].length - 1) {
            return false;
        }
        return TileRules.isWalkable(world[x][y]);
    }

    private static boolean isOriginPosition(
        TETile[][] world,
        Player player,
        int x,
        int y
    ) {
        if (!isWalkable(world, x, y)) {
            return false;
        }
        if (player.x() == x && player.y() == y) {
            return false;
        }
        return Math.abs(x - player.x()) + Math.abs(y - player.y()) >= 10;
    }

    public long id() {
        return id;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int homeX() {
        return homeX;
    }

    public int homeY() {
        return homeY;
    }

    public void setHome(int x, int y) {
        this.homeX = x;
        this.homeY = y;
    }

    public boolean hasPatrolRoute() {
        return patrolRouteInitialized;
    }

    public int patrolAX() {
        return patrolAX;
    }

    public int patrolAY() {
        return patrolAY;
    }

    public int patrolBX() {
        return patrolBX;
    }

    public int patrolBY() {
        return patrolBY;
    }

    public int patrolTargetIndex() {
        return patrolTargetIndex;
    }

    public void setPatrolRoute(int ax, int ay, int bx, int by) {
        this.patrolAX = ax;
        this.patrolAY = ay;
        this.patrolBX = bx;
        this.patrolBY = by;
        this.patrolTargetIndex = 0;
        this.patrolRouteInitialized = true;
    }

    public int patrolTargetX() {
        return patrolTargetIndex == 0 ? patrolAX : patrolBX;
    }

    public int patrolTargetY() {
        return patrolTargetIndex == 0 ? patrolAY : patrolBY;
    }

    public void advancePatrolTarget() {
        patrolTargetIndex = 1 - patrolTargetIndex;
    }

    public boolean hasCommittedTarget() {
        return commitmentTurns > 0;
    }

    public void commitTarget(int x, int y, int turns) {
        this.committedTargetX = x;
        this.committedTargetY = y;
        this.commitmentTurns = turns;
    }

    public void clearCommittedTarget() {
        commitmentTurns = 0;
    }

    public int committedTargetX() {
        return committedTargetX;
    }

    public int committedTargetY() {
        return committedTargetY;
    }

    public int commitmentTurns() {
        return commitmentTurns;
    }

    public void consumeCommitmentTurn() {
        if (commitmentTurns > 0) {
            commitmentTurns--;
        }
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int distanceTo(int targetX, int targetY) {
        return Math.abs(x - targetX) + Math.abs(y - targetY);
    }

    public MonsterType type() {
        return type;
    }

    public MonsterState state() {
        return state;
    }

    public void setState(MonsterState state) {
        this.state = state;
    }
}
