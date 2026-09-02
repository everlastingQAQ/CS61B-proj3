package byow.Core.WorldGenerator;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

import static byow.Core.Engine.HEIGHT;
import static byow.Core.Engine.WIDTH;
import static java.util.Collections.shuffle;

public class RegionConnector {

    private static final int[][] DIRS = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
    };

    private List<Connecter> connectorPositions = new ArrayList<>();

    public void connect(TETile[][] world, int[][] regions, int roomSize, Random random) {
        updatePos(world, regions);

        int regionNums = roomSize;
        shuffle(connectorPositions, random);

        DSU dsu = new DSU(regionNums + 1);
        for (Connecter connecter : connectorPositions) {
            int first = connecter.regions.first();
        }
    }

    private void updatePos(TETile[][] world, int[][] regions) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (!world[i][j].equals(Tileset.WALL)) {
                    continue;
                }
                TreeSet<Integer> regionCounts = new TreeSet<Integer>();

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
                    this.connectorPositions.add(new Connecter(i, j, regionCounts));
                }
            }
        }
    }

    private static class Connecter {
        private int x;
        private int y;
        private TreeSet<Integer> regions;

        Connecter(int x, int y, TreeSet<Integer> regions) {
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
        private int[] parent;
        private int[] size;

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

        boolean union(int a, int b) {
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
