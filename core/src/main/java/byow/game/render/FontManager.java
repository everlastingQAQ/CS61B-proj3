package byow.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * 统一管理游戏中使用的所有字体。
 */
public class FontManager {
    private static final String UI_FONT_PATH = "font/FusionPixel12SC.ttf";

    /** 所有菜单、HUD 和悬停说明会使用到的中文字符。 */
    private static final String CHINESE_UI_CHARACTERS =
        FreeTypeFontGenerator.DEFAULT_CHARS
            + "地牢探险新游戏读取退出输入世界种子按开始返回暂停继续保存"
            + "主菜单前吗并不直接成功逃脱结束存档位选择覆盖已有空无法"
            + "发现会锁定你的位置回合并通过追踪预测移动方向前方最多格"
            + "靠近守护点时开始在两个远端之间巡逻逃跑拉开距离后广度优先寻路"
            + "附近有拾取激活第件获得增益：，；。、“”！？"
            + "玩家当前生命地面可以通行墙壁不可并阻挡视野虚空"
            + "关闭出口收集三遗物后开启已经进入即可获胜连接点"
            + "草地水面花沙山树木岩石桥梁尚未未知区域"
            + "猎手伏击者守卫巡逻胆小鬼尖刺陷阱踩上去失去点"
            + "治愈泉满时恢复只能使用一次传送门另一座祝福宝箱打开随机一种"
            + "苔藓潮湿裂纹破损泥松软碎石水晶金币钥匙宝珠符文"
            + "强化雷达护盾冻结状态无存档覆盖该已有选择读取"
            + "北南东西当前位置到怪放";

    // 标题字体。
    private final BitmapFont titleFont;

    // 普通 UI 字体。
    private final BitmapFont bodyFont;

    // 左下角中文日志字体。
    private final BitmapFont messageFont;

    // 地图 tile 专用字体。
    private final BitmapFont tileFont;

    public FontManager() {
        // 原字体以 12px 像素网格设计，使用整数倍字号避免边缘发糊。
        // 标题使用 4 倍字号，并用同色轮廓做轻度加粗。
        titleFont = createPixelFont(48, 1.25f);

        // 菜单和 HUD 使用 2 倍字号，保持字体原始字重。
        bodyFont = createPixelFont(24, 0f);

        // 情境日志和悬停说明同样使用 24px，避免非整数倍缩放。
        messageFont = createPixelFont(24, 0f);

        // 生成地图 tile 字体。
        tileFont = createTileFont(
            "font/NotoSansSymbols2-Regular.ttf",
            16
        );
    }

    /**
     * 按字体原生 12px 网格生成像素 UI 字体。
     *
     * 同色轮廓只用于标题，相当于轻度加粗；正文不做轮廓处理，以免
     * 小字号笔画粘连。最近邻过滤保证窗口缩放时仍保持硬朗像素边缘。
     */
    private BitmapFont createPixelFont(int size, float boldWidth) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            resolveAsset(UI_FONT_PATH)
        );
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = size;
        parameter.characters = CHINESE_UI_CHARACTERS;
        parameter.mono = true;
        parameter.hinting = FreeTypeFontGenerator.Hinting.None;
        parameter.kerning = true;
        parameter.borderWidth = boldWidth;
        parameter.borderColor = Color.WHITE;
        parameter.borderStraight = true;
        parameter.minFilter = Texture.TextureFilter.Nearest;
        parameter.magFilter = Texture.TextureFilter.Nearest;

        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
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
