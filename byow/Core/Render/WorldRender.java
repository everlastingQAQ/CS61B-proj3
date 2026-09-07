package byow.Core.Render;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

/**
 * 负责游戏世界的渲染
 *
 * @author everlasting
 * */
public class WorldRender {

    /** 游戏世界的宽度和高度 */
    public static final int WIDTH = 51;
    public static final int HEIGHT = 31;

    /** 底层地图渲染器。 */
    private final TERenderer ter = new TERenderer();

    /** 初始化游戏世界的渲染 */
    public void initialize() {
        ter.initialize(WIDTH, HEIGHT);
    }

    /**
     * 渲染当前游戏世界。
     *
     * @param world 要显示的游戏世界
     */
    public void render(TETile[][] world) {
        ter.renderFrame(world);
    }
}
