package byow.game.save;

/**
 * 保存 Player 的相关数据
 *
 * @author everlasting
 *
 */
public record PlayerData(
    int x,
    int y,
    int lastMoveDx,
    int lastMoveDy,
    int currentHp,
    int maxHp,
    int shieldCharges
) {}
