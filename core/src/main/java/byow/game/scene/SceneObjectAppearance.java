package byow.game.scene;

import byow.game.tile.TETile;

import java.awt.Color;

/** 功能性场景物件的字符外观。 */
public final class SceneObjectAppearance {
    private static final Color BACKGROUND = new Color(45, 55, 70);
    private static final TETile SPIKE_TRAP = new TETile(
        '^', new Color(255, 105, 80), BACKGROUND, "尖刺陷阱"
    );
    private static final TETile HEALING_FOUNTAIN = new TETile(
        '+', new Color(90, 235, 220), BACKGROUND, "治愈泉"
    );
    private static final TETile PORTAL = new TETile(
        'O', new Color(205, 110, 255), BACKGROUND, "传送门"
    );
    private static final TETile BLESSING_CHEST = new TETile(
        'B', new Color(255, 205, 75), BACKGROUND, "祝福宝箱"
    );

    public static TETile get(SceneObjectType type) {
        return switch (type) {
            case SPIKE_TRAP -> SPIKE_TRAP;
            case HEALING_FOUNTAIN -> HEALING_FOUNTAIN;
            case PORTAL -> PORTAL;
            case BLESSING_CHEST -> BLESSING_CHEST;
        };
    }

    private SceneObjectAppearance() {}
}
