package byow.game.item;

/** 游戏中五种互不重复的遗物类型。 */
public enum ItemType {
    /** 水晶遗物，激活 Hunter，额外拾取时提供视野 Buff。 */
    CRYSTAL,

    /** 金币遗物，激活 Coward，额外拾取时提供治疗 Buff。 */
    COIN,

    /** 钥匙遗物，激活 Guardian，额外拾取时提供雷达 Buff。 */
    KEY,

    /** 宝珠遗物，激活 Ambusher，额外拾取时提供护盾 Buff。 */
    ORB,

    /** 符文遗物，激活 Patroller，额外拾取时提供冻结 Buff。 */
    RUNE
}
