package byow.game.monster;

public enum MonsterType {
    HUNTER("猎手", MonsterState.CHASING),
    AMBUSHER("伏击者", MonsterState.CHASING),
    GUARDIAN("守卫", MonsterState.IDLE),
    PATROLLER("巡逻者", MonsterState.PATROLLING),
    COWARD("胆小鬼", MonsterState.CHASING);

    private final String displayName;
    /** 怪物生成时的行为状态。 */
    private final MonsterState initialState;

    /** 构造函数。 */
    MonsterType(String displayName, MonsterState initialState) {
        this.displayName = displayName;
        this.initialState = initialState;
    }

    public String displayName() {
        return displayName;
    }

    /** 返回该类怪物的初始状态。 */
    public MonsterState initialState() {
        return initialState;
    }
}
