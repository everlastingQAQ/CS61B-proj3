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


    // 只用于补全斜方向的墙角
    private static final int[][] DIAGONAL_DIRECTIONS = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

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

            // 当前格没有到达视野边界时，补全斜方向的墙角
            if (p.distance < visionRadius) {
                for (int[] direction : DIAGONAL_DIRECTIONS) {
                    int cornerX = p.x + direction[0];
                    int cornerY = p.y + direction[1];

                    // 检查墙角是否越界
                    if (cornerX < 0 || cornerX >= world.length || cornerY < 0 || cornerY >= world[0].length) {
                        continue;
                    }

                    // 如果斜方向是不透明格子，就将墙角设为可见
                    if (!TileRules.isTransparent(world[cornerX][cornerY])) {
                        visible[cornerX][cornerY] = true;
                    }
                }
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

        return visible;
    }
}
