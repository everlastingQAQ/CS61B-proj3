package byow.game.pathfinder;

import byow.game.tile.TETile;
import byow.game.tile.TileRules;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/** 提供只依赖地形的 BFS 寻路工具。 */
public final class Pathfinder {
    public record Position(int x, int y) {}

    private static final int[][] DIRECTIONS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    /** 返回从起点前往目标的最短路径下一步。 */
    public static Position nextStep(
        TETile[][] world,
        int startX,
        int startY,
        int targetX,
        int targetY
    ) {
        Queue<Position> queue = new ArrayDeque<>();
        boolean[][] visited = new boolean[world.length][world[0].length];
        Position[][] parents = new Position[world.length][world[0].length];

        Position origin = new Position(startX, startY);
        queue.add(origin);
        visited[startX][startY] = true;

        if (startX == targetX && startY == targetY) {
            return origin;
        }

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            if (current.x() == targetX && current.y() == targetY) {
                return getNextStep(parents, current, origin);
            }

            for (int[] direction : DIRECTIONS) {
                int nextX = current.x() + direction[0];
                int nextY = current.y() + direction[1];
                if (isWalkable(world, nextX, nextY) && !visited[nextX][nextY]) {
                    Position next = new Position(nextX, nextY);
                    parents[nextX][nextY] = current;
                    queue.add(next);
                    visited[nextX][nextY] = true;
                }
            }
        }

        return null;
    }

    private static Position getNextStep(
        Position[][] parents,
        Position end,
        Position start
    ) {
        while (!parents[end.x()][end.y()].equals(start)) {
            end = parents[end.x()][end.y()];
        }
        return end;
    }

    /** 返回两点间的最短路径距离；无法到达时返回 -1。 */
    public static int distance(
        TETile[][] world,
        int startX,
        int startY,
        int targetX,
        int targetY
    ) {
        int[][] distances = calculateDistances(world, startX, startY);
        return distances[targetX][targetY];
    }

    /** 返回从起点出发路径距离最远的可达位置。 */
    public static Position farthestPosition(TETile[][] world, int startX, int startY) {
        int[][] distances = calculateDistances(world, startX, startY);
        Position farthest = new Position(startX, startY);

        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (distances[x][y] > distances[farthest.x()][farthest.y()]) {
                    farthest = new Position(x, y);
                }
            }
        }
        return farthest;
    }

    private static int[][] calculateDistances(TETile[][] world, int startX, int startY) {
        Queue<Position> queue = new ArrayDeque<>();
        int[][] distances = new int[world.length][world[0].length];
        for (int[] row : distances) {
            Arrays.fill(row, -1);
        }

        queue.add(new Position(startX, startY));
        distances[startX][startY] = 0;

        while (!queue.isEmpty()) {
            Position current = queue.poll();
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

    public static boolean isWalkable(TETile[][] world, int x, int y) {
        if (x < 1 || x >= world.length - 1 || y < 1 || y >= world[0].length - 1) {
            return false;
        }
        return TileRules.isWalkable(world[x][y]);
    }

    private Pathfinder() {}
}
