package byow.game.tile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TileTypeTest {

    @Test
    void tileRulesUsePropertiesDeclaredByTileType() {
        for (TileType type : TileType.values()) {
            TETile tile = TileConverter.toTETile(type);
            assertEquals(type.isWalkable(), TileRules.isWalkable(tile));
            assertEquals(type.isTransparent(), TileRules.isTransparent(tile));
            assertEquals(type, TileConverter.toTileType(tile));
        }
    }

    @Test
    void decorativeGroundStaysWalkableAndTransparent() {
        for (TileType type : new TileType[] {
            TileType.GRASS,
            TileType.FLOWER,
            TileType.SAND,
            TileType.MOSS,
            TileType.CRACKED_FLOOR,
            TileType.MUD,
            TileType.PEBBLES
        }) {
            assertTrue(type.isWalkable());
            assertTrue(type.isTransparent());
        }

        assertFalse(TileType.WALL.isWalkable());
        assertFalse(TileType.WALL.isTransparent());
    }
}
