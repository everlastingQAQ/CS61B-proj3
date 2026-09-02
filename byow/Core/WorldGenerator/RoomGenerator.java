package byow.Core.WorldGenerator;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.List;
import java.util.Random;

import static byow.Core.RandomUtils.uniform;

/**
 * 用于在地图中随机生成并放置互不重叠的矩形房间。
 *
 * 通过 {@link #generate(TETile[][], int[][], List<Room>, Random)} 方法生成房间。
 * */
public class RoomGenerator {

    private static final int MAX_TIMES = 200;
    private int width;
    private int height;
    private boolean[][] covered;

    /**
     * 在给定地图中随机生成矩形房间。
     *
     * @param world 要生成房间的地图
     * @param random 随机种子
     * @return 生成房间后的地图
     */
    public TETile[][] generate(TETile[][] world, int[][] regions, List<Room> rooms, Random random) {
        // 初始化地图的宽、高和覆盖情况
        this.width = world.length;
        this.height = world[0].length;
        this.covered = new boolean[width][height];

        for (int times = 0; times < MAX_TIMES; times++) {
            int roomWidth = randomOdd(random, 5, 15);
            int roomHeight = randomOdd(random, 5, 11);
            int recX = randomOdd(random, 1, width - roomWidth - 1);
            int recY = randomOdd(random, 1, height - roomHeight - 1);

            // 检查房间位置是否合法
            if (!isLegal(recX, recY, roomWidth, roomHeight)) {
                continue;
            }

            // 覆盖房间并且标记
            for (int i = recX; i < recX + roomWidth; i++) {
                for (int j = recY; j < recY + roomHeight; j++) {
                    world[i][j] = Tileset.FLOOR;
                    this.covered[i][j] = true;
                }
            }
        }

        return world;
    }

    /**
     * 检查房间生成位置是否合法
     * 即房间不能超出地图边界，且不能与已有房间重叠或紧贴
     *
     * @param x 房间左下角的横坐标
     * @param y 房间左下角的纵坐标
     * @param width 宽度
     * @param height 高度
     *
     * @return 生成位置是否合法
     * */
    private boolean isLegal(int x, int y, int width, int height) {
        // 检查房间位置是否超出地图大小，且地图四周不放置房间
        if (x + width >= this.width || y + height >= this.height
                || x < 1 || y < 1) {
            return false;
        }

        for (int i = x - 1; i <= x + width; i++) {
            for (int j = y - 1; j <= y + height; j++) {
                if (this.covered[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 在指定范围内随机生成一个奇数。
     *
     * @param random 随机数生成器
     * @param min 最小值，要求为奇数
     * @param max 最大值
     * @return [min, max] 范围内的随机奇数
     */
    private static int randomOdd(Random random, int min, int max) {
        int count = (max - min) / 2 + 1;
        return min + 2 * uniform(random, count);
    }
}