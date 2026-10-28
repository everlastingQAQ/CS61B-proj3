package byow.game.save;

import byow.game.GameTestSupport;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SaveManagerTest {
    @TempDir Path directory;

    @Test
    void realFileRoundTripIncludesFormatVersionAndAllSlotsAreIndependent() throws IOException {
        SaveManager manager = new SaveManager(directory);
        for (int slot = 1; slot <= 5; slot++) {
            long randomState = slot * 100L;
            GameSave save = GameTestSupport.change(GameTestSupport.level(),
                json -> json.addProperty("randomState", randomState));
            manager.save(slot, save);
            JsonObject disk = JsonParser.parseString(Files.readString(path(slot))).getAsJsonObject();
            assertEquals(SaveManager.FORMAT_VERSION, disk.get("formatVersion").getAsInt());
            assertEquals(randomState, manager.load(slot).randomState());
        }
        assertThrows(IllegalArgumentException.class, () -> manager.load(0));
        assertThrows(IllegalArgumentException.class, () -> manager.save(6, GameTestSupport.level()));
    }

    @Test
    void missingSlotAndUnreadableStorageReturnPlayerFacingErrors() throws IOException {
        SaveManager manager = new SaveManager(directory);
        assertFalse(manager.exists(1));
        assertThrows(SaveException.class, () -> manager.load(1));
        Path blocked = directory.resolve("file-instead-of-directory");
        Files.writeString(blocked, "occupied");
        assertThrows(SaveException.class,
            () -> new SaveManager(blocked).save(1, GameTestSupport.level()));
    }

    @Test
    void failedReplacementKeepsOriginalSaveAndRemovesTemporaryFile() throws IOException {
        SaveManager manager = new SaveManager(directory);
        manager.save(1, GameTestSupport.level());
        String original = Files.readString(path(1));
        SaveManager failing = new SaveManager(directory) {
            @Override
            void replaceFile(Path temporary, Path target) throws IOException {
                throw new IOException("simulated filesystem failure");
            }
        };
        GameSave changed = GameTestSupport.change(GameTestSupport.level(),
            json -> json.addProperty("randomState", 1234));
        assertThrows(SaveException.class, () -> failing.save(1, changed));
        assertEquals(original, Files.readString(path(1)));
        try (var files = Files.list(path(1).getParent())) {
            assertEquals(1, files.count());
        }
        assertEquals(42, manager.load(1).randomState());
    }

    @Test
    void invalidSnapshotCannotOverwriteExistingSave() throws IOException {
        SaveManager manager = new SaveManager(directory);
        manager.save(1, GameTestSupport.level());
        String original = Files.readString(path(1));
        assertThrows(SaveException.class, () -> manager.save(1, null));
        assertEquals(original, Files.readString(path(1)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"malformed", "null", "array", "futureVersion", "fractionalVersion",
        "hugeVersion", "missingWorld", "raggedWorld", "unknownTile", "badPlayer", "badHealth",
        "missingItems", "duplicateItem", "badExplored", "unknownMonster", "unpairedPortal"})
    void corruptOrUnsupportedSavesAreRejectedBeforeUse(String problem) throws IOException {
        SaveManager manager = new SaveManager(directory);
        manager.save(1, GameTestSupport.level());
        JsonObject json = JsonParser.parseString(Files.readString(path(1))).getAsJsonObject();
        switch (problem) {
            case "futureVersion" -> json.addProperty("formatVersion", 999);
            case "fractionalVersion" -> json.addProperty("formatVersion", 1.5);
            case "hugeVersion" -> json.addProperty("formatVersion", new java.math.BigInteger("18446744073709551616"));
            case "missingWorld" -> json.remove("worldData");
            case "raggedWorld" -> json.getAsJsonObject("worldData").getAsJsonArray("world").remove(0);
            case "unknownTile" -> json.getAsJsonObject("worldData").getAsJsonArray("world")
                .get(1).getAsJsonArray().set(1, new com.google.gson.JsonPrimitive("UNKNOWN_TILE"));
            case "badPlayer" -> json.getAsJsonObject("playerData").addProperty("x", -1);
            case "badHealth" -> json.getAsJsonObject("playerData").addProperty("currentHp", 100);
            case "missingItems" -> json.remove("items");
            case "duplicateItem" -> json.getAsJsonArray("items").set(1, json.getAsJsonArray("items").get(0).deepCopy());
            case "badExplored" -> json.getAsJsonArray("explored").remove(0);
            case "unknownMonster" -> json.add("monsters", JsonParser.parseString(
                "[{\"id\":1,\"x\":1,\"y\":1,\"homeX\":1,\"homeY\":1,\"type\":\"UNKNOWN\",\"state\":\"IDLE\"}]"));
            case "unpairedPortal" -> json.add("sceneObjects", JsonParser.parseString(
                "[{\"x\":4,\"y\":3,\"type\":\"PORTAL\",\"active\":true,\"targetX\":5,\"targetY\":3}]"));
        }
        String content = switch (problem) {
            case "malformed" -> "{broken";
            case "null" -> "null";
            case "array" -> "[]";
            default -> json.toString();
        };
        Files.writeString(path(1), content);
        assertThrows(SaveException.class, () -> manager.load(1), problem);
    }

    @Test
    void unversionedLegacyFileLoadsWithoutBeingRewritten() throws IOException {
        SaveManager manager = new SaveManager(directory);
        manager.save(1, GameTestSupport.level());
        JsonObject json = JsonParser.parseString(Files.readString(path(1))).getAsJsonObject();
        json.remove("formatVersion");
        json.remove("monsters");
        json.remove("sceneObjects");
        json.remove("seenMonsterTypes");
        Files.writeString(path(1), json.toString());
        String original = Files.readString(path(1));
        assertEquals(2, manager.load(1).playerData().x());
        assertEquals(original, Files.readString(path(1)));
    }

    private Path path(int slot) {
        return directory.resolve("slot" + slot).resolve("save.json");
    }
}
