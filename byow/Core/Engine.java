package byow.Core;

import byow.Core.Input.Input;
import byow.Core.Input.InputSource;
import byow.Core.WorldGenerator.WorldGenerator;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

/**
 * Engine 类负责根据当前的游戏状态执行对应逻辑，是游戏的核心控制器。
 *
 * @author Everlasting
 * */
public class Engine {

    /** 用于将当前世界渲染到屏幕上的地图渲染器。 */
    private TERenderer ter = new TERenderer();

    /** 当前游戏世界。 */
    private TETile[][] world = null;

    /** 用于暂存用户输入的新世界随机种子。 */
    private StringBuilder seedString = new StringBuilder();

    /** 当前游戏所处的状态。 */
    private GameState state = GameState.MENU;

    /** 游戏世界的宽度和高度 */
    public static final int WIDTH = 101;
    public static final int HEIGHT = 61;

    /**
     * 键盘交互模式
     * 持续读取用户输入并处理对应操作。
     * */
    public void interactWithKeyboard() {
        InputSource inputSource = Input.keyboardInput();
        interact(inputSource);
    }

    /**
     * 指令交互模式（并用于自动评分，不要大幅度修改）
     *
     * @param input 要输入给程序的一串字符
     * @return 处理完所有输入后最终的世界状态
     */
    public TETile[][] interactWithInputString(String input) {
        InputSource inputSource = Input.stringInput(input);
        interact(inputSource);
        return world;
    }

    /**
     * 统一处理来自不同输入源的输入，并根据不同的 GameState 对输入进行不同的处理。
     *
     * @param inputSource 当前使用的输入源
     * */
    private void interact(InputSource inputSource) {

        while (inputSource.possibleNextInput()) {
            char c = Character.toUpperCase(inputSource.getNextKey());

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
                break;
            case 'L':
                loadGame();
                break;
            case 'Q':
                quit();
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
        if (c == 'S') {
            long seed = Long.parseLong(seedString.toString());
            world = WorldGenerator.generate(seed);
            state = GameState.PLAYING;
        } else {
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

    }


}