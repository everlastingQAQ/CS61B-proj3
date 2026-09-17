package byow.game.save;

import byow.game.monster.MonsterType;

import java.util.List;

public record GameSave(
    WorldData worldData,
    PlayerData playerData,
    List<ItemData> items,
    List<MonsterData> monsters,
    boolean[][] explored,
    int visionRadius,
    long randomState,
    long nextMonsterId,
    int frozenMonsterTurns,
    boolean radarActive,
    List<MonsterType> seenMonsterTypes
) {}
