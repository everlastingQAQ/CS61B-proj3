package byow.game.save;

import byow.game.monster.Monster;
import byow.game.monster.MonsterState;
import byow.game.monster.MonsterType;

/** 保存一只怪物所有会影响后续行为的数据。 */
public record MonsterData(
    long id,
    int x,
    int y,
    MonsterType type,
    MonsterState state,
    int homeX,
    int homeY,
    boolean patrolRouteInitialized,
    int patrolAX,
    int patrolAY,
    int patrolBX,
    int patrolBY,
    int patrolTargetIndex,
    int committedTargetX,
    int committedTargetY,
    int commitmentTurns
) {
    public static MonsterData from(Monster monster) {
        return new MonsterData(
            monster.id(),
            monster.x(),
            monster.y(),
            monster.type(),
            monster.state(),
            monster.homeX(),
            monster.homeY(),
            monster.hasPatrolRoute(),
            monster.patrolAX(),
            monster.patrolAY(),
            monster.patrolBX(),
            monster.patrolBY(),
            monster.patrolTargetIndex(),
            monster.committedTargetX(),
            monster.committedTargetY(),
            monster.commitmentTurns()
        );
    }

    public Monster toMonster() {
        return new Monster(
            id,
            x,
            y,
            type,
            state,
            homeX,
            homeY,
            patrolRouteInitialized,
            patrolAX,
            patrolAY,
            patrolBX,
            patrolBY,
            patrolTargetIndex,
            committedTargetX,
            committedTargetY,
            commitmentTurns
        );
    }
}
