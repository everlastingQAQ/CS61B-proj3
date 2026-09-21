package byow.game.render.world;

import byow.game.GameConfig;
import byow.game.GameExit;
import byow.game.item.Item;
import byow.game.monster.Monster;
import byow.game.monster.MonsterAppearance;
import byow.game.player.Player;
import byow.game.render.FontManager;
import byow.game.scene.SceneObject;
import byow.game.scene.SceneObjectAppearance;
import byow.game.item.Itemset;
import byow.game.tile.TETile;
import byow.game.tile.Tileset;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.util.*;

import static byow.game.GameConfig.HUD_HEIGHT;

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
    public void render(
        TETile[][] world,
        Player player,
        List<Monster> monsters,
        List<Item> items,
        List<SceneObject> sceneObjects,
        GameExit exit,
        boolean[][] visible
    ) {

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

        // 出口是独立的玩法对象：即使尚未收集足够遗物，也要在进入
        // 玩家视野后显式绘制，不能依赖它恰好仍保存在底层地块中。
        if (exit != null && visible[exit.x()][exit.y()]) {
            tileRenderer.drawCharacter(exit.appearance(), exit.x(), exit.y());
        }

        // 画功能性场景物件；被使用的一次性物件不再显示。
        for (SceneObject object : sceneObjects) {
            if (object.active() && visible[object.x()][object.y()]) {
                tileRenderer.drawCharacter(
                    SceneObjectAppearance.get(object.type()),
                    object.x(),
                    object.y()
                );
            }
        }

        // 画物品的字符
        for (Item item : items) {
            if (!visible[item.x()][item.y()]) {
                continue;
            }

            TETile appearance = Itemset.get(item.type());
            tileRenderer.drawCharacter(appearance, item.x(), item.y());
        }

        // 画所有当前可见的怪物
        for (Monster monster : monsters) {
            if (visible[monster.x()][monster.y()]) {
                TETile appearance = MonsterAppearance.get(monster.type());
                tileRenderer.drawCharacter(appearance, monster.x(), monster.y());
            }
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
     * 把鼠标屏幕坐标转换为当前可见的世界格。
     * 顶部 HUD 和迷雾中的格子不会触发悬停。
     */
    public GridPosition tileAtScreen(
        boolean[][] visible,
        int screenX,
        int screenY
    ) {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        int worldViewportHeight = screenHeight - HUD_HEIGHT;

        if (screenX < 0 || screenX >= screenWidth
            || screenY < HUD_HEIGHT || screenY >= screenHeight) {
            return null;
        }

        float mouseYFromBottom = screenHeight - screenY;
        float projectedTileSize = worldViewportHeight
            / (float) GameConfig.CAMERA_VISIBLE_TILES_Y;
        float halfTile = projectedTileSize / 2f;
        Vector3 tileCenter = new Vector3();

        // 只投影当前可见格。与实际渲染使用同一个 camera，可以避开
        // Wayland/高 DPI 环境中输入坐标反投影不一致的问题。
        for (int x = 0; x < visible.length; x++) {
            for (int y = 0; y < visible[0].length; y++) {
                if (!visible[x][y]) {
                    continue;
                }
                tileCenter.set(
                    (x + 0.5f) * tileSize,
                    (y + 0.5f) * tileSize,
                    0f
                );
                camera.project(
                    tileCenter,
                    0,
                    0,
                    screenWidth,
                    worldViewportHeight
                );
                if (Math.abs(screenX - tileCenter.x) <= halfTile
                    && Math.abs(mouseYFromBottom - tileCenter.y) <= halfTile) {
                    return new GridPosition(x, y);
                }
            }
        }
        return null;
    }

    public record GridPosition(int x, int y) {}

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
