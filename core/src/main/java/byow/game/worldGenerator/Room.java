package byow.game.worldGenerator;

/**
 * 表示地图中的一个矩形房间。
 *
 * <p>房间由左下角坐标 {@code (x, y)}、宽度和高度确定。
 * 创建后房间的位置和大小不会发生变化。</p>
 *
 * @author everlasting
 */
public record Room(int x, int y, int width, int height) {

}
