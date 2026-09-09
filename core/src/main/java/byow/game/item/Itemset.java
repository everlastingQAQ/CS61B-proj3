package byow.game.item;

import byow.game.item.Item;
import byow.game.item.ItemType;
import byow.game.tile.TETile;

import java.awt.Color;

/**
 * 1. 用于规定物品的样式种类
 * 2. 用于知道物品类型的时候获取物品样式
 */
public class Itemset {

    // 规定物品样式
    public static final TETile CRYSTAL =
        new TETile('*', Color.cyan, Color.black, "CRYSTAL");

    public static final TETile COIN =
        new TETile('$', Color.yellow, Color.black, "COIN");

    public static final TETile KEY =
        new TETile('k', new Color(255, 165, 0), Color.black, "KEY");

    public static final TETile ORB =
        new TETile('o', Color.magenta, Color.black, "ORB");

    public static final TETile RUNE =
        new TETile('?', Color.white, Color.black, "RUNE");

    // 通过类型确定物品的样式
    public static TETile get(ItemType type) {
        return switch (type) {
            case CRYSTAL -> CRYSTAL;
            case COIN -> COIN;
            case KEY -> KEY;
            case ORB -> ORB;
            case RUNE -> RUNE;
        };
    }

    private Itemset() {
    }
}

