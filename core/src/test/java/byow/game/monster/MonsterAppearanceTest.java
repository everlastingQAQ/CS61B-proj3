package byow.game.monster;

import byow.game.tile.TETile;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 验证五种怪物使用方案 A 的独立字符和颜色。 */
class MonsterAppearanceTest {

    @Test
    void everyMonsterTypeHasExpectedAppearance() {
        assertAppearance(MonsterType.HUNTER, 'H', new Color(255, 80, 80));
        assertAppearance(MonsterType.AMBUSHER, 'A', new Color(210, 100, 255));
        assertAppearance(MonsterType.GUARDIAN, 'G', new Color(255, 190, 70));
        assertAppearance(MonsterType.PATROLLER, 'P', new Color(80, 180, 255));
        assertAppearance(MonsterType.COWARD, 'C', new Color(120, 230, 120));
    }

    @Test
    void monsterCharactersAreUnique() {
        Set<Character> characters = new HashSet<>();
        for (MonsterType type : MonsterType.values()) {
            characters.add(MonsterAppearance.get(type).character());
        }
        assertEquals(MonsterType.values().length, characters.size());
    }

    /** 验证一种怪物的字符和前景色。 */
    private static void assertAppearance(MonsterType type, char character, Color color) {
        TETile appearance = MonsterAppearance.get(type);
        assertEquals(character, appearance.character());
        assertEquals(color, appearance.textColor());
    }
}
