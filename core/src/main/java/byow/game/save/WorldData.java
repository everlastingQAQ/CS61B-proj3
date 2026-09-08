package byow.game.save;

import byow.game.render.WorldRenderer;
import byow.game.tile.TETile;

/**
 * 保存 World 的相关数据
 *
 * @author everlasting
 * */
public class WorldData {
    private final TETile[][] world;

    public WorldData(TETile[][] world) {
        this.world = world;
    }

    public TETile[][] world() {
        return world;
    }
}
