package byow.game.save;

import java.util.List;

public record GameSave(
    WorldData worldData,
    PlayerData playerData,
    List<ItemData> items,
    long randomState
) {}
