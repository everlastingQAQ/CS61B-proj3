package byow.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * 统一管理游戏中使用的所有字体。
 */
public class FontManager {
    /** 左下角日志会使用到的中文字符。 */
    private static final String HUD_MESSAGE_CHARACTERS =
        FreeTypeFontGenerator.DEFAULT_CHARS
            + "发现会锁定你的位置回合并通过追踪预测移动方向前方最多格"
            + "靠近守护点时开始在两个远端之间巡逻逃跑拉开距离后"
            + "附近有拾取激活第件获得：，；。、";

    // 标题字体。
    private final BitmapFont titleFont;

    // 普通 UI 字体。
    private final BitmapFont bodyFont;

    // 左下角中文日志字体。
    private final BitmapFont messageFont;

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

        // 中文说明使用 Noto Sans SC，专有名词仍使用英文。
        messageFont = createFont(
            "font/NotoSansSC-VF.ttf",
            22,
            HUD_MESSAGE_CHARACTERS
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
        return createFont(path, size, FreeTypeFontGenerator.DEFAULT_CHARS);
    }

    /**
     * 生成只包含指定字符的 UI 字体，避免把完整 CJK 字符集放进纹理。
     */
    private BitmapFont createFont(String path, int size, String characters) {

        // 创建 FreeType 字体生成器。
        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(
                resolveAsset(path)
            );

        // 创建字体生成参数对象。
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        // 设置实际生成字体的像素大小。
        parameter.size = size;
        parameter.characters = characters;

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
                resolveAsset(path)
            );

        // 创建 tile 字体的生成参数。
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        // 设置字体生成大小。
        parameter.size = size;

        // 指定需要真正生成到 BitmapFont 中的字符。
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "·≈❀█▢▒▲♠●◆";

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

    /**
     * 根据不同启动目录查找资源文件。
     *
     * Gradle 从 assets 目录运行时直接使用原路径；IntelliJ 从项目根目录
     * 运行时使用 assets 前缀，避免要求用户修改运行配置。
     *
     * @param path 相对于 assets 目录的资源路径
     * @return 可以读取的资源文件
     */
    private static FileHandle resolveAsset(String path) {
        // 优先使用 LibGDX 标准资源路径，兼容 Gradle 和打包运行。
        FileHandle file = Gdx.files.internal(path);
        if (file.exists()) {
            return file;
        }

        // IntelliJ 从项目根目录运行时，资源位于 assets 子目录。
        return Gdx.files.internal("assets/" + path);
    }

    public BitmapFont title() {
        return titleFont;
    }

    public BitmapFont body() {
        return bodyFont;
    }

    public BitmapFont message() {
        return messageFont;
    }

    public BitmapFont tile() {
        return tileFont;
    }

    public void dispose() {
        titleFont.dispose();
        bodyFont.dispose();
        messageFont.dispose();
        tileFont.dispose();
    }
}
