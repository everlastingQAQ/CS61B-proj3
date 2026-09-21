package byow.game.save;

import byow.game.GameExit;

/** 存档中的出口坐标和激活状态。 */
public record ExitData(int x, int y, boolean active) {
    public static ExitData from(GameExit exit) {
        return new ExitData(exit.x(), exit.y(), exit.active());
    }

    public GameExit toExit() {
        return new GameExit(x, y, active);
    }
}
