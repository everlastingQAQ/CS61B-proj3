package byow.game.item;

import byow.game.monster.MonsterType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 验证遗物的固定 Buff 映射和额外拾取边界。 */
class ItemRulesTest {

    @Test
    void eachItemTypeHasItsFixedMonsterAndBuff() {
        assertRule(ItemType.CRYSTAL, MonsterType.HUNTER, BuffType.VISION);
        assertRule(ItemType.COIN, MonsterType.COWARD, BuffType.HEAL);
        assertRule(ItemType.KEY, MonsterType.GUARDIAN, BuffType.RADAR);
        assertRule(ItemType.ORB, MonsterType.AMBUSHER, BuffType.SHIELD);
        assertRule(ItemType.RUNE, MonsterType.PATROLLER, BuffType.STUN);
    }

    @Test
    void onlyItemsBeyondRequiredCountGrantBuffs() {
        assertFalse(ItemRules.shouldGrantBuff(3));
        assertTrue(ItemRules.shouldGrantBuff(4));
        assertTrue(ItemRules.shouldGrantBuff(5));
    }

    /** 验证完整规则和两个便捷查询返回一致的固定映射。 */
    private static void assertRule(
        ItemType itemType,
        MonsterType monsterType,
        BuffType buffType
    ) {
        ItemRules.Rule rule = ItemRules.ruleFor(itemType);

        assertEquals(monsterType, rule.monsterType());
        assertEquals(buffType, rule.buffType());
        assertEquals(monsterType, ItemRules.monsterFor(itemType));
        assertEquals(buffType, ItemRules.buffFor(itemType));
    }
}
