package byow.game.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/** JSON 存档边界：版本检查、数据校验、原子替换。 */
public class SaveManager {
    public static final int FORMAT_VERSION = 1;
    private static final long MAX_SAVE_BYTES = 16 * 1024 * 1024;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path directory;

    public SaveManager() {
        this(Path.of(System.getProperty("byow.saveDir",
            Path.of(System.getProperty("user.dir"), "save").toString())));
    }

    /** 可注入独立存档目录，测试不会写入玩家的真实存档。 */
    public SaveManager(Path directory) {
        this.directory = Objects.requireNonNull(directory).toAbsolutePath().normalize();
    }

    public void save(int slot, GameSave gameSave) {
        validateSlot(slot);
        SaveValidator.validate(gameSave);
        JsonObject document = gson.toJsonTree(gameSave).getAsJsonObject();
        document.addProperty("formatVersion", FORMAT_VERSION);
        byte[] bytes = gson.toJson(document).getBytes(StandardCharsets.UTF_8);
        if (bytes.length > MAX_SAVE_BYTES) {
            throw new SaveException("存档过大，无法保存。");
        }
        Path path = getSavePath(slot);
        Path temporary = null;
        try {
            Files.createDirectories(path.getParent());
            temporary = Files.createTempFile(path.getParent(), "save-", ".tmp");
            try (FileChannel channel = FileChannel.open(temporary, StandardOpenOption.WRITE)) {
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                channel.force(true);
            }
            replaceFile(temporary, path);
        } catch (IOException | SecurityException exception) {
            throw new SaveException("保存失败，原有存档已保留。", exception);
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException | SecurityException ignored) {
                    // 清理失败不能掩盖实际保存结果；临时文件不会被当作存档读取。
                }
            }
        }
    }

    /** 同目录原子替换；文件系统不支持时保留旧文件并报告失败。 */
    void replaceFile(Path temporary, Path target) throws IOException {
        Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    public GameSave load(int slot) {
        validateSlot(slot);
        Path path = getSavePath(slot);
        try {
            if (!Files.exists(path)) {
                throw new SaveException("该位置没有存档。");
            }
            if (Files.size(path) > MAX_SAVE_BYTES) {
                throw new SaveException("存档过大，无法读取。");
            }
            JsonElement element = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));
            if (!element.isJsonObject()) {
                throw new SaveException("存档数据无效，无法读取。");
            }
            JsonObject document = element.getAsJsonObject();
            if (document.has("formatVersion")) {
                JsonElement version = document.get("formatVersion");
                if (!version.isJsonPrimitive() || !version.getAsJsonPrimitive().isNumber()
                    || !version.getAsString().matches("[0-9]+")) {
                    throw new SaveException("存档版本无效。");
                }
                java.math.BigInteger number = new java.math.BigInteger(version.getAsString());
                if (number.compareTo(java.math.BigInteger.valueOf(FORMAT_VERSION)) > 0) {
                    throw new SaveException("存档来自更新版本，请更新游戏。");
                }
            }
            GameSave save = gson.fromJson(document, GameSave.class);
            SaveValidator.validate(save);
            return save;
        } catch (JsonParseException | IllegalStateException | NumberFormatException exception) {
            throw new SaveException("存档已损坏，无法读取。", exception);
        } catch (IOException | SecurityException exception) {
            throw new SaveException("读取失败，请检查存档文件。", exception);
        }
    }

    public boolean exists(int slot) {
        validateSlot(slot);
        try {
            return Files.exists(getSavePath(slot));
        } catch (SecurityException exception) {
            throw new SaveException("无法访问存档目录。", exception);
        }
    }

    public void validateSlot(int slot) {
        if (slot < 1 || slot > 5) {
            throw new IllegalArgumentException("Invalid save slot: " + slot);
        }
    }

    private Path getSavePath(int slot) {
        return directory.resolve("slot" + slot).resolve("save.json");
    }
}
