package byow.game.item;

public class Item {
    private final int x;
    private final int y;

    private final ItemType itemType;

    public Item(int x, int y, ItemType itemType) {
        this.x = x;
        this.y = y;
        this.itemType = itemType;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public ItemType type() {
        return itemType;
    }

}
