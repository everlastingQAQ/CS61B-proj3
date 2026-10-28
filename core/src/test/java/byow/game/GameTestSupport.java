package byow.game;

import byow.game.item.ItemType;
import byow.game.save.*;
import byow.game.scene.SceneObject;
import byow.game.scene.SceneObjectType;
import byow.game.tile.TileType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.function.Consumer;

/** 小型确定性关卡。全部存档写入测试调用者提供的临时目录。 */
public final class GameTestSupport {
    private static final Gson GSON = new Gson();

    public static GameSave level() {
        TileType[][] world = new TileType[11][7];
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                world[x][y] = x == 0 || y == 0 || x == 10 || y == 6
                    ? TileType.WALL : TileType.FLOOR;
            }
        }
        world[9][3] = TileType.CLOSED_DOOR;
        return new GameSave(new WorldData(world), new PlayerData(2, 3, 0, 0, 20, 20, 2),
            List.of(new ItemData(3, 3, ItemType.CRYSTAL), new ItemData(7, 1, ItemType.COIN),
                new ItemData(7, 2, ItemType.KEY), new ItemData(7, 4, ItemType.ORB),
                new ItemData(7, 5, ItemType.RUNE)),
            List.of(), new boolean[11][7], 7, 42, 1, 0, false, List.of(),
            new ExitData(9, 3, false),
            List.of(SceneObjectData.from(new SceneObject(4, 3, SceneObjectType.BLESSING_CHEST))));
    }

    public static GameSave change(GameSave save, Consumer<JsonObject> mutation) {
        JsonObject json = GSON.toJsonTree(save).getAsJsonObject();
        mutation.accept(json);
        return GSON.fromJson(json, GameSave.class);
    }

    public static void keys(Engine engine, String input) {
        for (char key : input.toCharArray()) {
            engine.handleKey(key);
        }
    }

    private GameTestSupport() {}
}
