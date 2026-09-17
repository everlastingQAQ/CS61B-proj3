package byow.game.player;

import byow.game.random.GameRandom;
import byow.game.tile.TETile;
import byow.game.tile.TileRules;

import static byow.game.GameConfig.PLAYER_MAX_HP;

/**
 * 玩家属性
 * 1. 当前位置横坐标
 * 2. 当前位置纵坐标
 * 3. 上次站的板块的样式
 * 玩家行为
 * 1. W A S D移动, 并改变世界(包括改变当前站的位置的板块和还原上一步的板块)
 * 2. 获取当前横纵坐标的函数
 * @author icovo everlasting
 */

public class Player {
    private int x;
    private int y;

    /** 上次成功移动的横向距离。 */
    private int lastMoveDx;

    /** 上次成功移动的纵向距离。 */
    private int lastMoveDy;

    /** 玩家当前生命值。 */
    private int currentHp;

    /** 玩家最大生命值。 */
    private int maxHp;

    /** 可以抵挡怪物伤害的剩余次数。 */
    private int shieldCharges;

    /**
     * 初始化玩家
     * 1. 随机化玩家出生位置
     * 2. 更新游戏界面
     * @param world 传入游戏界面
     * @param random 传入种子生成的随机数
     */
    public Player(TETile[][] world, GameRandom random) {
        // 随机在[min,max]范围生成当前玩家的位置,横坐标[1, WIDTH],纵坐标[1,HEIGHT]
        int originX = random.nextInt(1, world.length - 1);
        int originY = random.nextInt(1, world[0].length - 1);

        // 判断当前位置是否玩家可通行,如果不可以再次随机一个
        while (!isWalkable(world, originX, originY)) {
            originX = random.nextInt(1, world.length - 1);
            originY = random.nextInt(1, world[0].length - 1);
        }

        this.x = originX;
        this.y = originY;
        this.currentHp = PLAYER_MAX_HP;
        this.maxHp = PLAYER_MAX_HP;
    }

    /**
     * 加载玩家
     * @param x 玩家的横坐标
     * @param y 玩家的纵坐标
     * @param world 玩家的世界
     */
    public Player(TETile[][] world, int x, int y) {
        this(world, x, y, 0, 0);
    }

    /** 从存档恢复玩家位置和最近一次成功移动方向。 */
    public Player(TETile[][] world, int x, int y, int lastMoveDx, int lastMoveDy) {
        this(world, x, y, lastMoveDx, lastMoveDy, PLAYER_MAX_HP, PLAYER_MAX_HP);
    }

    /** 从存档恢复玩家位置、移动方向和生命值。 */
    public Player(
        TETile[][] world,
        int x,
        int y,
        int lastMoveDx,
        int lastMoveDy,
        int currentHp,
        int maxHp
    ) {
        this(world, x, y, lastMoveDx, lastMoveDy, currentHp, maxHp, 0);
    }

    /** 从存档恢复玩家位置、移动方向、生命值和护盾。 */
    public Player(
        TETile[][] world,
        int x,
        int y,
        int lastMoveDx,
        int lastMoveDy,
        int currentHp,
        int maxHp,
        int shieldCharges
    ) {
        if (!isWalkable(world, x, y)) {
            throw new IllegalArgumentException(
                "Player position is not walkable"
            );
        }
        if (maxHp <= 0 || currentHp < 0 || currentHp > maxHp) {
            throw new IllegalArgumentException("Invalid player health");
        }
        if (shieldCharges < 0) {
            throw new IllegalArgumentException("Invalid shield charges");
        }
        this.x = x;
        this.y = y;
        this.lastMoveDx = lastMoveDx;
        this.lastMoveDy = lastMoveDy;
        this.currentHp = currentHp;
        this.maxHp = maxHp;
        this.shieldCharges = shieldCharges;
    }

    // 取得玩家现在横坐标位置
    public int x() {
        return x;
    }

    // 取得玩家现在纵坐标位置
    public int y() {
        return y;
    }

    /** 返回上次成功移动的横向距离。 */
    public int lastMoveDx() {
        return lastMoveDx;
    }

    /** 返回上次成功移动的纵向距离。 */
    public int lastMoveDy() {
        return lastMoveDy;
    }

    /** 返回玩家当前生命值。 */
    public int currentHp() {
        return currentHp;
    }

    /** 返回玩家最大生命值。 */
    public int maxHp() {
        return maxHp;
    }

    /** 返回玩家剩余的护盾次数。 */
    public int shieldCharges() {
        return shieldCharges;
    }

    /**
     * 恢复玩家生命值，但不会超过最大生命值。
     *
     * @param amount 恢复的生命值
     */
    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Heal amount cannot be negative");
        }
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    /**
     * 增加可以抵挡怪物伤害的次数。
     *
     * @param charges 增加的护盾次数
     */
    public void grantShield(int charges) {
        if (charges < 0) {
            throw new IllegalArgumentException("Shield charges cannot be negative");
        }
        shieldCharges += charges;
    }

    /**
     * 尝试消耗一层护盾。
     *
     * @return 成功抵挡伤害时返回 true；没有护盾时返回 false
     */
    public boolean consumeShield() {
        if (shieldCharges == 0) {
            return false;
        }
        shieldCharges--;
        return true;
    }

    /**
     * 扣除玩家生命值，并保证生命值不会低于 0。
     *
     * @param amount 受到的伤害
     */
    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        currentHp = Math.max(0, currentHp - amount);
    }

    /** 返回玩家是否仍然存活。 */
    public boolean isAlive() {
        return currentHp > 0;
    }

    /**
     * 判断可以走的位置
     * @param world 传入当前世界
     * @param x 判断的位置横坐标
     * @param y 判断的位置纵坐标
     * @return 位置是否可走
     * 是否可走判断:
     * 1. 越界不可以走(最外层算墙壁,不合理)
     * 2. UNLOCKED_DOOR 可走
     * 3. FLOOR 可走
     */
    private boolean isWalkable(TETile[][] world, int x, int y) {
        if (x < 1 || x >= world.length - 1 || y < 1 || y >= world[0].length - 1) {
            return false;
        }

        return TileRules.isWalkable(world[x][y]);
    }

    /**
     * 向指定方向移动，并记录成功移动的方向。
     *
     * @param world 当前世界
     * @param dx 横向移动距离
     * @param dy 纵向移动距离
     * @return 是否移动成功
     */
    private boolean move(TETile[][] world, int dx, int dy) {
        // 移动失败时保留原位置和上一次成功移动方向。
        if (!isWalkable(world, x + dx, y + dy)) {
            return false;
        }

        // 更新玩家位置。
        x += dx;
        y += dy;

        // 记录方向，供 Ambusher 预测玩家前方位置。
        lastMoveDx = dx;
        lastMoveDy = dy;
        return true;
    }

    public boolean moveUp(TETile[][] world) {
        return move(world, 0, 1);
    }

    public boolean moveDown(TETile[][] world) {
        return move(world, 0, -1);
    }

    public boolean moveLeft(TETile[][] world) {
        return move(world, -1, 0);
    }

    public boolean moveRight(TETile[][] world) {
        return move(world, 1, 0);
    }
}
