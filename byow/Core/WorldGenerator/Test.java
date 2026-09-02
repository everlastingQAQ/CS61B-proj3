package byow.Core.WorldGenerator;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import static byow.Core.Engine.HEIGHT;
import static byow.Core.Engine.WIDTH;

public class Test {

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = WorldGenerator.generate(114514);
        ter.renderFrame(world);
    }
}
