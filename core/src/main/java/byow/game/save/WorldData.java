package byow.game.save;

import byow.game.tile.TileType;

/**
 * 保存 World 的相关数据
 *
 * @author everlasting
 *
 */
public record WorldData(TileType[][] world) {}
