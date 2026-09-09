package byow.game.tile;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile FLOOR =
        new TETile('·', new Color(120, 170, 190), new Color(45, 55, 70), "floor");

    public static final TETile AVATAR =
        new TETile('@', new Color(120, 255, 255), new Color(45, 55, 70), "avatar");

    public static final TETile WALL =
        new TETile('█', new Color(150, 170, 185), new Color(75, 90, 110), "wall");

    public static final TETile NOTHING =
        new TETile(' ', new Color(0, 0, 0), new Color(8, 12, 20), "nothing");

    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile CLOSED_DOOR = new TETile('█', Color.orange, Color.black,
        "locked door");
    public static final TETile OPEN_DOOR = new TETile('▢', Color.orange, Color.black,
        "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");
    public static final TETile ROCK = new TETile('●', Color.gray, Color.black, "rock");
    public static final TETile BRIDGE = new TETile('=',new Color(139, 69, 19), Color.black,
        "bridge");

    private Tileset() {
    }
}

