package byow.Core.WorldGenerator;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import static byow.Core.Engine.HEIGHT;
import static byow.Core.Engine.WIDTH;

public class MazeGenerator {
    private TETile[][] world;
    private Random random;
    private int[][] regions;
    private int regionSize;

    // 初始化世界 和 随机种子 和 连通快标记 和 连通块数目
    public MazeGenerator(TETile[][] world, Random random, int[][] regions, int regionSize) {
        this.world = world;
        this.random = random;
        this.regions = regions;
        this.regionSize = regionSize;
    }

    // 创建一个Pair类
    private static class Position {
        int x, y;
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * 判断这个节点能不能挖开
     * - 只有是NOTHING可以
     * @return 返回判断
      */

    private boolean canCarve(int width, int height) {
        if (width < 1 || height < 1 || width >= WIDTH - 1 || height >= HEIGHT - 1) {
            return false;
        }
        return world[width][height].equals(Tileset.NOTHING);
    }

    /**
     * 判断有几个方向可以移动, 注意一次性判断两个节点
     * - 原点移动一步和原点移动两步都要可以挖才算这个方向可以移动
     * @param width 当前所在的横坐标
     * @param height 当前所在纵坐标
     * @return 返回数字数组表示现在可以走的方向, 上(0), 下(1), 左(2), 右(3)
     */

    private List<Integer> getValidDirections(int width, int height) {
        List<Integer> list = new ArrayList<>();
        if (canCarve(width - 2, height) && canCarve(width - 1, height)) {
            list.add(2);
        }
        if (canCarve(width + 2, height) && canCarve(width + 1, height)) {
            list.add(3);
        }
        if (canCarve(width, height - 2) && canCarve(width, height - 1)) {
            list.add(1);
        }
        if (canCarve(width, height + 2) && canCarve(width, height + 1)) {
            list.add(0);
        }
        return list;
    }

    /**
     * 从一个起始的可以挖的点开始往周围扩张通道
     * @param width 当前的横坐标
     * @param height 当前的纵坐标
     */

    private void growMaze(int width, int height) {
        // 栈记录走过的位置, 以便回溯
        Stack<Position> stack = new Stack<>();
        stack.add(new Position(width, height));
        world[width][height] = Tileset.FLOOR;
        regions[width][height] = regionSize;

        while (!stack.empty()) {
            Position nowPosition = stack.peek();

            int x = nowPosition.x;
            int y = nowPosition.y;
            // 找到随机生成的可以走的方向
            List<Integer> wayList = getValidDirections(x, y);

            // 如果找不到, pop掉现在的点, 回溯
            if (wayList.isEmpty()) {
                stack.pop();
                continue;
            }

            int randomWay = random.nextInt(wayList.size());
            int realWay = wayList.get(randomWay);

            // 建立迷宫, 然后将新节点加入栈
            if (realWay == 0) {
                world[x][y + 1] = Tileset.FLOOR;
                world[x][y + 2] = Tileset.FLOOR;
                regions[x][y + 1] = regionSize;
                regions[x][y + 2] = regionSize;
                stack.push(new Position(x, y + 2));
            } else if (realWay == 1) {
                world[x][y - 1] = Tileset.FLOOR;
                world[x][y - 2] = Tileset.FLOOR;
                regions[x][y - 1] = regionSize;
                regions[x][y - 2] = regionSize;
                stack.push(new Position(x, y - 2));
            } else if (realWay == 2) {
                world[x - 1][y] = Tileset.FLOOR;
                world[x - 2][y] = Tileset.FLOOR;
                regions[x - 1][y] = regionSize;
                regions[x - 2][y] = regionSize;
                stack.push(new Position(x - 2, y));
            } else {
                world[x + 1][y] = Tileset.FLOOR;
                world[x + 2][y] = Tileset.FLOOR;
                regions[x + 1][y] = regionSize;
                regions[x + 2][y] = regionSize;
                stack.push(new Position(x + 2, y));
            }
        }
    }

    /**
     * 生成迷宫的函数
     * 通过遍历每一个可以开始生成迷宫的地方, 找到迷宫起点, 生成迷宫, 每次遇到一个起点, regionSize++
     */

    public void generateMaze() {
        // 遍历地图, 如果找到可以挖的起点, 从这里开始dfs洪水填充迷宫
        for (int width = 1; width < WIDTH; width += 2) {
            for (int height = 1; height < HEIGHT; height += 2) {
                if (canCarve(width, height) && !getValidDirections(width, height).isEmpty()) {
                    regionSize++;
                    growMaze(width, height);
                }
            }
        }
    }
}
