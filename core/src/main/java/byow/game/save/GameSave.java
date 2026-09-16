package byow.game.save;

import byow.game.monster.Monster;

import java.util.List;

public record GameSave(
    WorldData worldData,
    PlayerData playerData,
    List<ItemData> items,
    MonsterData monsterData,
    boolean[][] explored,
    int visionRadius,
    long randomState
) {}
