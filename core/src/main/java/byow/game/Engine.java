package byow.game;

import byow.game.player.Player;
import byow.game.tile.TETile;
import byow.game.worldGenerator.WorldGenerator;

import java.util.Random;

public class Engine {
    /** 当前游戏世界。 */
    private TETile[][] world = null;

    /** 当前游戏玩家。 */
    private Player player;

    /** 用于暂存用户输入的新世界随机种子。 */
    private final StringBuilder seedString = new StringBuilder();

    /** 随机种子生成的随机数生成器。 */
    private Random random;

    /** 当前游戏所处的状态。 */
    private GameState state = GameState.MENU;

    /**
     * 根据不同的游戏状态分别处理输入
     * */
    public void handleKey(char key) {

        char c = Character.toUpperCase(key);

        switch (state) {
            case MENU -> handleMenuInput(c);
            case SEED -> handleSeedInput(c);
            case PLAYING -> handlePlayingInput(c);
            case QUIT -> {
            }
        }
    }

    /**
     * 处理主菜单状态下的输入。
     *
     * N：开始新游戏并进入种子输入状态。
     * L：加载已有游戏。
     * Q：退出游戏。
     *
     * @param c 用户输入的字符
     */
    private void handleMenuInput(char c) {
        switch (c) {
            case 'N' -> {
                seedString.setLength(0);
                state = GameState.SEED;
            }

            case 'L' -> {
                // TODO load game
            }

            case 'Q' -> {
                state = GameState.QUIT;
            }
        }
    }

    /**
     * 处理随机种子的输入
     *
     * 在读取到 S 之前，将输入的字符依次加入 seedString
     * 在读取到 S 之后，将根据种子生成世界，并进入 PLAYING 状态
     *
     * @param c 用户输入的字符
     */
    private void handleSeedInput(char c) {
        if (c == 'S') {
            long seed = Long.parseLong(seedString.toString());

            random = new Random(seed);

            world = WorldGenerator.generate(seed);

            player = new Player(world, random);

            state = GameState.PLAYING;

            return;
        }

        if (Character.isDigit(c)) {
            seedString.append(c);
        }
    }

    /**
     * 处理游戏进行状态下的输入。
     * 后续将在这里处理玩家移动、保存游戏等操作。
     *
     * @param c 用户输入的字符
     */
    private void handlePlayingInput(char c) {
        switch (c) {
            case 'W' ->
                player.moveUp(world);

            case 'A' ->
                player.moveLeft(world);

            case 'S' ->
                player.moveDown(world);

            case 'D' ->
                player.moveRight(world);
        }
    }


    public GameState state() {
        return state;
    }

    public TETile[][] world() {
        return world;
    }

    public Player player() {
        return player;
    }

    public String seedString() {
        return seedString.toString();
    }

}
