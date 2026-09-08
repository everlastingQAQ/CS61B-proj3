package byow.game;

import byow.game.player.Player;
import byow.game.save.GameSave;
import byow.game.save.PlayerData;
import byow.game.save.SaveManager;
import byow.game.save.WorldData;
import byow.game.tile.TETile;
import byow.game.worldGenerator.WorldGenerator;
import com.badlogic.gdx.Game;

import java.util.Random;

import static byow.game.tile.TileConverter.toTETile;
import static byow.game.tile.TileConverter.toTileType;

public class Engine {
    /** 当前游戏世界。 */
    private TETile[][] world = null;

    /** 当前游戏玩家。 */
    private Player player;

    /** 用于暂存用户输入的新世界随机种子。 */
    private final StringBuilder seedString = new StringBuilder();

    /** 当前世界随机种子 */
    private long seed;

    /** 随机种子生成的随机数生成器。 */
    private Random random;

    /** 当前游戏所处的状态。 */
    private GameState state = GameState.MENU;

    /** 游戏存档管理器 */
    private final SaveManager saveManager = new SaveManager();

    /** 是否检测到保存退出的冒号 */
    private boolean colonPressed = false;

    private boolean quitAfterSave;

    /** 是否请求退出游戏 */
    private boolean shouldQuit = false;

    /**
     * 根据不同的游戏状态分别处理输入
     * */
    public void handleKey(char key) {

        char c = Character.toUpperCase(key);

        switch (state) {
            case MENU -> handleMenuInput(c);
            case SEED -> handleSeedInput(c);
            case PLAYING -> handlePlayingInput(c);
            case LOAD -> handleLoadInput(c);
            case PAUSE -> handlePauseInput(c);
            case SAVE -> handleSaveInput(c);
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
            case 'L' -> state = GameState.LOAD;
            case 'Q' -> quit();
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
            initNewWorld();
            return;
        }

        if (Character.isDigit(c)) {
            seedString.append(c);
        }
    }

    private void handleQuitInput(char c) {

    }

    /**
     * 在输入完种子后初始化世界
     * */
    private void initNewWorld() {
        // 创建 seed
        this.seed = Long.parseLong(seedString.toString());

        // 床架随机数
        random = new Random(seed);

        // 创建世界
        world = WorldGenerator.generate(seed);

        // 创建人物
        player = new Player(world, random);

        // 更改游戏状态
        state = GameState.PLAYING;
    }

    /**
     * 处理加载存档时的输入
     * */
    private void handleLoadInput(char c) {
        int slot = Character.getNumericValue(c);
        loadGame(slot);
        state = GameState.PLAYING;
    }

    /**
     * 处理保存时的输入
     * */
    private void handleSaveInput(char c) {
        int slot = Character.getNumericValue(c);
        saveGame(slot);
        if (quitAfterSave) {
            quit();
        } else {
            state = GameState.PAUSE;
        }
    }

    private void handlePauseInput(char c) {
        switch(c) {
            case 'P' -> state = GameState.PLAYING;
            case 'S' -> {
                quitAfterSave = false;
                state = GameState.SAVE;
            }
            case 'Q' -> {
                quitAfterSave = true;
                state = GameState.MENU;
            }

        }
    }

    /**
     * 处理游戏进行状态下的输入。
     * 后续将在这里处理玩家移动、保存游戏等操作。
     *
     * @param c 用户输入的字符
     */
    private void handlePlayingInput(char c) {

        if (c == ':') {
            colonPressed = true;
            return;
        }

        if (colonPressed) {
            if (c == 'Q') {
                quit();
                return;
            }
            colonPressed = false;
        }

        switch (c) {
            case 'W' -> player.moveUp(world);
            case 'A' -> player.moveLeft(world);
            case 'S' -> player.moveDown(world);
            case 'D' -> player.moveRight(world);
        }
    }

    /**
     * 根据存档加载游戏
     * */
    private void loadGame(int slot) {
        // 加载 gamesave
        GameSave gamesave = saveManager.load(slot);

        // 加载随机数
        this.random = gamesave.random();

        // 加载世界
        this.world = toTETile(gamesave.worldData().world());

        // 加载人物
        this.player = new Player(world, gamesave.playerData().x(), gamesave.playerData().y());
    }

    /**
     * 保存游戏于对应存档位中
     * */
    private void saveGame(int slot) {
        GameSave gameSave = new GameSave(
            new WorldData(toTileType(world)),
            new PlayerData(
                player.x(),
                player.y()
            ),
            random
            );
        saveManager.save(slot, gameSave);
    }

    /**
     * 退出游戏
     * */
    private void quit() {
        shouldQuit = true;
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

    public boolean shouldQuit() {
        return shouldQuit;
    }

}
