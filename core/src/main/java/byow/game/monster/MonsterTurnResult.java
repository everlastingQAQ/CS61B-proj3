package byow.game.monster;

import java.util.List;

/** 一轮怪物行动产生、需要由 Engine 继续结算的游戏事件。 */
public record MonsterTurnResult(List<Monster> contacts) {
    public MonsterTurnResult {
        contacts = List.copyOf(contacts);
    }
}
