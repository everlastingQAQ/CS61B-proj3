package byow.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * 统一管理游戏中使用的所有字体。
 */
public class FontManager {

    // 标题字体。
    private final BitmapFont titleFont;

    // 普通 UI 字体。
    private final BitmapFont bodyFont;

    // 地图 tile 专用字体。
    private final BitmapFont tileFont;

    public FontManager() {
        // 标题字体。
        titleFont = createFont(
            "font/JetBrainsMono-Medium.ttf",
            40
        );

        // 生成普通 UI 字体。
        bodyFont = createFont(
            "font/JetBrainsMonoNL-Regular.ttf",
            24
        );

        // 生成地图 tile 字体。
        tileFont = createTileFont(
            "font/NotoSansSymbols2-Regular.ttf",
            16
        );
    }

    /**
     * 根据 TTF 文件和字号生成普通 BitmapFont。
     *
     * 主要用于 titleFont 和 bodyFont。
     *
     * @param path TTF 字体在 assets 中的路径
     * @param size 需要生成的字体像素大小
     * @return 可以直接用于 SpriteBatch 绘制的 BitmapFont
     */
    private BitmapFont createFont(String path, int size) {

        // 创建 FreeType 字体生成器。
        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(
                Gdx.files.internal(path)
            );

        // 创建字体生成参数对象。
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        // 设置实际生成字体的像素大小。
        parameter.size = size;

        // 设置字体纹理缩小时的过滤方式。
        //
        // Linear 会在像素之间做插值，
        // 因此边缘比较平滑。
        parameter.minFilter = Texture.TextureFilter.Linear;

        // 设置字体纹理放大时的过滤方式。
        //
        // Linear 放大时会进行平滑插值，
        // 比 Nearest 更柔和。
        parameter.magFilter = Texture.TextureFilter.Linear;

        // 根据上面的参数真正生成 BitmapFont。
        BitmapFont font = generator.generateFont(parameter);

        // 立即释放 generator 占用的 native 资源
        generator.dispose();

        // 返回生成好的 BitmapFont。
        return font;
    }

    /**
     * 生成地图 tile 专用字体。
     *
     * 和普通字体最大的区别是：
     * 这里显式指定了需要生成到字体纹理中的字符。
     *
     * @param path TTF 字体文件路径
     * @param size 字体像素大小
     * @return tile 渲染使用的 BitmapFont
     */
    private BitmapFont createTileFont(String path, int size) {

        // 读取 tile 专用 TTF 字体
        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(
                Gdx.files.internal(path)
            );

        // 创建 tile 字体的生成参数。
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        // 设置字体生成大小。
        parameter.size = size;

        // 指定需要真正生成到 BitmapFont 中的字符。
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "·≈❀█▢▒▲♠●";

        // 设置字体纹理缩小时的过滤方式。
        parameter.minFilter = Texture.TextureFilter.Nearest;

        // 设置字体纹理放大时的过滤方式。
        parameter.magFilter = Texture.TextureFilter.Nearest;

        // 根据配置生成真正供 TileRenderer 使用的 BitmapFont。
        BitmapFont font = generator.generateFont(parameter);

        // 立即释放 generator 占用的 native 资源
        generator.dispose();

        // 返回生成好的 BitmapFont。
        return font;
    }

    public BitmapFont title() {
        return titleFont;
    }

    public BitmapFont body() {
        return bodyFont;
    }

    public BitmapFont tile() {
        return tileFont;
    }

    public void dispose() {
        titleFont.dispose();
        bodyFont.dispose();
        tileFont.dispose();
    }
}
