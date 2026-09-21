package byow.game.scene;

/** 地图上的功能性场景物件类型。 */
public enum SceneObjectType {
    SPIKE_TRAP("尖刺陷阱"),
    HEALING_FOUNTAIN("治愈泉"),
    PORTAL("传送门"),
    BLESSING_CHEST("祝福宝箱");

    private final String displayName;

    SceneObjectType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
