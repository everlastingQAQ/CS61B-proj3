package byow.game.monster;

import byow.game.tile.TETile;

import java.awt.Color;

/** 集中定义五种怪物在地图上的字符和颜色。 */
public final class MonsterAppearance {
    private static final Color BACKGROUND = new Color(45, 55, 70);

    public static final TETile HUNTER = new TETile(
        'H', new Color(255, 80, 80), BACKGROUND, "hunter"
    );

    public static final TETile AMBUSHER = new TETile(
        'A', new Color(210, 100, 255), BACKGROUND, "ambusher"
    );

    public static final TETile GUARDIAN = new TETile(
        'G', new Color(255, 190, 70), BACKGROUND, "guardian"
    );

    public static final TETile PATROLLER = new TETile(
        'P', new Color(80, 180, 255), BACKGROUND, "patroller"
    );

    public static final TETile COWARD = new TETile(
        'C', new Color(120, 230, 120), BACKGROUND, "coward"
    );

    /**
     * 返回指定类型怪物的地图外观。
     *
     * @param type 怪物类型
     * @return 怪物对应的字符 tile
     */
    public static TETile get(MonsterType type) {
        return switch (type) {
            case HUNTER -> HUNTER;
            case AMBUSHER -> AMBUSHER;
            case GUARDIAN -> GUARDIAN;
            case PATROLLER -> PATROLLER;
            case COWARD -> COWARD;
        };
    }

    private MonsterAppearance() {}
}
