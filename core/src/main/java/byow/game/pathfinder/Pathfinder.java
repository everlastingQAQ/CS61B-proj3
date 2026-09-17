package byow.game.pathfinder;

import byow.game.tile.TETile;
import byow.game.tile.TileRules;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class Pathfinder {

    // 存储类
    public record Position(int x, int y) {}

    // 可能前进的四个方向
    private static final int[][] DIRECTIONS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    /**
     * BFS查找下一步
     * 允许掉头
     *
     * @param world 传入世界
     * @param startX 传入初始横坐标
     * @param startY 传入初始纵坐标
     * @param targetX 传入目标横坐标
     * @param targetY 传入目标纵坐标
     * @return 返回Position记录下一步应该走的位置
     */
    public static Position nextStep(TETile[][] world, int startX, int startY, int targetX, int targetY) {
        // 普通寻路不限制第一步的方向。
        return search(world, startX, startY, targetX, targetY, null);
    }

    /**
     * BFS 查找下一步，并避免第一步返回上一个位置。
     * 无法避开时允许掉头，防止怪物困在死路。
     *
     * @param world 当前世界
     * @param startX 起点横坐标
     * @param startY 起点纵坐标
     * @param targetX 目标横坐标
     * @param targetY 目标纵坐标
     * @param previousX 上一个位置的横坐标
     * @param previousY 上一个位置的纵坐标
     * @return 下一步；没有可达路径时返回 null
     */
    public static Position nextStep(
        TETile[][] world,
        int startX,
        int startY,
        int targetX,
        int targetY,
        int previousX,
        int previousY
    ) {
        Position previous = new Position(previousX, previousY);

        // 第一次搜索禁止第一步回到上一位置。
        Position next = search(world, startX, startY, targetX, targetY, previous);
        if (next != null) {
            return next;
        }

        // 如果在禁止回头的情况下没有路可以走，则允许回头
        return search(world, startX, startY, targetX, targetY, null);
    }

    /** 执行 BFS，forbiddenFirstStep 只限制离开起点的第一步。 */
    private static Position search(
        TETile[][] world,
        int startX,
        int startY,
        int targetX,
        int targetY,
        Position forbiddenFirstStep
    ) {
        // 队列记录路径, vis记录走过, parents记录来路
        Queue<Position> queue = new ArrayDeque<>();
        boolean[][] vis = new boolean[world.length][world[0].length];
        Position[][] parents = new Position[world.length][world[0].length];

        // 初始化
        Position origin = new Position(startX, startY);
        queue.add(origin);
        vis[startX][startY] = true;

        // 如果初始位置就是目标直接返回
        if (startX == targetX && startY == targetY) {
            return new Position(startX, startY);
        }

        while (!queue.isEmpty()) {
            Position p = queue.poll();

            // 如果找到了目标地址, 直接返回回溯查找的第一步
            if (p.x == targetX && p.y == targetY) {
                return getNextStep(parents, p, origin);
            }

            // 向四个方向BFS
            for (int i = 0; i < 4; i++) {
                int nextX = p.x + DIRECTIONS[i][0];
                int nextY = p.y + DIRECTIONS[i][1];
                Position next = new Position(nextX, nextY);

                // 当在起点的时候不回头扩散方向
                if (p.equals(origin) && next.equals(forbiddenFirstStep)) {
                    continue;
                }

                if (isWalkable(world, nextX, nextY) && !vis[nextX][nextY]) {
                    parents[nextX][nextY] = p;
                    queue.add(next);
                    // 入队时 visited=true，而不是出队时
                    vis[nextX][nextY] = true;
                }
            }
        }

        return null;
    }

    /**
     * 回溯寻找第一步应该走哪里
     * @param parents 传入父节点数组
     * @param end 传入最后的位置
     * @param start 传入最初位置
     * @return 当父节点是最初位置的时候, 返回当前位置作为应该走的下一步
     */
    private static Position getNextStep(Position[][] parents, Position end, Position start) {
        while(!parents[end.x][end.y].equals(start)) {
            end = parents[end.x][end.y];
        }
        return end;
    }

    /**
     * 返回两点间的最短路径距离。
     *
     * @return 最短距离；没有可达路径时返回 -1
     */
    public static int distance(TETile[][] world, int startX, int startY, int targetX, int targetY) {
        // 距离图保存起点到所有可达位置的最短距离。
        int[][] distances = calculateDistances(world, startX, startY);
        return distances[targetX][targetY];
    }

    /**
     * 返回从起点出发路径距离最远的可达位置。
     *
     * @return 距离起点最远的位置
     */
    public static Position farthestPosition(TETile[][] world, int startX, int startY) {
        int[][] distances = calculateDistances(world, startX, startY);
        Position farthest = new Position(startX, startY);

        // 扫描距离图，保留当前距离最大的可达位置。
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (distances[x][y] > distances[farthest.x()][farthest.y()]) {
                    farthest = new Position(x, y);
                }
            }
        }

        return farthest;
    }

    /** 计算起点到所有可达位置的最短距离。 */
    private static int[][] calculateDistances(TETile[][] world, int startX, int startY) {
        Queue<Position> queue = new ArrayDeque<>();
        int[][] distances = new int[world.length][world[0].length];

        // -1 表示该位置尚未访问或无法到达。
        for (int[] row : distances) {
            Arrays.fill(row, -1);
        }

        // 从起点开始逐层扩散。
        queue.add(new Position(startX, startY));
        distances[startX][startY] = 0;

        while (!queue.isEmpty()) {
            Position current = queue.poll();

            // 将所有尚未访问的相邻可行走位置加入队列。
            for (int[] direction : DIRECTIONS) {
                int nextX = current.x() + direction[0];
                int nextY = current.y() + direction[1];
                if (isWalkable(world, nextX, nextY) && distances[nextX][nextY] == -1) {
                    distances[nextX][nextY] = distances[current.x()][current.y()] + 1;
                    queue.add(new Position(nextX, nextY));
                }
            }
        }

        return distances;
    }

    /** 判断目标位置是否可以行走。 */
    public static boolean isWalkable(TETile[][] world, int x, int y) {
        if (x < 1 || x >= world.length - 1 || y < 1 || y >= world[0].length - 1) {
            return false;
        }

        return TileRules.isWalkable(world[x][y]);
    }

    private Pathfinder() {}
}
