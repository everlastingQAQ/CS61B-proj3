package byow.game;

public class GameConfig {
    public static final int WORLD_WIDTH = 101;
    public static final int WORLD_HEIGHT = 61;

    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;

    public static final int HUD_HEIGHT = 80;

    public static final int CAMERA_VISIBLE_TILES_Y = 40;

    public static float getTileSize() {
//        return Math.min (
//            WINDOW_WIDTH / WORLD_WIDTH,
//            WINDOW_HEIGHT / WORLD_HEIGHT
//        );
        return 16f;
    }

    private GameConfig() {

    }
}
