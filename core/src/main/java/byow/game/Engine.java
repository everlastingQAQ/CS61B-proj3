package byow.game;

import byow.game.monster.MonsterController;
import byow.game.item.Item;
import byow.game.itemGenerator.ItemGenerator;
import byow.game.monster.Monster;
import byow.game.player.Player;
import byow.game.random.GameRandom;
import byow.game.save.*;
import byow.game.tile.TETile;
import byow.game.visioncauculate.Visioncauculate;
import byow.game.worldGenerator.WorldGenerator;

import java.util.*;

import static byow.game.GameConfig.ITEM_NUMBER;
import static byow.game.monster.MonsterType.*;
import static byow.game.tile.TileConverter.toTETile;
import static byow.game.tile.TileConverter.toTileType;

public class Engine {

    // =====================
    // Fields
    // =====================

    /** 当前游戏世界。 */
    private TETile[][] world = null;

    /** 当前游戏玩家。 */
    private Player player;

    /** 当前游戏物品。 */
    private List<Item> items;

    /** 当前唯一怪物 */
    Monster monster;

    /** 怪物管理。 */
    private final MonsterController monsterController = new MonsterController();

    /** 当前拥有的物品数。 */
    private int collectedCount;

    /** 当前玩家视野范围。 */
    private int visionRadius;

    /** 用于暂存用户输入的新世界随机种子。 */
    private final StringBuilder seedString = new StringBuilder();

    /** 当前世界随机种子 */
    private long seed;

    /** 随机种子生成的随机数生成器。 */
    private GameRandom random;

    /** 当前游戏所处的状态。 */
    private GameState state = GameState.MENU;

    /** 游戏存档管理器 */
    private final SaveManager saveManager = new SaveManager();

    /** 保存完成后是否返回主菜单。 */
    private boolean returnToMenuAfterSave;

    /** 是否请求退出游戏 */
    private boolean shouldQuit = false;

    /** 视觉可视范围。 */
    private boolean[][] visible;

    /** 已经探索过的范围。 */
    private boolean[][] explored;


