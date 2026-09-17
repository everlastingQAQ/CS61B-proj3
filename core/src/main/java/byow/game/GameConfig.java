package byow.game;

public class GameConfig {
    public static final int WORLD_WIDTH = 101;
    public static final int WORLD_HEIGHT = 61;

    // 程序启动时默认窗口大小
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;

    public static final int HUD_HEIGHT = 80;

    public static final int CAMERA_VISIBLE_TILES_Y = 21;

    public static final int ITEM_NUMBER = 5;

    /** 激活出口所需的遗物数量。 */
    public static final int REQUIRED_ITEM_COUNT = 3;

    /** 玩家没有 Vision Buff 时的基础视野半径。 */
    public static final int DEFAULT_VISION_RADIUS = 7;

    /** 玩家初始和默认最大生命值。 */
    public static final int PLAYER_MAX_HP = 3;

    /** 一只怪物接触玩家时造成的伤害。 */
    public static final int MONSTER_CONTACT_DAMAGE = 1;

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
