package byow.game.tile;

public enum TileType {
    AVATAR("avatar", true, true),
    WALL("wall", false, false),
    FLOOR("floor", true, true),
    NOTHING("nothing", false, true),
    GRASS("grass", true, true),
    WATER("water", false, true),
    FLOWER("flower", true, true),
    CLOSED_DOOR("locked door", false, false),
    OPEN_DOOR("unlocked door", true, true),
    SAND("sand", true, true),
    MOSS("moss", true, true),
    CRACKED_FLOOR("cracked floor", true, true),
    MUD("mud", true, true),
    PEBBLES("pebbles", true, true),
    MOUNTAIN("mountain", false, false),
    TREE("tree", false, false),
    ROCK("rock", false, false),
    BRIDGE("bridge", true, true);

    private final String description;
    private final boolean walkable;
    private final boolean transparent;

    TileType(String description, boolean walkable, boolean transparent) {
        this.description = description;
        this.walkable = walkable;
        this.transparent = transparent;
    }

    public String description() {
        return description;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public static TileType fromDescription(String description) {
        for (TileType type : values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown tile description: " + description);
    }
}
