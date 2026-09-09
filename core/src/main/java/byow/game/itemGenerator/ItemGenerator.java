package byow.game.itemGenerator;

import byow.game.item.Item;
import byow.game.item.ItemType;
import byow.game.player.Player;
import byow.game.random.GameRandom;
import byow.game.tile.TETile;
import byow.game.tile.Tileset;

import java.util.ArrayList;
import java.util.List;

import static byow.game.GameConfig.*;

public class ItemGenerator {

    /**
     * 随机生成生成ITEM_NUMBER个物品
     *
     * @param world 传入游戏世界
     * @param player 传入用户位置
     * @param gameRandom 传入随机数
     * @return 返回物品数组
     */
    public static List<Item> generate(TETile[][] world, Player player, GameRandom gameRandom) {
        List<Item> items = new ArrayList<>();

        while (items.size() < ITEM_NUMBER) {
            int x = gameRandom.nextInt(1, WORLD_WIDTH);
            int y = gameRandom.nextInt(1, WORLD_HEIGHT);

            // 如果是用户位置, 不允许放置
            if (player.x() == x && player.y() == y) {
                continue;
            }

            // 如果世界板块不是FLOOR, 不允许放置
            if (!world[x][y].equals(Tileset.FLOOR)) {
                continue;
            }

            // 取得枚举数组
            ItemType[] types = ItemType.values();

            // 确定随机类型
            int index = gameRandom.nextInt(types.length);

            // 建立新Item
            Item newItem = new Item(x, y, types[index]);

            // 加入数组
            items.add(newItem);
        }

        return items;
    }
}
