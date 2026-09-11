package byow.game.render;

import byow.game.Engine;
import byow.game.render.ui.*;
import byow.game.render.world.WorldRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;

import static byow.game.GameConfig.*;

public class GameRenderer {

    private final MenuRenderer menuRenderer;
    private final SeedRenderer seedRenderer;
    private final WorldRenderer worldRenderer;
    private final PauseRenderer pauseRenderer;
    private final SaveRenderer saveRenderer;
    private final LoadRenderer loadRenderer;
    private final ConfirmQuitRenderer confirmQuitRenderer;
    private final HudRenderer hudRenderer;

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera uiCamera;

    public GameRenderer(
        SpriteBatch batch,
        FontManager fonts,
        ShapeRenderer shapeRenderer,
        float tileSize
    ) {
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;

        this.uiCamera = new OrthographicCamera();

        // 初始化 UI camera 的 viewport。用GameConfig中的配置
        updateUiCamera(WINDOW_WIDTH, WINDOW_HEIGHT);

        this.menuRenderer = new MenuRenderer(batch, fonts, shapeRenderer);
        this.seedRenderer = new SeedRenderer(batch, fonts, shapeRenderer);
        this.worldRenderer = new WorldRenderer(shapeRenderer, batch, fonts, tileSize);
        this.pauseRenderer = new PauseRenderer(batch, fonts);
        this.saveRenderer = new SaveRenderer(batch, fonts);
        this.loadRenderer = new LoadRenderer(batch, fonts);
        this.confirmQuitRenderer = new ConfirmQuitRenderer(batch, fonts);
        this.hudRenderer = new HudRenderer(batch, fonts, shapeRenderer);
    }

    public void render(Engine engine) {
        switch (engine.state()) {
            case PLAYING -> {
                useWorldViewPoint();
                worldRenderer.render(engine.world(), engine.player(), engine.items());

                useFullViewport();
                useUiCamera();
                hudRenderer.render(engine.CollectedCount(), ITEM_NUMBER);
            }

            case MENU -> {
                useFullViewport();
                useUiCamera();
                menuRenderer.render();
            }
            case SEED -> {
                useFullViewport();
                useUiCamera();
                seedRenderer.render(engine.seedString());
            }
            case PAUSE -> {
                useFullViewport();
                useUiCamera();
                pauseRenderer.render();
            }
            case SAVE -> {
                useFullViewport();
                useUiCamera();
                saveRenderer.render();
            }
            case LOAD -> {
                useFullViewport();
                useUiCamera();
                loadRenderer.render();
            }
            case CONFIRM_QUIT -> {
                useFullViewport();
                useUiCamera();
                confirmQuitRenderer.render();
            }

        }
    }

    // =====================
    // Camera用来改变变量的可视范围和视觉中心
    // =====================

    /**
     * 更新 UI 摄像机
     * @param width 窗口尺寸
     * @param height 窗口尺寸
     */
    private void updateUiCamera(int width, int height) {
        // 将 UI camera 设置成 2D 坐标系形式。
        uiCamera.setToOrtho(false, width, height);

        // 更新 uiCamera
        uiCamera.update();
    }

    /**
     * 将渲染坐标系切换为 UI 坐标系, 提供一个固定于屏幕的坐标系，所以 UI 不会随着 world camera / 玩家位置变化。
     */
    private void useUiCamera() {
        // 绘制的文字使用 UI 坐标系，
        batch.setProjectionMatrix(uiCamera.combined);

        // 背景也使用 UI 坐标系
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
    }

    /**
     * 修改两个Camera的尺寸, 当存在窗口大小改变的时候自动被调用
     */
    public void resize(int width, int height) {
        updateUiCamera(width, height);

        int worldHeight = height - HUD_HEIGHT;
        worldRenderer.resize(width, worldHeight);
    }

    // =====================
    // Viewport是改变渲染区域的函数, 设计让hud区域和世界区域分开
    // =====================

    /**
     *  调整渲染区域, 仅渲染hud以下区域
     */
    private void useWorldViewPoint() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        int worldHeight = height - HUD_HEIGHT;

        HdpiUtils.glViewport(0, 0, width, worldHeight);
    }

    /**
     * 调整渲染区域为全剧
     */
    private void useFullViewport() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        HdpiUtils.glViewport(0, 0, width, height);
    }
}
