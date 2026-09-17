package byow.game.item;

/** 保存一个遗物的位置和固定类型。 */
public class Item {

    /** 遗物所在位置的横坐标。 */
    private final int x;

    /** 遗物所在位置的纵坐标。 */
    private final int y;

    /** 决定遗物外观和固定 Buff 的类型。 */
    private final ItemType itemType;

    /**
     * 在指定位置创建一种遗物。
     *
     * @param x 遗物横坐标
     * @param y 遗物纵坐标
     * @param itemType 遗物类型
     */
    public Item(int x, int y, ItemType itemType) {
        this.x = x;
        this.y = y;
        this.itemType = itemType;
    }

    /** 返回遗物横坐标。 */
    public int x() {
        return x;
    }

    /** 返回遗物纵坐标。 */
    public int y() {
        return y;
    }

    /** 返回遗物类型。 */
    public ItemType type() {
        return itemType;
    }
}
