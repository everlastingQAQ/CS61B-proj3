package byow.Core;

import byow.Core.Input.Input;
import byow.Core.Input.InputSource;
import byow.Core.Player.Player;
import byow.Core.Render.MenuRender;
import byow.Core.Render.SeedInputRender;
import byow.Core.Render.WorldRender;
import byow.Core.WorldGenerator.WorldGenerator;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.util.Random;

/**
 * Engine 类负责根据当前的游戏状态执行对应逻辑，是游戏的核心控制器。
 *
 * @author Everlasting icovo
 * */
public class Engine {

    /** 当前游戏世界。 */
    private TETile[][] world = null;

    /** 当前游戏玩家。 */
    private Player player;

    /** 用于暂存用户输入的新世界随机种子。 */
    private final StringBuilder seedString = new StringBuilder();

    /** 用于渲染世界的渲染器 */
    private WorldRender worldrender = new WorldRender();

    /** 随机种子生成的随机数生成器。 */
    private Random random;

    /** 当前游戏所处的状态。 */
    private GameState state = GameState.MENU;

    /** 当前游戏历史存档 */
    private final StringBuilder historyString = new StringBuilder();

    /**
     * 键盘交互模式
     * 持续读取用户输入并处理对应操作。
     * */
    public void interactWithKeyboard() {
        // 新建输入源
        InputSource inputSource = Input.keyboardInput();

        // 渲染菜单
        MenuRender.render();

        // 处理输入
        interact(inputSource);
    }

    /**
     * 指令交互模式（并用于自动评分，不要大幅度修改）
     *
     * @param input 要输入给程序的一串字符
     * @return 处理完所有输入后最终的世界状态
     */
    public TETile[][] interactWithInputString(String input) {
        // 新建输入源
        InputSource inputSource = Input.stringInput(input);

        // 处理输入
        interact(inputSource);
        return world;
    }

    /**
     * 统一处理来自不同输入源的输入，并根据不同的 GameState 对输入进行不同的处理。
     *
     * @param inputSource 当前使用的输入源
     * */
    private void interact(InputSource inputSource) {

        // 若存在下一个输入，则一直处理
        while (inputSource.possibleNextInput()) {
            // 从输入源取出输入字符并大写
            char c = Character.toUpperCase(inputSource.getNextKey());

            // 存储历史命令
            historyString.append(c);

            // 根据不同的游戏状态分别处理输入
            switch (state) {
                case MENU -> handleMenuInput(c);
                case SEED -> handleSeedInput(c);
                case PLAYING -> handlePlayingInput(c);
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
            case 'N':
                state = GameState.SEED;
                SeedInputRender.render("");
                break;
            case 'L':
//                loadGame();
                break;
            case 'Q':
//                quit();
                break;
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
        if (c == 'S') { // 种子输入完了
            // 获取种子
            long seed = Long.parseLong(seedString.toString());

            // 初始化随机数生成器
            random = new Random(seed);

            // 初始化世界
            world = WorldGenerator.generate(seed);

            // 初始化人物
            player = new Player(world, random);

            // 更新游戏状态
            state = GameState.PLAYING;

            // 渲染游戏画面
            worldrender.render(world);

        } else { // 种子没有输入完
            // 更新当前种子
            seedString.append(c);

            // 渲染输入种子画面
            SeedInputRender.render(seedString.toString());
        }
    }


    /**
     * 处理游戏进行状态下的输入。
     * 后续将在这里处理玩家移动、保存游戏等操作。
     *
     * @param c 用户输入的字符
     */
    private void handlePlayingInput(char c) {
        switch(c) {
            case 'W' -> player.moveUp(world);
            case 'A' -> player.moveLeft(world);
            case 'S' -> player.moveDown(world);
            case 'D' -> player.moveRight(world);
        }
        // 渲染新世界
        worldrender.render(world);
    }

}