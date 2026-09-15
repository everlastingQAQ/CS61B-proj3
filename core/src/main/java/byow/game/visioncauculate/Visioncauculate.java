package byow.game.visioncauculate;

import byow.game.player.Player;
import byow.game.tile.TETile;

public class Visioncauculate {

    public static boolean[][] calculate(TETile[][] world, Player player, int visionRadius) {
        boolean[][] visible = new boolean[world.length][world[0].length];

        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                if (inRadius(i, j, player.x(), player.y(), visionRadius)) {
                    visible[i][j] = true;
                }
            }
        }

        return visible;
    }

    private static boolean inRadius(int startX, int startY, int endX, int endY, int radius) {
        return (startX - endX) * (startX - endX) + (startY - endY) * (startY - endY) <= radius * radius;
    }

    private static boolean hasLineOfSight() {
        return false;
    }
}
