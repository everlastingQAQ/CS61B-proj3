package byow.Core.WorldGenerator;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;

import static byow.Core.Engine.WIDTH;
import static byow.Core.Engine.HEIGHT;

public class DeadEndRemover {
    private TETile[][] world;
    private List<Room> rooms;
    private int[][] roomTarget;

    DeadEndRemover(TETile[][] world, List<Room> rooms) {
        this.world = world;
        this.rooms = rooms;
    }

    // 通过通道附近是否有三面墙判断是不是叶子节点
    private boolean isDeadEnd(int width, int height) {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        if (!world[width][height].equals(Tileset.FLOOR)) {
            return false;
        }

        int wallNum = 0;
        for (int i = 0; i < 4; i ++) {
            int x = width + dx[i];
            int y = height + dy[i];
            if (x < 0 || y < 0 || x > WIDTH - 1 || y > HEIGHT - 1) {
                continue;
            }
            if (world[x][y].equals(Tileset.WALL)) {
                wallNum++;
            }
        }

        return (wallNum == 3);
    }

    // 移走所有死胡同
    public void removeDeadEnd() {

    }
}
