package byow.game.save;

public record GameSave(
    WorldData worldData,
    PlayerData playerData,
    long randomState
) {}
