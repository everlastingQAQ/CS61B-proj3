package byow.game.render;

import byow.game.tile.TETile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static byow.game.GameConfig.WORLD_WIDTH;

/**
 * 负责渲染一个世界
 * 1. 属性
 * - SpriteBatch 字符渲染器
 * - ShapeRenderer 背景渲染器
 * - TileRenderer 板块渲染器
 * 2. 行为
 * - render(TETile[][] world) 渲染世界
 */

public class WorldRenderer {
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final TileRenderer tileRenderer;

    /**
     * 初始化世界渲染器
     * @param shapeRenderer 传入背景渲染器, begin(), end()启用和关闭, 绘画背景时调用
     * @param batch 传入字符渲染器, begin(), end()启用和关闭, 绘画字符时调用
     * @param tileRenderer 传入板块渲染器
     */
    public WorldRenderer(ShapeRenderer shapeRenderer,
                         SpriteBatch batch,
                         TileRenderer tileRenderer) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.tileRenderer = tileRenderer;
    }

    public WorldRenderer(ShapeRenderer shapeRenderer,
                         SpriteBatch batch,
                         BitmapFont font,
                         float tileSize) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;

        // 计算世界横向占据空间
        float worldPixelWidth = WORLD_WIDTH * tileSize;

        // 计算世界横向偏移量, 让世界水平居中
        float offsetX = (Gdx.graphics.getWidth() - worldPixelWidth) / 2f;

        this.tileRenderer = new TileRenderer(batch, font, shapeRenderer, tileSize, offsetX);
    }

    /**
     * 渲染世界
     * @param world 传入需要渲染的世界
     */
    public void render(TETile[][] world) {
        // 先画背景
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                tileRenderer.drawBackground(world[x][y], x, y);
            }
        }
        shapeRenderer.end();

        // 再画字符
        batch.begin();
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                tileRenderer.drawCharacter(world[x][y], x, y);
            }
        }
        batch.end();
    }
}
