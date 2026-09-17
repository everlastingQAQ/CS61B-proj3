package byow.game;

import byow.game.monster.MonsterController;
import byow.game.monster.MonsterSpawner;
import byow.game.monster.MonsterTurnResult;
import byow.game.item.BuffType;
import byow.game.item.Item;
import byow.game.item.ItemRules;
import byow.game.itemGenerator.ItemGenerator;
import byow.game.monster.Monster;
import byow.game.monster.MonsterType;
import byow.game.player.Player;
import byow.game.random.GameRandom;
import byow.game.save.*;
import byow.game.tile.TETile;
import byow.game.visioncauculate.Visioncauculate;
import byow.game.worldGenerator.WorldGenerator;

import java.util.*;

import static byow.game.GameConfig.ITEM_NUMBER;
import static byow.game.GameConfig.MONSTER_CONTACT_DAMAGE;
import static byow.game.GameConfig.PLAYER_MAX_HP;
import static byow.game.GameConfig.DEFAULT_VISION_RADIUS;
import static byow.game.item.ItemRules.HEAL_AMOUNT;
import static byow.game.item.ItemRules.SHIELD_CHARGES;
import static byow.game.item.ItemRules.STUN_TURNS;
import static byow.game.item.ItemRules.VISION_RADIUS_BONUS;
import static byow.game.tile.TileConverter.toTETile;
import static byow.game.tile.TileConverter.toTileType;

public class Engine {

    /** 首次发现怪物的提示保留多少个有效玩家回合。 */
    private static final int MONSTER_NOTICE_TURNS = 3;

    /** 玩家在该曼哈顿距离内可以看到遗物说明。 */
    private static final int ITEM_HINT_DISTANCE = 3;

    // =====================
    // Fields
    // =====================

    /** 当前游戏世界。 */
    private TETile[][] world = null;

    /** 当前游戏玩家。 */
    private Player player;

    /** 当前游戏物品。 */
    private List<Item> items;

    /** 当前游戏中的全部怪物。空列表表示尚未激活怪物。 */
    private final List<Monster> monsters = new ArrayList<>();

    /** 分配给下一只怪物的稳定 ID。 */
    private long nextMonsterId = 1;

    /** 怪物管理。 */
    private final MonsterController monsterController = new MonsterController();

    /** 当前拥有的物品数。 */
    private int collectedCount;

    /** 当前玩家视野范围。 */
    private int visionRadius;

    /** 全部怪物还需要跳过的行动回合数。 */
    private int frozenMonsterTurns;

    /** Radar Buff 是否已经激活。 */
    private boolean radarActive;

    /** 已经进入过玩家视野的怪物类型，用于保证首次提示只出现一次。 */
    private final EnumSet<MonsterType> seenMonsterTypes =
        EnumSet.noneOf(MonsterType.class);

    /** 当前正在左下角显示的首次怪物类型。 */
    private MonsterType monsterNoticeType;

    /** 当前首次怪物提示还会保留的有效回合数。 */
    private int monsterNoticeTurns;

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
            updateHudContext();
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

    /** 拾取遗物，激活对应怪物，并在超过目标数量时给予固定 Buff。 */
    private void collectItem(Item item) {
        ItemRules.Rule rule = ItemRules.ruleFor(item.type());

        // 先完成拾取，再根据拾取后的总数判断是否应当给予 Buff。
        items.remove(item);
        collectedCount++;

        // 每种遗物都会激活一只固定类型的怪物。
        spawnMonster(rule.monsterType(), item.x(), item.y());

        // 只有第 4 和第 5 个遗物会额外给予 Buff。
        if (ItemRules.shouldGrantBuff(collectedCount)) {
            applyBuff(rule.buffType());
        }
    }

    /**
     * 创建遗物对应的怪物；Guardian 使用触发遗物的位置作为守护点。
     */
    private void spawnMonster(MonsterType type, int itemX, int itemY) {
        Monster monster = MonsterSpawner.spawn(
            nextMonsterId,
            world,
            random,
            player,
            monsters,
            items,
            type
        );

        if (type == MonsterType.GUARDIAN) {
            monster.setHome(itemX, itemY);
        }

        monsters.add(monster);
        nextMonsterId++;
    }

    /** 应用不依赖出口系统的遗物 Buff。 */
    private void applyBuff(BuffType type) {
        switch (type) {
            case VISION -> visionRadius += VISION_RADIUS_BONUS;
            case HEAL -> player.heal(HEAL_AMOUNT);
            case SHIELD -> player.grantShield(SHIELD_CHARGES);
            case STUN -> frozenMonsterTurns = STUN_TURNS;
            case RADAR -> radarActive = true;
        }
    }

    // 处理怪物交互
    private void resolveMonsterInteractions() {
        // Stun 直接跳过整个怪物回合，不覆盖每只怪物原有的行为状态。
        if (frozenMonsterTurns > 0) {
            frozenMonsterTurns--;
            return;
        }

        MonsterTurnResult result = monsterController.takeTurn(world, monsters, player);
        resolveMonsterContacts(result.contacts());
    }

