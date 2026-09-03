package byow.Core.WorldGenerator;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

import static byow.Core.Engine.HEIGHT;
import static byow.Core.Engine.WIDTH;
import static java.util.Collections.shuffle;

/**
 * 用于连接地图的各个部分，使地图成为一个完整的连通块
 *
 * 通过 {@link #connect(TETile[][], int[][], Random)} 方法连接地图。
 * */
public class RegionConnector {

    private static final int EXTRA_CONNECTOR_CHANCE = 50;

    private static final int[][] DIRS = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
    };

    private final List<Connector> connectorPositions = new ArrayList<>();

    /**
     * 将整张地图的房间和迷宫串联起来
     *
     * @param world 将要串联起来的地图
     * @param regions 每个格子的连通块编号
     * @param random 随机数生成器
     * */
    public void connect(TETile[][] world, int[][] regions, Random random) {
        // 统计可能会变成门的格子
        updatePos(world, regions);

        int regionNums = getRegionSize();
        shuffle(connectorPositions, random);

        DSU dsu = new DSU(regionNums + 1);

        // 遍历可能会变成门的格子，通过并查集来判断是否应该变成门
        for (Connector connector : connectorPositions) {
            TreeSet<Integer> set = connector.regions;
            int first = set.first();
            boolean connected = false;
            for (int x : set) {
                if (dsu.merge(first, x)) {
                    connected = true;
                }
            }
            if (connected || random.nextInt(EXTRA_CONNECTOR_CHANCE) == 0) {
                world[connector.x][connector.y] = Tileset.UNLOCKED_DOOR;
            }
        }
    }

    /**
     * 统计可能会被变成门的格子，并更新{@link connectorPositions}
     *
     * @param world 将要串联起来的地图
     * @param regions 每个格子的连通块编号
     * */
    private void updatePos(TETile[][] world, int[][] regions) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (!world[i][j].equals(Tileset.WALL)) {
                    continue;
                }
                TreeSet<Integer> regionCounts = new TreeSet<>();

                for (int[] dir : DIRS) {
                    int x = i + dir[0];
                    int y = j + dir[1];
                    if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
                        continue;
                    }
                    int region = regions[x][y];
                    if (region != 0) {
                        regionCounts.add(region);
                    }
                }

                if (regionCounts.size() >= 2) {
                    this.connectorPositions.add(new Connector(i, j, regionCounts));
                }
            }
        }
    }

    private static class Connector {
        private final int x;
        private final int y;
        private final TreeSet<Integer> regions;

        Connector(int x, int y, TreeSet<Integer> regions) {
            this.x = x;
            this.y = y;
            this.regions = regions;
        }

        public int x() {
            return this.x;
        }

        public int y() {
            return this.y;
        }

        public Set<Integer> regions() {
            return this.regions;
        }
    }

    private static class DSU {
        private final int[] parent;
        private final int[] size;

        DSU(int n) {
            parent = new int[n];
            size = new int[n];

            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        boolean merge(int a, int b) {
            a = find(a);
            b = find(b);
            if (a == b) {
                return false;
            }
            if (size[a] < size[b]) {
                int temp = a;
                a = b;
                b = temp;
            }

            parent[b] = a;
            size[a] += size[b];

            return true;
        }
    }
}