    // =====================
    // Public input entry
    // =====================

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
            case CONFIRM_QUIT -> handleConfirmQuitInput(c);
        }
    }

    // =====================
    // State input handlers
    // =====================

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

        if (c == 'B') {
            state = GameState.MENU;
            return;
        }

        if (Character.isDigit(c)) {
            seedString.append(c);
        }
    }

    /**
     * 处理加载存档时的输入
     * */
    private void handleLoadInput(char c) {
        if (c == 'B') {
            state = GameState.MENU;
            return;
        }

        if (c < '1' || c > '5') {
            return;
        }
        int slot = Character.getNumericValue(c);
        if (!saveManager.exists(slot)) {
            return; // TODO：保持 LOAD 状态，之后显示“该位置没有存档”
        }
        loadGame(slot);
        state = GameState.PLAYING;
    }

    /**
     * 处理保存时的输入
     * */
    private void handleSaveInput(char c) {
        if (c == 'B') {
            state = GameState.PAUSE;
            returnToMenuAfterSave = false;
            return;
        }

        if (c < '1' || c > '5') {
            return;
        }

        int slot = Character.getNumericValue(c);
        saveGame(slot);
        if (returnToMenuAfterSave) {
            state = GameState.MENU;
        } else {
            state = GameState.PAUSE;
        }
    }

    /**
     * 处理暂停时的输入
     * */
    private void handlePauseInput(char c) {
        switch(c) {
            case 'P' -> state = GameState.PLAYING;
            case 'S' -> {
                returnToMenuAfterSave = false;
                state = GameState.SAVE;
            }
            case 'Q' -> state = GameState.CONFIRM_QUIT;
        }
    }

    /**
     * 处理确认是否退出时的输入
     * */
    private void handleConfirmQuitInput(char c) {
        switch(c) {
            case 'Y' -> {
                returnToMenuAfterSave = true;
                state = GameState.SAVE;
            }
            case 'N' -> state = GameState.MENU;
            case 'B' -> state = GameState.PAUSE;
        }
    }

    /**
     * 处理游戏进行状态下的输入。
     * 后续将在这里处理玩家移动、保存游戏等操作。
     *
     * @param c 用户输入的字符
     */
    private void handlePlayingInput(char c) {

        boolean moved = switch (c) {
            case 'W' -> player.moveUp(world);
            case 'A' -> player.moveLeft(world);
            case 'S' -> player.moveDown(world);
            case 'D' -> player.moveRight(world);
            case 'P' -> {
                state = GameState.PAUSE;
                yield false;
            }
            default -> false;
        };

        if (moved) {
            resolvePlayerInteractions();
            resolveMonsterInteractions();
            updateVision();
        }
    }

    // =====================
    // handle game interact
    // =====================

    // 处理人物交互
    private void resolvePlayerInteractions() {
        checkItemCollection();
    }

    // 处理是否收集到物品
    private void checkItemCollection() {
        int x = player.x();
        int y = player.y();

        Item collectItem = null;
        for (Item item : items) {
            if (item.x() == x && item.y() == y) {
                collectItem = item;
                break;
            }
        }

        if (collectItem != null) {
            collectItem(collectItem);
        }
    }

    // 收集物品
    private void collectItem(Item item) {
        // 拾取
        collectedCount++;

        // 从地图移除
        items.remove(item);

        // TODO: 产生效果
    }

    // 处理怪物交互
    private void resolveMonsterInteractions() {
        monsterController.takeTurn(world, monster, player);
    }

    // =====================
    // Update Vision
    // ====================

    public void updateVision() {
        visible = Visioncauculate.calculate(world, player, visionRadius);

        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (visible[x][y]) {
                    explored[x][y] = true;
                }
            }
        }
    }

    // =====================
    // Game lifecycle
    // =====================

    /**
     * 在输入完种子后初始化世界
     * */
    private void initNewWorld() {

        try {
            seed = Long.parseLong(seedString.toString());
        } catch (NumberFormatException e) {
            return;
        }

        // 创建随机数
        random = new GameRandom(seed);

        // 创建世界
        world = WorldGenerator.generate(seed);

        // 创建人物
        player = new Player(world, random);

        // 创建物品
        items = ItemGenerator.generate(world, player, random);

        // 创建怪物
        monster = new Monster(world, random, player, GUARDIAN);

        // 初始化物品数量
        collectedCount = 0;

        // 初始化视野范围
        visionRadius = 7;

        // 初始化可视范围
        visible = new boolean[world.length][world[0].length];

        // 初始化探索范围
        explored = new boolean[world.length][world[0].length];

        updateVision();

        // 更改游戏状态
        state = GameState.PLAYING;
    }

    /**
     * 退出游戏
     * */
    private void quit() {
        shouldQuit = true;
    }


    // =====================
    // Save / Load
    // =====================

    // 复制boolean数组
    private static boolean[][] copyGrid(boolean[][] source) {
        boolean[][] copy = new boolean[source.length][];

        for (int x = 0; x < source.length; x++) {
            copy[x] = source[x].clone();
        }

        return copy;
    }

    /**
     * 根据存档加载游戏
     **/
    private void loadGame(int slot) {
        // 加载 gamesave
        GameSave gamesave = saveManager.load(slot);

        // 加载随机数
        this.random = new GameRandom(gamesave.randomState());

        // 加载世界
        this.world = toTETile(gamesave.worldData().world());

        // 加载人物
        this.player = new Player(world, gamesave.playerData().x(), gamesave.playerData().y());

        // 加载物品
        this.items = new ArrayList<>();

//        // 加载怪物
//        this.monster = new Monster(gamesave.monsterData().x(), gamesave.monsterData().y());

        // 加载物品栏
        for (ItemData data : gamesave.items()) {
            this.items.add(
                new Item(
                    data.x(),
                    data.y(),
                    data.type()
                )
            );
        }

        // 设置拾取的物品个数
        collectedCount = ITEM_NUMBER - items().size();

        // 加载可视范围
        visionRadius = gamesave.visionRadius();

        // 加载已探索区域
        this.explored = copyGrid(gamesave.explored());

        // 加载玩家可视范围
        this.visible = new boolean[world.length][world[0].length];
        updateVision();
    }

    /**
     * 保存游戏于对应存档位中
     * */
    private void saveGame(int slot) {
        // 求物品的存档数组
        List<ItemData> itemDataList = new ArrayList<>();

        for (Item item : items) {
            itemDataList.add(
                new ItemData(
                    item.x(),
                    item.y(),
                    item.type()
                )
            );
        }

        GameSave gameSave = new GameSave(
            new WorldData(toTileType(world)),
            new PlayerData(player.x(), player.y()),
            itemDataList,
            new MonsterData(monster.x(), monster.y()),
            copyGrid(explored),
            visionRadius,
            random.state()
            );
        saveManager.save(slot, gameSave);
    }

    // =====================
    // Getters
    // =====================

    public GameState state() {
        return state;
    }

    public TETile[][] world() {
        return world;
    }

    public Player player() {
        return player;
    }

    public List<Item> items() {
        return items;
    }

    public Monster monster() {
        return monster;
    }

    public String seedString() {
        return seedString.toString();
    }

    public boolean shouldQuit() {
        return shouldQuit;
    }

    public int CollectedCount() {
        return collectedCount;
    }

    public int visionRadius() {
        return visionRadius;
    }

    public boolean[][] visible() {
        return visible;
    }

    public boolean[][] explored() {
        return explored;
    }
}
