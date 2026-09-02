package byow.Core.WorldGenerator;

import byow.TileEngine.TETile;

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

        RoomGenerator roomGenerator = new RoomGenerator();

        roomGenerator.generate(world, regions, rooms, random);

        return world;
    }

}
