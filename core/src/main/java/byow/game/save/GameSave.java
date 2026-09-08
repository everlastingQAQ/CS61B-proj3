package byow.game.save;

import java.util.Random;

public record GameSave(
    WorldData worldData,
    PlayerData playerData,
    Random random
) {}
