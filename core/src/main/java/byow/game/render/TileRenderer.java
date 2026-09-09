package byow.game.render;

import byow.game.tile.TETile;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

/**
 * 负责渲染一个板块
 * 1. 属性
 * - SpriteBatch 画字符时候调用
 * - BitmapFont 画字符
 * - shapeRenderer 画背景
 * - tileSize 板块像素
 * 2. 行为
 * - drawBackground(TETile tile, int x, int y) 画板块背景
 * - drawCharacter(TETile tile, int x, int y) 画板块字符
 * - toGdxColor 将板块的颜色转化成绘画颜色
 */
public class TileRenderer {

    private final SpriteBatch batch;
    private final BitmapFont tileFont;
    private final ShapeRenderer shapeRenderer;

    private final float tileSize;

    private final GlyphLayout layout = new GlyphLayout();

    /**
     * 初始化板块渲染器
     * @param batch 传入batch, 用于font绘画时调用
     * @param font 传入font, 用于绘画板块
     * @param shapeRenderer 传入shapeRenderer, 用于绘画背景, 绘画时候调用自己
     * @param tileSize 传入每一个板块占像素, 用于绘画计算位置
     */
    public TileRenderer(SpriteBatch batch,
                        BitmapFont font,
                        ShapeRenderer shapeRenderer,
                        float tileSize) {
        this.batch = batch;
        this.tileFont = font;
        this.shapeRenderer = shapeRenderer;
        this.tileSize = tileSize;
    }

    /**
     * 画板块的背景
     * @param tile 传入板块
     * @param x 传入板块横坐标
     * @param y 传入板块纵坐标
     */
    public void drawBackground(TETile tile, int x, int y) {
        float px = x * tileSize;
        float py = y * tileSize;

        // 设定绘画颜色
        shapeRenderer.setColor(toGdxColor(tile.backgroundColor()));

        // 调用自己绘画,  rect(左下角 x, 左下角 y, 宽度, 高度)
        shapeRenderer.rect(px, py, tileSize, tileSize);
    }

    /**
     * 画板块的符号
     * @param tile 传入板块
     * @param x 传入板块横坐标
     * @param y 传入板块纵坐标
     */
    public void drawCharacter(TETile tile, int x, int y) {
        // 描述该在哪里画
        float px = x * tileSize;
        float py = y * tileSize;

        // 设定颜色
        tileFont.setColor(toGdxColor(tile.textColor()));

        // 取得板块的字符
        String text = String.valueOf(tile.character());

        // 通过工具测量一段文字实际占多大空间
        layout.setText(tileFont, text);

        // 居中计算: 容器起点 + (容器大小 - 内容大小) / 2
        float textX = px + (tileSize - layout.width) / 2f;
        float textY = py + (tileSize + layout.height) / 2f;

        // 通过 draw(batch, 字符, 字符开始绘画横坐标, 字符baseline纵坐标)
        tileFont.draw(batch, text, textX, textY);
    }

    // 将TETile的颜色转化成BitmapFont的颜色单位
    private com.badlogic.gdx.graphics.Color toGdxColor(java.awt.Color color) {
        return new com.badlogic.gdx.graphics.Color(
            color.getRed() / 255f,
            color.getGreen() / 255f,
            color.getBlue() / 255f,
            color.getAlpha() / 255f
        );
    }
}
