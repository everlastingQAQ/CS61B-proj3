package byow.game.visioncauculate;

import byow.game.player.Player;
import byow.game.tile.TETile;
import byow.game.tile.TileRules;

import java.util.LinkedList;
import java.util.Queue;

public class Visioncauculate {

    private static class Distance {
        int x;
        int y;
        int distance;

        public Distance(int x, int y, int distance) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }
    }

    private static int[][] moveTo = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};


    /**
     * 计算那哪些范围可视
     * @param world 传入世界
     * @param player 传入玩家
     * @param visionRadius 传入可视范围
     * @return 返回boolean数组标记那些范围可视
     */
    public static boolean[][] calculate(TETile[][] world, Player player, int visionRadius) {
        boolean[][] visible = new boolean[world.length][world[0].length];

        boolean[][] visited = new boolean[world.length][world[0].length];
        Queue<Distance> queue = new LinkedList<Distance>();

        queue.add(new Distance(player.x(), player.y(), 0));
        visited[player.x()][player.y()] = true;
        visible[player.x()][player.y()] = true;

        /*
        1. 将边界范围内，满足距离的加入队列，并可见
        2. 队列pop掉的，不可穿透的，不进行四个方向扩散
         */
        while (!queue.isEmpty()) {
            Distance p =  queue.poll();

            if (!TileRules.isTransparent(world[p.x][p.y])) {
                continue;
            }

            for (int i = 0; i < 4; i++) {
                int nextX = p.x + moveTo[i][0];
                int nextY = p.y + moveTo[i][1];
                int nextDistance = p.distance + 1;

                if (nextX < 0 || nextX >= world.length || nextY < 0 || nextY >= world[0].length) {
                    continue;
                }

                if (nextDistance <= visionRadius && !visited[nextX][nextY]) {
                    queue.add(new Distance(nextX, nextY, nextDistance));
                    visited[nextX][nextY] = true;
                    visible[nextX][nextY] = true;
                }
            }
        }

        revealBoundaryWalls(world, visible);

        return visible;
    }

    /**
     * 补全可见地面周围八方向的墙体。独立于 BFS 半径边界执行，避免
     * 房间拐角因为刚好处于视野边缘而被雾层遮掉。
     */
    private static void revealBoundaryWalls(TETile[][] world, boolean[][] visible) {
        boolean[][] boundaryWalls = new boolean[world.length][world[0].length];

        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (!visible[x][y] || !TileRules.isTransparent(world[x][y])) {
                    continue;
                }

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int wallX = x + dx;
                        int wallY = y + dy;
                        if (wallX < 0 || wallX >= world.length
                            || wallY < 0 || wallY >= world[0].length) {
                            continue;
                        }
                        if (!TileRules.isTransparent(world[wallX][wallY])) {
                            boundaryWalls[wallX][wallY] = true;
                        }
                    }
                }
            }
        }

        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                visible[x][y] |= boundaryWalls[x][y];
            }
        }
    }
}
