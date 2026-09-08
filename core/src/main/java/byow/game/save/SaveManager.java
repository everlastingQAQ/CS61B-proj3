package byow.game.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SaveManager {

    private final Gson gson;

    private static final int MIN_SLOT = 1;
    private static final int MAX_SLOT = 5;

    public SaveManager() {
        gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    }

    /**
     * 将存档保存在指定的存档位中
     *
     * @param slot 存档编号
     * @param gameSave 游戏存档
     * */
    public void save(int slot, GameSave gameSave) {

        validateSlot(slot);

        try {
            // 创建 save.json 路径
            Path path = getSavePath(slot);

            // 创建 save 文件夹
            Files.createDirectories(path.getParent());

            // 将 gameSave 对象转化为 json
            String json = gson.toJson(gameSave);

            // 写入 json 文件
            Files.writeString(path, json);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 加载指定的存档
     *
     * @param slot 存档编号
     * */
    public GameSave load(int slot) {

        validateSlot(slot);

        if (!exists(slot)) {
            throw new IllegalArgumentException("Save slot " + slot + " does not exist.");
        }

        try {
            // 创建 save.json 路径
            Path path = getSavePath(slot);

            // 读取 json 文件
            String json = Files.readString(path);

            // gson 将 json 转换为 GameSave class
            return gson.fromJson(json, GameSave.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 查看是否存在这个存档
     *
     * @param slot 存档编号
     * */
    public boolean exists(int slot) {
        Path path = getSavePath(slot);
        validateSlot(slot);
        return Files.exists(path);
    }

    /** 检查存档编号是否合法 */
    public void validateSlot(int slot) {
        if (slot < MIN_SLOT || slot > MAX_SLOT) {
            throw new IllegalArgumentException("Invalid save slot: " + slot);
        }
    }

    /** 获取存档路径 */
    private Path getSavePath(int slot) {

        return Path.of(
            System.getProperty("user.dir"),
            "save",
            "slot" + slot,
            "save.json"
        );
    }

}
