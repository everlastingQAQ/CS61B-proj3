package byow.Core.UI;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

public class WorldRender {

    public static final int WIDTH = 101;
    public static final int HEIGHT = 61;

    private final TERenderer ter = new TERenderer();

    public WorldRender() {
        ter.initialize(WIDTH, HEIGHT);
    }

    public void render(TETile[][] world) {
        ter.renderFrame(world);
    }
}
