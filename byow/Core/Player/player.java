package byow.Core.Player;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

import static byow.Core.Engine.HEIGHT;
import static byow.Core.Engine.WIDTH;

/**
 * 玩家属性
 * 1. 横坐标
 * 2. 纵坐标
 * 玩家行为
 * 1. W U S D移动
 * 2. 取得当前横纵坐标
 */

public class player {
    private int x;
    private int y;

    // 初始化用户, 随机化玩家初始位置 , 横坐标随机[1, WIDTH], 纵坐标随机[1, HEIGHT]
    public player(TETile[][] world, Random random) {
        int maxWidth = WIDTH - 1, minWidth = 1;
        int maxHeight = HEIGHT - 1, minHeight = 1;
        int originX = random.nextInt(maxWidth - minWidth + 1) + minWidth;
        int originY = random.nextInt(maxHeight - minHeight + 1) + maxHeight;

        while (!isPlaceWalkable(world, originX, originY)) {
            originX = random.nextInt(maxWidth - minWidth + 1) + minWidth;
            originY = random.nextInt(maxHeight - minHeight + 1) + maxHeight;
        }

        x = originX;
        y = originY;
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
     * @param world 当前世界
     * @param width 判断的位置横坐标
     * @param height 判断的位置纵坐标
     * @return 位置是否可走
     * 是否可走判断:
     * 1. 越界不可以走(最外层算墙壁,不合理)
     * 2. UNLOCKED_DOOR 可走
     * 3. FLOOR 可走
     */

    private boolean isPlaceWalkable(TETile[][] world, int width, int height) {
        if (width < 1 || width > WIDTH - 1 || height < 1 || height > HEIGHT - 1) {
            return false;
        }

        return world[width][height].equals(Tileset.UNLOCKED_DOOR)
            || world[width][height].equals(Tileset.FLOOR);
    }

    // 移动玩家
    public void moveFront(TETile[][] world) {
        if (!isPlaceWalkable(world, x, y + 1)) {
            return;
        }
        y++;
    }

    public void moveBack(TETile[][] world) {
        if (!isPlaceWalkable(world, x, y - 1)) {
            return;
        }
        y--;
    }

    public void moveLeft(TETile[][] world) {
        if (!isPlaceWalkable(world, x - 1, y)) {
            return;
        }
        x--;
    }

    public void moveRight(TETile[][] world) {
        if (!isPlaceWalkable(world, x + 1, y)) {
            return;
        }
        x++;
    }
}
