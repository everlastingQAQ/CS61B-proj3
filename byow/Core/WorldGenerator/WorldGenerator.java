package byow.Core.WorldGenerator;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static byow.Core.Engine.HEIGHT;
import static byow.Core.Engine.WIDTH;

public class WorldGenerator {

    public static TETile[][] generate(long seed) {

        Random random = new Random(seed);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        int[][] regions = new int[WIDTH][HEIGHT];
        List<Room> rooms = new ArrayList<>();

        initWorld(world);

        // 生成房间
        RoomGenerator roomGenerator = new RoomGenerator();
        roomGenerator.generate(world, regions, rooms, random);

        // 生成迷宫
        MazeGenerator mazeGenerator = new MazeGenerator(world, random, regions, rooms.size());
        mazeGenerator.generateMaze();

        return world;
    }

    private static void initWorld(TETile[][] world) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[i][j] = Tileset.WALL;
            }
        }
    }
}
