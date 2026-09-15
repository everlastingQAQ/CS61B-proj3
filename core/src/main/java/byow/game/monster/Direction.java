package byow.game.monster;

import byow.game.random.GameRandom;

import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;

public enum Direction {
    LEFT(-1, 0),
    RIGHT(1, 0),
    UP(0, 1),
    DOWN(0, -1);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int dx() {
        return dx;
    }

    public int dy() {
        return dy;
    }

    public static Direction randomDirection(GameRandom random) {
        Direction[] directions = values();
        return directions[random.nextInt(directions.length)];
    }
}
