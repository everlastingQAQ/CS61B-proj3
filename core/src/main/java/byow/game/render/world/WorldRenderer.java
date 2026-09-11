package byow.game.render.world;

import byow.game.GameConfig;
import byow.game.item.Item;
import byow.game.player.Player;
import byow.game.render.FontManager;
import byow.game.item.Itemset;
import byow.game.tile.TETile;
import byow.game.tile.Tileset;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.util.*;

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

    private final OrthographicCamera camera;
    private final float tileSize;

    /**
     * 创建 WorldRenderer。
     *
     * @param shapeRenderer 用于绘制 tile 背景
     * @param batch 用于绘制 tile 字符
     * @param fonts 游戏使用的字体管理器
     * @param tileSize 一个 tile 的世界尺寸
     */
    public WorldRenderer(ShapeRenderer shapeRenderer,
                         SpriteBatch batch,
                         FontManager fonts,
                         float tileSize) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;

        this.camera = new OrthographicCamera();

        this.tileSize = tileSize;

        // 初始化 camera 能看到多大的世界区域。
        resize(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        this.tileRenderer = new TileRenderer(batch, fonts.tile(), shapeRenderer, tileSize);
    }

    /**
     * 渲染世界
     * @param world 当前世界
     * @param player 当前玩家
     */
    public void render(TETile[][] world, Player player, List<Item> items) {

        // 镜头跟随玩家
        updateCamera(world, player);

        // 先画背景
        // 背景跟着 camera.combined 移动
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                tileRenderer.drawBackground(world[x][y], x, y);
            }
        }
        shapeRenderer.end();

        // 再画字符
        // 字符跟着 camera 移动，
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                tileRenderer.drawCharacter(world[x][y], x, y);
            }
        }

        // 画物品的字符
        for (Item item : items) {
            TETile appearance = Itemset.get(item.type());
            tileRenderer.drawCharacter(appearance, item.x(), item.y());
        }

        // 画玩家
        tileRenderer.drawCharacter(Tileset.AVATAR, player.x(), player.y());

        batch.end();
    }

    /**
     * 设置 Camera 可视范围
     * @param width 窗口尺寸
     * @param height 窗口尺寸
     */
    public void resize(int width, int height) {
        float aspectRatio = (float)width / (float) height;

        // 设置 camera 纵向能够看到多少世界单位。
        camera.viewportHeight = GameConfig.CAMERA_VISIBLE_TILES_Y * tileSize;

        // 根据窗口宽高比计算横向视野。
        camera.viewportWidth = camera.viewportHeight * aspectRatio;

        // 更新 camera
        camera.update();
    }

    /**
     * 根据玩家当前位置更新 camera 视觉中心
     */
    private void updateCamera(TETile[][] world, Player player) {
        // 计算player所在中心位置
        float playerCenterX = (player.x() + 0.5f) * tileSize;
        float playerCenterY = (player.y() + 0.5f) * tileSize;

        // 计算camera半宽
        float halfWidth = camera.viewportWidth / 2;
        float halfHeight = camera.viewportHeight / 2;

        // 计算camera范围
        float worldWidth = world.length * tileSize;
        float worldHeight = world[0].length * tileSize;

        // 限定camera真正的位置
        float centerX = MathUtils.clamp(playerCenterX,halfWidth, worldWidth - halfWidth);
        float centerY = MathUtils.clamp(playerCenterY, halfHeight, worldHeight - halfHeight);

        // 把 camera 的中心位置设置为玩家中心位置。
        camera.position.set(centerX, centerY, 0);

        // 更新 camera
        camera.update();
    }
}
