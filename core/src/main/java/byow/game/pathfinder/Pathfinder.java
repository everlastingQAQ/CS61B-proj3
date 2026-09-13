package byow.game.pathfinder;

import byow.game.tile.TETile;
import byow.game.tile.TileRules;

import java.util.ArrayDeque;
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
     * @param world 传入世界
     * @param startX 传入初始横坐标
     * @param startY 传入初始纵坐标
     * @param targetX 传入目标横坐标
     * @param targetY 传入目标纵坐标
     * @return 返回Position记录下一步应该走的位置
     */
    public static Position nextStep(TETile[][] world, int startX, int startY, int targetX, int targetY) {
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

                if (isWalkable(world, nextX, nextY) && !vis[nextX][nextY]) {
                    parents[nextX][nextY] = p;
                    queue.add(new Position(nextX, nextY));
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

    // 判断是否可以走
    private static boolean isWalkable(TETile[][] world, int x, int y) {
        if (x < 1 || x >= world.length - 1 || y < 1 || y >= world[0].length - 1) {
            return false;
        }

        return TileRules.isWalkable(world[x][y]);
    }

    private Pathfinder() {}
}
