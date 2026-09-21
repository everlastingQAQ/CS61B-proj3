package byow.game.item;

/** 遗物在第 4 或第 5 次拾取时可能提供的 Buff 类型。 */
public enum BuffType {
    /** 永久增加玩家视野半径。 */
    VISION("视野强化"),

    /** 恢复玩家生命值。 */
    HEAL("生命恢复"),

    /** 在 HUD 中提示出口方向。 */
    RADAR("出口雷达"),

    /** 抵挡下一次怪物伤害。 */
    SHIELD("护盾"),

    /** 暂停全部怪物的行动回合。 */
    STUN("怪物冻结");

    private final String displayName;

    BuffType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
