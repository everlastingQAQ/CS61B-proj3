package byow.game.render.ui;

import byow.game.Engine;
import byow.game.item.Item;
import byow.game.item.ItemRules;
import byow.game.monster.Monster;
import byow.game.monster.MonsterType;
import byow.game.scene.SceneObject;
import byow.game.scene.SceneObjectType;

/** 为当前可见格生成鼠标悬停说明。 */
public final class HoverDescriptions {

    public static String forTile(Engine engine, int x, int y) {
        if (engine.player().x() == x && engine.player().y() == y) {
            return "玩家：当前生命 " + engine.player().currentHp()
                + " / " + engine.player().maxHp() + "。";
        }

        for (Monster monster : engine.monsters()) {
            if (monster.x() == x && monster.y() == y) {
                return monsterDescription(monster.type());
            }
        }

        for (Item item : engine.items()) {
            if (item.x() == x && item.y() == y) {
                return itemDescription(item);
            }
        }

        for (SceneObject object : engine.sceneObjects()) {
            if (object.contains(x, y)) {
                return sceneObjectDescription(object.type());
            }
        }

        if (engine.exit().contains(x, y)) {
            return engine.exit().active()
                ? "出口：已经激活，进入即可获胜。"
                : "出口：收集 3 件遗物后开启。";
        }

        return terrainDescription(engine.world()[x][y].description());
    }

    public static String itemDescription(Item item) {
        ItemRules.Rule rule = ItemRules.ruleFor(item.type());
        return item.type().displayName()
            + "：拾取会激活 " + rule.monsterType().displayName()
            + "；第 4、5 件会获得“" + rule.buffType().displayName() + "”增益。";
    }

    public static String monsterDescription(MonsterType type) {
        return switch (type) {
            case HUNTER -> "猎手：锁定玩家位置 2 回合并通过广度优先寻路追踪。";
            case AMBUSHER -> "伏击者：预测玩家移动方向前方最多 3 格。";
            case GUARDIAN -> "守卫：玩家靠近守护点时开始追击。";
            case PATROLLER -> "巡逻者：在两个远端点之间巡逻。";
            case COWARD -> "胆小鬼：靠近玩家时逃跑，拉开距离后追击。";
        };
    }

    public static String sceneObjectDescription(SceneObjectType type) {
        return switch (type) {
            case SPIKE_TRAP -> "尖刺陷阱：踩上去会失去 1 点生命。";
            case HEALING_FOUNTAIN -> "治愈泉：生命未满时恢复 1 点生命，只能使用一次。";
            case PORTAL -> "传送门：进入后传送到另一座传送门。";
            case BLESSING_CHEST -> "祝福宝箱：打开后随机获得一种增益，只能使用一次。";
        };
    }

    private static String terrainDescription(String description) {
        return switch (description) {
            case "floor" -> "地面：可以通行。";
            case "wall" -> "墙壁：不可通行，并会阻挡视野。";
            case "nothing" -> "虚空：不可通行。";
            case "grass" -> "草地：可以通行。";
            case "water" -> "水面：不可通行。";
            case "flower" -> "花地：可以通行。";
            case "locked door" -> "关闭的出口：尚未开启。";
            case "unlocked door" -> "开放的出口：可以通行。";
            case "sand" -> "沙地：可以通行。";
            case "moss" -> "苔藓地：可以通行的潮湿地面。";
            case "cracked floor" -> "裂纹地面：可以通行的破损地面。";
            case "mud" -> "泥地：可以通行的松软地面。";
            case "pebbles" -> "碎石地：可以通行的碎石地面。";
            case "mountain" -> "山地：不可通行。";
            case "tree" -> "树木：不可通行。";
            case "rock" -> "岩石：不可通行。";
            case "bridge" -> "桥梁：可以通行。";
            default -> "未知区域。";
        };
    }

    private HoverDescriptions() {}
}
