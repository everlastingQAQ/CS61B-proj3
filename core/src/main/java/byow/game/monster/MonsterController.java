package byow.game.monster;

import byow.game.monster.ai.AmbusherAI;
import byow.game.monster.ai.CowardAI;
import byow.game.monster.ai.GuardianAI;
import byow.game.monster.ai.HunterAI;
import byow.game.monster.ai.MonsterAI;
import byow.game.monster.ai.PatrollerAI;
import byow.game.pathfinder.Pathfinder;
import byow.game.player.Player;
import byow.game.tile.TETile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 统一计算所有怪物的行动意图，并解决怪物之间的移动冲突。 */
public class MonsterController {

    /**
     * 所有怪物先基于同一个回合快照决策，再统一执行没有冲突的移动。
     *
     * @param world 当前世界
     * @param monsters 当前全部怪物
     * @param player 当前玩家
     * @return 本回合需要由 Engine 继续结算的怪物事件
     */
    public MonsterTurnResult takeTurn(
        TETile[][] world,
        List<Monster> monsters,
        Player player
    ) {
        // 记录回合开始时的占位，保证所有怪物基于同一份位置快照行动。
        Set<Pathfinder.Position> occupiedAtStart = collectOccupiedPositions(monsters);
        List<MoveIntent> intents = new ArrayList<>();

        // 先让每只怪物独立计算下一步，此阶段不修改任何怪物的位置。
        for (Monster monster : monsters) {
            MonsterAI ai = createAI(monster.type());
            Pathfinder.Position from = positionOf(monster);
            Pathfinder.Position next = ai.decideNextStep(world, monster, player);
            intents.add(new MoveIntent(monster, from, next));
        }

        List<Monster> contacts = new ArrayList<>();
        Map<Pathfinder.Position, MoveIntent> winnerByDestination = new HashMap<>();

        // 逐个检查移动意图，并为每个合法目标格选出唯一获胜者。
        for (MoveIntent intent : intents) {
            Pathfinder.Position destination = intent.destination();

            // AI 没有找到可行路径时，怪物留在原地。
            if (destination == null) {
                continue;
            }

            // 怪物尝试进入玩家位置时产生接触事件，但不实际移动。
            if (destination.x() == player.x() && destination.y() == player.y()) {
                contacts.add(intent.monster());
                continue;
            }

            // 怪物不能原地移动，也不能进入回合开始时被其他怪物占据的位置。
            if (destination.equals(intent.origin())
                || occupiedAtStart.contains(destination)) {
                continue;
            }

            // 多只怪物选择同一目标格时，只保留优先级最高的移动意图。
            winnerByDestination.merge(
                destination,
                intent,
                MonsterController::higherPriority
            );
        }

        // 按稳定 ID 顺序执行获胜意图，使回合结果保持确定性。
        List<MoveIntent> winners = new ArrayList<>(winnerByDestination.values());
        winners.sort(Comparator.comparingLong(intent -> intent.monster().id()));
        for (MoveIntent winner : winners) {
            Pathfinder.Position destination = winner.destination();
            winner.monster().moveTo(destination.x(), destination.y());
        }

        // 按稳定 ID 排序接触事件，再交给 Engine 继续结算。
        contacts.sort(Comparator.comparingLong(Monster::id));
        return new MonsterTurnResult(contacts);
    }

    /**
     * 收集回合开始时的怪物占位，并验证怪物 ID 和位置没有重复。
     *
     * @param monsters 当前全部怪物
     * @return 回合开始时被怪物占据的位置
     */
    private static Set<Pathfinder.Position> collectOccupiedPositions(
        List<Monster> monsters
    ) {
        Set<Pathfinder.Position> occupied = new HashSet<>();
        Set<Long> ids = new HashSet<>();

        // 同时检查稳定 ID 和位置，避免在非法状态下继续解决移动冲突。
        for (Monster monster : monsters) {
            if (!ids.add(monster.id())) {
                throw new IllegalStateException("Duplicate monster id: " + monster.id());
            }
            Pathfinder.Position position = positionOf(monster);
            if (!occupied.add(position)) {
                throw new IllegalStateException(
                    "Multiple monsters occupy " + position
                );
            }
        }
        return occupied;
    }

    /**
     * 把怪物当前坐标转换为寻路模块使用的位置对象。
     *
     * @param monster 当前怪物
     * @return 怪物当前位置
     */
    private static Pathfinder.Position positionOf(Monster monster) {
        return new Pathfinder.Position(monster.x(), monster.y());
    }

    /**
     * 比较两个目标格相同的移动意图，稳定 ID 较小的怪物优先。
     *
     * @param first 第一个移动意图
     * @param second 第二个移动意图
     * @return 优先级较高的移动意图
     */
    private static MoveIntent higherPriority(MoveIntent first, MoveIntent second) {
        return first.monster().id() <= second.monster().id() ? first : second;
    }

    /**
     * 根据怪物类型创建本回合使用的无状态 AI。
     *
     * @param type 怪物类型
     * @return 与怪物类型对应的 AI
     */
    private static MonsterAI createAI(MonsterType type) {
        return switch (type) {
            case HUNTER -> new HunterAI();
            case AMBUSHER -> new AmbusherAI();
            case GUARDIAN -> new GuardianAI();
            case PATROLLER -> new PatrollerAI();
            case COWARD -> new CowardAI();
        };
    }

    /** 保存一只怪物本回合的起点和期望目标。 */
    private record MoveIntent(
        Monster monster,
        Pathfinder.Position origin,
        Pathfinder.Position destination
    ) {}
}
