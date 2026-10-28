package byow.game;

import byow.game.save.*;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static byow.game.GameTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class EngineIntegrationTest {
    @TempDir Path directory;

    @Test
    void seedErrorsAreRecoverableAndBackspaceEditsInput() {
        Engine engine = new Engine(new SaveManager(directory));
        keys(engine, "ns");
        assertEquals(GameState.SEED, engine.state());
        assertFalse(engine.uiMessage().isEmpty());
        keys(engine, "9223372036854775808s");
        assertEquals(GameState.SEED, engine.state());
        assertFalse(engine.uiMessage().isEmpty());
        keys(engine, "\b7s");
        assertEquals(GameState.PLAYING, engine.state());
    }

    @Test
    void overwriteRequiresConfirmationAndCancellationPreservesOldFile() throws IOException {
        SaveManager manager = new SaveManager(directory);
        manager.save(1, level());
        String original = Files.readString(path(directory, 1));
        Engine engine = new Engine(manager);
        keys(engine, "l1dps1");
        assertEquals(GameState.SAVE, engine.state());
        assertEquals(1, engine.pendingOverwriteSlot());
        keys(engine, "n");
        assertEquals(0, engine.pendingOverwriteSlot());
        assertEquals(original, Files.readString(path(directory, 1)));
        keys(engine, "1y");
        assertEquals(GameState.PAUSE, engine.state());
        assertEquals(3, manager.load(1).playerData().x());
    }

    @Test
    void invalidInputDoesNotDirtySavedProgressButMovingDoes() {
        SaveManager manager = new SaveManager(directory);
        Engine engine = new Engine(manager);
        keys(engine, "n42sps1pxpq");
        assertEquals(GameState.MENU, engine.state());
        keys(engine, "l1");
        var player = engine.player();
        char movement = '\0';
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        char[] keys = {'d', 'a', 'w', 's'};
        for (int i = 0; i < directions.length; i++) {
            int x = player.x() + directions[i][0], y = player.y() + directions[i][1];
            if (byow.game.pathfinder.Pathfinder.isWalkable(engine.world(), x, y)
                && engine.sceneObjects().stream().noneMatch(object -> object.contains(x, y))) {
                movement = keys[i];
                break;
            }
        }
        assertNotEquals('\0', movement);
        engine.handleKey(movement);
        keys(engine, "pq");
        assertEquals(GameState.CONFIRM_QUIT, engine.state());
    }

    @Test
    void missingAndCorruptLoadsLeavePreviousGameUntouched() throws IOException {
        Engine engine = new Engine(new SaveManager(directory));
        keys(engine, "n42spqnl2");
        var world = engine.world();
        var player = engine.player();
        assertEquals(GameState.LOAD, engine.state());
        assertEquals("该位置没有存档。", engine.uiMessage());
        Files.createDirectories(path(directory, 2).getParent());
        Files.writeString(path(directory, 2), "{broken");
        keys(engine, "2");
        assertEquals(GameState.LOAD, engine.state());
        assertSame(world, engine.world());
        assertSame(player, engine.player());
        assertFalse(engine.uiMessage().isEmpty());
    }

    @Test
    void failedLegacyExitReconstructionDoesNotPartiallyReplaceGame() {
        SaveManager manager = new SaveManager(directory);
        GameSave legacy = change(level(), json -> {
            json.remove("exit");
            json.getAsJsonObject("worldData").getAsJsonArray("world").get(9).getAsJsonArray()
                .set(3, new com.google.gson.JsonPrimitive("FLOOR"));
        });
        manager.save(2, legacy);
        Engine engine = new Engine(manager);
        keys(engine, "n42spqn");
        var world = engine.world();
        keys(engine, "l2");
        assertEquals(GameState.LOAD, engine.state());
        assertSame(world, engine.world());
    }

    @Test
    void failedSaveKeepsPlayerInSaveMenuWithUnsavedProgress() {
        SaveManager manager = new SaveManager(directory) {
            @Override public void save(int slot, GameSave data) {
                throw new SaveException("保存失败，原有存档已保留。");
            }
        };
        Engine engine = new Engine(manager);
        keys(engine, "n42sps1");
        assertEquals(GameState.SAVE, engine.state());
        assertFalse(engine.uiMessage().isEmpty());
        keys(engine, "bq");
        assertEquals(GameState.CONFIRM_QUIT, engine.state());
    }

    @Test
    void diskSaveAndResumeProducesSameGameplayAndRandomRewardsAsContinuousPlay() throws IOException {
        Path uninterrupted = directory.resolve("continuous");
        Path resumed = directory.resolve("resumed");
        SaveManager firstManager = new SaveManager(uninterrupted);
        SaveManager secondManager = new SaveManager(resumed);
        firstManager.save(1, level());
        secondManager.save(1, level());
        Engine first = new Engine(firstManager);
        Engine second = new Engine(secondManager);
        keys(first, "l1d");
        keys(second, "l1dps2");
        second = new Engine(secondManager);
        keys(second, "l2");
        String continuation = "ddwwaassddwwaass";
        keys(first, continuation);
        keys(second, continuation);
        assertEquals(GameState.PLAYING, first.state());
        assertEquals(first.state(), second.state());
        assertFalse(first.monsters().isEmpty());
        assertTrue(first.sceneObjects().stream().noneMatch(object -> object.active()));
        keys(first, "ps3");
        keys(second, "ps3");
        assertEquals(JsonParser.parseString(Files.readString(path(uninterrupted, 3))),
            JsonParser.parseString(Files.readString(path(resumed, 3))));
    }

    @Test
    void loadingDeadOrWinningSaveRestoresEndScreen() {
        SaveManager manager = new SaveManager(directory);
        manager.save(1, change(level(), json -> json.getAsJsonObject("playerData").addProperty("currentHp", 0)));
        Engine engine = new Engine(manager);
        keys(engine, "l1");
        assertEquals(GameState.GAME_OVER, engine.state());
        manager.save(2, change(level(), json -> {
            while (json.getAsJsonArray("items").size() > 2) json.getAsJsonArray("items").remove(0);
            json.getAsJsonObject("exit").addProperty("active", true);
            json.getAsJsonObject("worldData").getAsJsonArray("world").get(9).getAsJsonArray()
                .set(3, new com.google.gson.JsonPrimitive("OPEN_DOOR"));
            json.getAsJsonObject("playerData").addProperty("x", 9);
        }));
        keys(engine, "ml2");
        assertEquals(GameState.WIN, engine.state());
    }

    private static Path path(Path base, int slot) {
        return base.resolve("slot" + slot).resolve("save.json");
    }
}
