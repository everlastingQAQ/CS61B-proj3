package byow.game.save;

/**
 * 保存 Player 的相关数据
 *
 * @author everlasting
 * */
public class PlayerData {
    private final int x;
    private final int y;

    public PlayerData(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }
}
