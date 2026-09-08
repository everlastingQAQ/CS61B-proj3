package byow.game.tile;

import byow.game.random.GameRandom;

import java.awt.Color;
import java.util.Arrays;

/**
 * 表示地图中的一个图块，并保存图块数据。
 */
public class TETile {

    private final char character;

    private final Color textColor;
    private final Color backgroundColor;
    private final String description;

    /**
     * TETile 的完整构造函数。
     *
     * @param character
     *        显示在屏幕上的字符
     *
     * @param textColor
     *        字符本身的颜色（前景色）
     *
     * @param backgroundColor
     *        字符后面的背景颜色
     *
     * @param description
     *        对这个 tile 的描述。
     *        当鼠标悬停在 tile 上时，会在 GUI 中显示这个描述。
     *
     */
    public TETile(char character,
                  Color textColor,
                  Color backgroundColor,
                  String description
    ) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.description = description;
    }

    /**
     * 创建 TETile t 的一个副本，
     * 但是把它的文字颜色替换成给定的 textColor。
     *
     * @param t
     *        要复制的 tile
     *
     * @param textColor
     *        新 tile 的前景色 / 文字颜色
     */
    public TETile(TETile t, Color textColor) {
        this(t.character,
            textColor,
            t.backgroundColor,
            t.description);
    }


    /**
     * 返回这个 tile 的字符表示。
     *
     * 主要用于文本模式绘制。
     *
     * @return tile 对应的字符
     */
    public char character() {
        return character;
    }

    /**
     * 返回这个 tile 的描述。
     *
     * 这个描述可以用于：
     *
     * - 鼠标悬停时显示文字
     * - 测试两个 tile 是否表示同一种东西
     *
     * @return tile 的描述
     */
    public String description() {
        return description;
    }

    /**
     *  返回当前格子字符的颜色
     * */
    public Color textColor() {
        return textColor;
    }

    /**
     *  返回当前格子背景的颜色
     * */
    public Color backgroundColor() {
        return backgroundColor;
    }

    /**
     * 根据原颜色分量 v，
     * 在 ±dv 范围内随机生成一个新的颜色分量。
     */
    private static int newColorValue(
        int v,
        int dv,
        GameRandom r) {

        int rawNewValue =
            v + r.nextInt(-dv, dv + 1);

        /*
         * 确保颜色值不会超出合法范围：
         *
         * 0 <= value <= 255
         */
        int newValue =
            Math.clamp(rawNewValue, 0, 255);

        return newValue;
    }

    /**
     * 将一个二维 TETile 数组转换成 String。
     *
     * 这个方法非常适合调试。
     *
     * 注意：
     *
     * 在 tile rendering engine 中，
     * y = 0 实际上对应世界的最底部。
     *
     * 因此这里打印地图的时候，
     * y 必须从最大的值往 0 打印。
     *
     * 所以看起来打印顺序好像是“反过来的”。
     *
     * 这样最终终端里显示出来的地图，
     * 才和图形界面看到的是一致的。
     *
     * @param world
     *        要打印的二维世界
     *
     * @return
     *        世界对应的字符串表示
     */
    public static String toString(TETile[][] world) {

        int width = world.length;
        int height = world[0].length;

        StringBuilder sb =
            new StringBuilder();

        /*
         * 从地图顶部开始往下打印。
         */
        for (int y = height - 1;
             y >= 0;
             y -= 1) {

            /*
             * 从左往右打印。
             */
            for (int x = 0;
                 x < width;
                 x += 1) {

                if (world[x][y] == null) {

                    throw new IllegalArgumentException(
                        "Tile at position x="
                            + x
                            + ", y="
                            + y
                            + " is null."
                    );
                }

                sb.append(
                    world[x][y].character()
                );
            }

            sb.append('\n');
        }

        return sb.toString();
    }

    /**
     * 创建给定二维 tile 数组的一个副本。
     *
     * @param tiles
     *        要复制的二维数组
     *
     * @return
     *        复制出来的新二维数组
     */
    public static TETile[][] copyOf(
        TETile[][] tiles) {

        if (tiles == null) {
            return null;
        }

        /*
         * 创建一个新的二维数组。
         *
         * 第一维长度和原来的 tiles 一样。
         */
        TETile[][] copy =
            new TETile[tiles.length][];

        int i = 0;

        /*
         * 遍历每一列。
         */
        for (TETile[] column : tiles) {

            /*
             * 复制这一列。
             */
            copy[i] =
                Arrays.copyOf(
                    column,
                    column.length
                );

            i += 1;
        }

        return copy;
    }


}
