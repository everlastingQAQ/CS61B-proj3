package byow.game.save;

import byow.game.monster.MonsterType;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 验证新增 Buff 状态的序列化和旧存档默认值。 */
class SaveDataCompatibilityTest {
    private final Gson gson = new Gson();

    @Test
    void buffStateSurvivesJsonRoundTrip() {
        PlayerData player = new PlayerData(2, 3, 1, 0, 2, 3, 1);
        GameSave original = new GameSave(
            null,
            player,
            List.of(),
            List.of(),
            null,
            9,
            42L,
            6L,
            2,
            true,
            List.of(MonsterType.HUNTER)
        );

        GameSave restored = gson.fromJson(gson.toJson(original), GameSave.class);

        assertEquals(1, restored.playerData().shieldCharges());
        assertEquals(2, restored.frozenMonsterTurns());
        assertTrue(restored.radarActive());
        assertEquals(List.of(MonsterType.HUNTER), restored.seenMonsterTypes());
    }

    @Test
    void oldJsonUsesInactiveBuffDefaults() {
        PlayerData oldPlayer = gson.fromJson(
            "{\"x\":2,\"y\":3,\"currentHp\":3,\"maxHp\":3}",
            PlayerData.class
        );
        GameSave oldGame = gson.fromJson("{}", GameSave.class);

        assertEquals(0, oldPlayer.shieldCharges());
        assertEquals(0, oldGame.frozenMonsterTurns());
        assertFalse(oldGame.radarActive());
    }
}
