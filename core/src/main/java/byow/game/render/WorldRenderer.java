package byow.game.render;

import byow.game.GameConfig;
import byow.game.player.Player;
import byow.game.tile.TETile;
import byow.game.tile.Tileset;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

    private final OrthographicCamera camera;
    private final float tileSize;

    /**
     * 初始化世界渲染器
     * @param shapeRenderer 传入背景渲染器, begin(), end()启用和关闭, 绘画背景时调用
     * @param batch 传入字符渲染器, begin(), end()启用和关闭, 绘画字符时调用
     * @param fonts 传入板块渲染器
     */
    public WorldRenderer(ShapeRenderer shapeRenderer,
                         SpriteBatch batch,
                         FontManager fonts,
                         float tileSize) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;

        this.camera = new OrthographicCamera();

        updateCameraViewport();

        this.tileRenderer = new TileRenderer(batch, fonts.tile(), shapeRenderer, tileSize);

        this.tileSize = tileSize;
    }

    /**
     * 渲染世界
     * @param world 传入需要渲染的世界
     */
    public void render(TETile[][] world, Player player) {

        // 镜头跟随玩家
        updateCamera(player);

        // 先画背景
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                tileRenderer.drawBackground(world[x][y], x, y);
            }
        }
        shapeRenderer.end();

        // 再画字符
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                tileRenderer.drawCharacter(world[x][y], x, y);
            }
        }

        // 画玩家
        tileRenderer.drawCharacter(Tileset.AVATAR, player.x(), player.y());
        batch.end();
    }

    private void updateCameraViewport() {
        float aspectRatio = (float)Gdx.graphics.getWidth() / Gdx.graphics.getHeight();

        camera.viewportHeight = GameConfig.CAMERA_VISIBLE_TILES_Y * tileSize;

        camera.viewportWidth = camera.viewportHeight * aspectRatio;

        camera.update();
    }

    private void updateCamera(Player player) {
        float playerCenterX = (player.x() + 0.5f) * tileSize;

        float playerCenterY = (player.y() + 0.5f) * tileSize;

        camera.position.set(playerCenterX, playerCenterY, 0);

        camera.update();
    }
}
