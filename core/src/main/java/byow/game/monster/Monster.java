package byow.game.monster;

import byow.game.player.Player;
import byow.game.random.GameRandom;
import byow.game.tile.TETile;
import byow.game.tile.TileRules;

public class Monster {
    private int x;
    private int y;

    public Monster(TETile[][] world, GameRandom random, Player player) {
        int originX = random.nextInt(1, world.length - 1);
        int originY = random.nextInt(1, world[0].length - 1);

        // 判断当前位置是否玩家可通行,如果不可以再次随机一个
        while (!isOriginPositon(world, player, originX, originY)) {
            originX = random.nextInt(1, world.length - 1);
            originY = random.nextInt(1, world[0].length - 1);
        }

        this.x = originX;
        this.y = originY;
    }

    private boolean isPlaceWalkable(TETile[][] world, int width, int height) {
        if (width < 1 || width >= world.length - 1 || height < 1 || height >= world[0].length - 1) {
            return false;
        }
        return TileRules.isWalkable(world[width][height]);
    }

    private boolean isOriginPositon(TETile[][] world, Player player, int width, int height) {
        // 不能生成在不能行走的道路
        if (!isPlaceWalkable(world, width, height)) {
            return false;
        }

        // 不能生成在player的位置
        if (player.x() == width && player.y() == height) {
            return false;
        }

        // 不能初始位置离玩家太近
        int distance = Math.abs(width - player.x()) + Math.abs(height - player.y());
        if (distance < 10) {
            return false;
        }

        return true;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
