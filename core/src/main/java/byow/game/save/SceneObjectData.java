package byow.game.save;

import byow.game.scene.SceneObject;
import byow.game.scene.SceneObjectType;

/** 场景物件的可序列化状态。 */
public record SceneObjectData(
    int x,
    int y,
    SceneObjectType type,
    boolean active,
    int targetX,
    int targetY
) {
    public static SceneObjectData from(SceneObject object) {
        return new SceneObjectData(
            object.x(), object.y(), object.type(), object.active(),
            object.targetX(), object.targetY()
        );
    }

    public SceneObject toSceneObject() {
        return new SceneObject(x, y, type, active, targetX, targetY);
    }
}
