package byow.game.player;

import byow.game.random.GameRandom;
import byow.game.tile.TETile;
import byow.game.tile.TileRules;
import byow.game.tile.Tileset;

import java.util.Random;

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

    // 记录用户上一个行走的板块的样式
    private TETile lastPositionType;

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
        while (!isPlaceWalkable(world, originX, originY)) {
            originX = random.nextInt(1, world.length - 1);
            originY = random.nextInt(1, world[0].length - 1);
        }

        // 初始化世界
        init(world, originX, originY);
    }

    /**
     * 加载玩家
     *
     * @param x 玩家的横坐标
     * @param y 玩家的纵坐标
     * @param world 玩家的世界
     */
    public Player(TETile[][] world, int x, int y) {
        if (!isPlaceWalkable(world, x, y)) {
            throw new IllegalArgumentException(
                "Player position is not walkable"
            );
        }

        init(world, x, y);
    }

    /**
     * 初始化玩家状态
     * 1. 设定玩家初始位置
     * 2. 改变游戏界面,并记录此板块样式
     */
    private void init(TETile[][] world, int x, int y) {
        // 设定玩家初始位置
        this.x = x;
        this.y = y;

        // 改变游戏界面,并记录此板块样式
        lastPositionType = world[x][y];
        world[x][y] = Tileset.AVATAR;
    }

    // 取得玩家现在横坐标位置
    public int x() {
        return x;
    }

    // 取得玩家现在纵坐标位置
    public int y() {
        return y;
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
    private boolean isPlaceWalkable(TETile[][] world, int x, int y) {
        if (x < 1 || x >= world.length - 1 || y < 1 || y >= world[0].length - 1) {
            return false;
        }

        return TileRules.isWalkable(world[x][y]);
    }

    /**
     * 改变人物移动后的世界
     * 1. 还原用户当前站的位置的板块
     * 2. 更新 lastPositionType
     * 3. 改变用户下一步站的位置的板块
     * @param world 游戏世界
     * @param width 当前横坐标
     * @param height 当前纵坐标
     * @param nextWidth 下一步的横坐标
     * @param nextHeight 下一步的纵坐标
     */
    private void moveTo(TETile[][] world, int width, int height, int nextWidth, int nextHeight) {
        // 还原人物原来站的板块
        world[width][height] = lastPositionType;

        // 更新lastPositionType
        lastPositionType = world[nextWidth][nextHeight];

        // 更新用户现在站的板块
        world[nextWidth][nextHeight] = Tileset.AVATAR;
    }

    /**
     * 移动玩家:
     * 1. 判断能否移动
     * 2. 改变世界
     * 3. 更新坐标
     * @param world 游戏世界
     */
    public void moveUp(TETile[][] world) {
        if (!isPlaceWalkable(world, x, y + 1)) {
            return;
        }
        moveTo(world, x, y, x, y + 1);
        y++;
    }

    public void moveDown(TETile[][] world) {
        if (!isPlaceWalkable(world, x, y - 1)) {
            return;
        }
        moveTo(world, x, y, x, y - 1);
        y--;
    }

    public void moveLeft(TETile[][] world) {
        if (!isPlaceWalkable(world, x - 1, y)) {
            return;
        }
        moveTo(world, x, y, x - 1, y);
        x--;
    }

    public void moveRight(TETile[][] world) {
        if (!isPlaceWalkable(world, x + 1, y)) {
            return;
        }
        moveTo(world, x, y, x + 1, y);
        x++;
    }
}
