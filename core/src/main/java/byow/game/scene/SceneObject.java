package byow.game.scene;

/** 独立于底层地面的功能性场景物件。 */
public class SceneObject {
    private final int x;
    private final int y;
    private final SceneObjectType type;
    private boolean active;
    private final int targetX;
    private final int targetY;

    public SceneObject(int x, int y, SceneObjectType type) {
        this(x, y, type, true, -1, -1);
    }

    public SceneObject(
        int x,
        int y,
        SceneObjectType type,
        boolean active,
        int targetX,
        int targetY
    ) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.active = active;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public static SceneObject portal(int x, int y, int targetX, int targetY) {
        return new SceneObject(x, y, SceneObjectType.PORTAL, true, targetX, targetY);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public SceneObjectType type() {
        return type;
    }

    public boolean active() {
        return active;
    }

    public int targetX() {
        return targetX;
    }

    public int targetY() {
        return targetY;
    }

    public boolean contains(int otherX, int otherY) {
        return active && x == otherX && y == otherY;
    }

    /** 一次性物件使用后不再参与渲染和交互。 */
    public void consume() {
        active = false;
    }
}