    /** 结算本回合尝试进入玩家位置的怪物。 */
    private void resolveMonsterContacts(List<Monster> contacts) {
        // 接触事件已经按怪物稳定 ID 排序，依次结算每一次攻击。
        for (Monster ignored : contacts) {
            // 一层 Shield 只抵挡一次怪物攻击。
            if (player.consumeShield()) {
                continue;
            }

            player.takeDamage(MONSTER_CONTACT_DAMAGE);
            if (!player.isAlive()) {
                // TODO: 接入游戏结束状态和游戏结束画面。
                break;
            }
        }
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

    /**
     * 更新左下角的首次怪物提示。
     *
     * 已有怪物提示优先显示；提示结束后，再选择一只当前可见且尚未
     * 介绍过的怪物。遗物靠近提示由渲染时动态查询，不需要保存计时。
     */
    private void updateHudContext() {
        if (monsterNoticeTurns > 0) {
            monsterNoticeTurns--;
            if (monsterNoticeTurns > 0) {
                return;
            }
            monsterNoticeType = null;
        }

        for (Monster monster : monsters) {
            if (visible[monster.x()][monster.y()]
                && !seenMonsterTypes.contains(monster.type())) {
                seenMonsterTypes.add(monster.type());
                monsterNoticeType = monster.type();
                monsterNoticeTurns = MONSTER_NOTICE_TURNS;
                return;
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

        // 新游戏开始时没有怪物，拾取遗物后再逐个激活。
        monsters.clear();
        nextMonsterId = 1;
        frozenMonsterTurns = 0;
        radarActive = false;
        seenMonsterTypes.clear();
        monsterNoticeType = null;
        monsterNoticeTurns = 0;

        // 初始化物品数量
        collectedCount = 0;

        // 初始化视野范围
        visionRadius = DEFAULT_VISION_RADIUS;

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
        PlayerData playerData = gamesave.playerData();

        // 旧存档没有生命值字段时，使用默认满生命值。
        boolean hasSavedHealth = playerData.maxHp() > 0;
        int maxHp = hasSavedHealth ? playerData.maxHp() : PLAYER_MAX_HP;
        int currentHp = hasSavedHealth
            ? Math.max(0, Math.min(playerData.currentHp(), maxHp))
            : maxHp;
        int shieldCharges = Math.max(0, playerData.shieldCharges());

        this.player = new Player(
            world,
            playerData.x(),
            playerData.y(),
            playerData.lastMoveDx(),
            playerData.lastMoveDy(),
            currentHp,
            maxHp,
            shieldCharges
        );

        // 加载物品
        this.items = new ArrayList<>();

        // 加载全部怪物；旧存档没有 monsters 字段时按空列表处理。
        monsters.clear();
        if (gamesave.monsters() != null) {
            for (MonsterData data : gamesave.monsters()) {
                monsters.add(data.toMonster());
            }
        }

        long minimumNextId = monsters.stream()
            .mapToLong(Monster::id)
            .max()
            .orElse(0L) + 1;
        nextMonsterId = Math.max(gamesave.nextMonsterId(), minimumNextId);

        // 恢复由 Engine 管理的 Buff 状态；旧存档缺少字段时使用 0 和 false。
        frozenMonsterTurns = Math.max(0, gamesave.frozenMonsterTurns());
        radarActive = gamesave.radarActive();
        seenMonsterTypes.clear();
        if (gamesave.seenMonsterTypes() != null) {
            seenMonsterTypes.addAll(gamesave.seenMonsterTypes());
        }
        monsterNoticeType = null;
        monsterNoticeTurns = 0;

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
        visionRadius = gamesave.visionRadius() > 0
            ? gamesave.visionRadius()
            : DEFAULT_VISION_RADIUS;

        // 加载已探索区域
        if (gamesave.explored() == null) {
            this.explored = new boolean[world.length][world[0].length];
        } else {
            this.explored = copyGrid(gamesave.explored());
        }

        // 加载玩家可视范围
        this.visible = new boolean[world.length][world[0].length];
        updateVision();
        updateHudContext();
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

        List<MonsterData> monsterDataList = new ArrayList<>();
        for (Monster monster : monsters) {
            monsterDataList.add(MonsterData.from(monster));
        }

        GameSave gameSave = new GameSave(
            new WorldData(toTileType(world)),
            new PlayerData(
                player.x(),
                player.y(),
                player.lastMoveDx(),
                player.lastMoveDy(),
                player.currentHp(),
                player.maxHp(),
                player.shieldCharges()
            ),
            itemDataList,
            monsterDataList,
            copyGrid(explored),
            visionRadius,
            random.state(),
            nextMonsterId,
            frozenMonsterTurns,
            radarActive,
            new ArrayList<>(seenMonsterTypes)
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

    public List<Monster> monsters() {
        return Collections.unmodifiableList(monsters);
    }

    public String seedString() {
        return seedString.toString();
    }

    public boolean shouldQuit() {
        return shouldQuit;
    }

    public int collectedCount() {
        return collectedCount;
    }

    public int visionRadius() {
        return visionRadius;
    }

    public int frozenMonsterTurns() {
        return frozenMonsterTurns;
    }

    public boolean radarActive() {
        return radarActive;
    }

    /** 返回当前首次进入视野、需要在 HUD 中介绍的怪物类型。 */
    public MonsterType monsterNoticeType() {
        return monsterNoticeType;
    }

    /**
     * 返回玩家附近最近的遗物；没有距离不超过 3 格的遗物时返回 null。
     */
    public Item nearbyItem() {
        Item nearest = null;
        int nearestDistance = ITEM_HINT_DISTANCE + 1;

        for (Item item : items) {
            int distance = Math.abs(item.x() - player.x())
                + Math.abs(item.y() - player.y());
            if (distance < nearestDistance) {
                nearest = item;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    public boolean[][] visible() {
        return visible;
    }

    public boolean[][] explored() {
        return explored;
    }
}
