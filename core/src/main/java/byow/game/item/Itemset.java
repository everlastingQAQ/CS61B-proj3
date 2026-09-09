package byow.game.item;

import byow.game.item.Item;
import byow.game.item.ItemType;
import byow.game.tile.TETile;

import java.awt.Color;

public class Itemset {
    public static final TETile FIRE = new TETile('&', Color.red, Color.black, "FIRE");

    public static TETile get(ItemType type) {
        return switch (type) {
            case FIRE -> FIRE;
        };
    }

    private Itemset() {
    }
}

