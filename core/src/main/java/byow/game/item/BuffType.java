package byow.game.item;

/** 遗物在第 4 或第 5 次拾取时可能提供的 Buff 类型。 */
public enum BuffType {
    /** 永久增加玩家视野半径。 */
    VISION,

    /** 恢复玩家生命值。 */
    HEAL,

    /** 在 HUD 中提示出口方向。 */
    RADAR,

    /** 抵挡下一次怪物伤害。 */
    SHIELD,

    /** 暂停全部怪物的行动回合。 */
    STUN
}
