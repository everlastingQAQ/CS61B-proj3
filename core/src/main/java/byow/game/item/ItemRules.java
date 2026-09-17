package byow.game.item;

import byow.game.monster.MonsterType;

import static byow.game.GameConfig.REQUIRED_ITEM_COUNT;

/** 集中定义遗物类型与固定 Buff 之间的规则。 */
public final class ItemRules {

    /** 保存一种遗物对应的怪物类型和 Buff 类型。 */
    public record Rule(MonsterType monsterType, BuffType buffType) {}

    /** Vision 增加的视野半径。 */
    public static final int VISION_RADIUS_BONUS = 2;

    /** Heal 恢复的生命值。 */
    public static final int HEAL_AMOUNT = 1;

    /** Shield 提供的伤害抵挡次数。 */
    public static final int SHIELD_CHARGES = 1;

    /** Stun 冻结怪物的回合数。 */
    public static final int STUN_TURNS = 2;

    /**
     * 返回指定遗物对应的完整固定规则。
     *
     * @param type 遗物类型
     * @return 遗物对应的怪物类型和 Buff 类型
     */
    public static Rule ruleFor(ItemType type) {
        return switch (type) {
            case CRYSTAL -> new Rule(MonsterType.HUNTER, BuffType.VISION);
            case COIN -> new Rule(MonsterType.COWARD, BuffType.HEAL);
            case KEY -> new Rule(MonsterType.GUARDIAN, BuffType.RADAR);
            case ORB -> new Rule(MonsterType.AMBUSHER, BuffType.SHIELD);
            case RUNE -> new Rule(MonsterType.PATROLLER, BuffType.STUN);
        };
    }

    /**
     * 返回指定遗物激活的固定怪物类型。
     *
     * @param type 遗物类型
     * @return 与遗物类型对应的怪物类型
     */
    public static MonsterType monsterFor(ItemType type) {
        return ruleFor(type).monsterType();
    }

    /**
     * 返回指定遗物在额外拾取时提供的固定 Buff。
     *
     * @param type 遗物类型
     * @return 与遗物类型对应的 Buff
     */
    public static BuffType buffFor(ItemType type) {
        return ruleFor(type).buffType();
    }

    /**
     * 判断本次拾取是否超过通关所需数量并应当给予 Buff。
     * 传入的数量表示完成本次拾取后的遗物总数。
     *
     * @param collectedCount 完成本次拾取后的遗物总数
     * @return 超过通关所需数量时返回 true
     */
    public static boolean shouldGrantBuff(int collectedCount) {
        return collectedCount > REQUIRED_ITEM_COUNT;
    }

    private ItemRules() {}
}
