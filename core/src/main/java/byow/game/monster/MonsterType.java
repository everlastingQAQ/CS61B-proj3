package byow.game.monster;

public enum MonsterType {
    HUNTER(MonsterState.CHASING),
    AMBUSHER(MonsterState.CHASING),
    GUARDIAN(MonsterState.IDLE),
    PATROLLER(MonsterState.PATROLLING),
    COWARD(MonsterState.CHASING);

    /** 怪物生成时的行为状态。 */
    private final MonsterState initialState;

    /** 构造函数。 */
    MonsterType(MonsterState initialState) {
        this.initialState = initialState;
    }

    /** 返回该类怪物的初始状态。 */
    public MonsterState initialState() {
        return initialState;
    }
}
